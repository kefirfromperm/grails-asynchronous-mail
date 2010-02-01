package ru.perm.kefir.asynchronousmail

import org.springframework.mail.MailException
import org.springframework.mail.MailParseException
import org.grails.mail.MailService
import org.springframework.mail.MailMessage
import org.springframework.mail.MailPreparationException
import org.springframework.mail.MailAuthenticationException

/** Sent asynchronous messages       */
class AsynchronousMailJob {
    static triggers = {}
    def concurrent = false;

    // Dependency injection
    MailService mailService;

    def execute(context) {
        log.trace('Enter to execute method');

        // Get messages from DB
        def messages = AsynchronousMailMessage.withCriteria {
            Date now = new Date();
            lt('beginDate', now);
            gt('endDate', now);
            or {
                eq('status', MessageStatus.CREATED);
                eq('status', MessageStatus.ATTEMPTED);
            }
            order('endDate', 'asc');
            order('attemptsCount', 'asc');
            order('beginDate', 'asc');
            maxResults(context.mergedJobDataMap.get('messagesAtOnce'));
        }

        // Send each message and save new status
        try {
            messages.each {AsynchronousMailMessage message ->
                log.trace("Found message: " + message.toString());

                Date now = new Date();
                Date attemptDate = new Date(now.getTime() - message.attemptInterval);
                if (
                    message.status == MessageStatus.CREATED
                            || (message.status == MessageStatus.ATTEMPTED && message.lastAttemptDate.before(attemptDate))
                ) {
                    try {
                        message.sentDate = now;
                        message.lastAttemptDate = now;
                        message.attemptsCount++;
                        if (message.attemptsCount < message.maxAttemptsCount || message.maxAttemptsCount==0) {
                            message.status = MessageStatus.ATTEMPTED;
                        } else {
                            message.status = MessageStatus.ERROR;
                        }

                        try {
                            sendMessage(message);
                            message.status = MessageStatus.SENT;
                        } catch (MailException e) {
                            log.warn("Attempt to send message with id=${message.id}", e);
                            if (e instanceof MailParseException || e instanceof MailPreparationException) {
                                message.status = MessageStatus.ERROR;
                            } else if (e instanceof MailAuthenticationException) {
                                throw e;
                            }
                        }
                    } finally {
                        message.save(flush: true);
                    }
                }
            }
        } catch (Exception e) {
            log.warn('Abort mail sent', e);
        }
    }

    /** Send message by SMTP  */
    private MailMessage sendMessage(AsynchronousMailMessage message) {
        return mailService.sendMail {
            if(message.attachments){
                multipart true;
            }
            to message.to;
            subject message.subject;
            if (message.headers && !message.headers.isEmpty()) {
                headers message.headers;
            }
            if (message.html) {
                html message.text;
            } else {
                body message.text;
            }
            if (message.bcc && !message.bcc.isEmpty()) {
                bcc message.bcc;
            }
            if (message.cc && !message.cc.isEmpty()) {
                cc message.cc;
            }
            if (message.replyTo) {
                replyTo message.replyTo;
            }
            if (message.from) {
                from message.from;
            }
            message.attachments.each {AsynchronousMailAttachment attachment->
                attachBytes attachment.attachmentName, attachment.mimeType, attachment.content                
            }
        }
    }
}

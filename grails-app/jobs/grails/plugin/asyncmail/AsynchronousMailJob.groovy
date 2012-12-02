package grails.plugin.asyncmail

import grails.plugin.mail.MailService
import org.springframework.mail.*

/** Sent asynchronous messages         */
class AsynchronousMailJob {
    def concurrent = false
    def group = "AsynchronousMail"

    def getTriggers() {
        if (!config.asynchronous.mail.disable) {
            return {
                simple([repeatInterval: (Long) config.asynchronous.mail.send.repeat.interval])
            }
        }
        return {}
    }

    // Dependency injection
    MailService nonAsynchronousMailService

    def execute(context) {
        log.trace('Enter to execute method')

        // Get messages from DB
        def messages = AsynchronousMailMessage.withCriteria {
            Date now = new Date()
            lt('beginDate', now)
            gt('endDate', now)
            or {
                eq('status', MessageStatus.CREATED)
                eq('status', MessageStatus.ATTEMPTED)
            }
            order('priority', 'desc')
            order('endDate', 'asc')
            order('attemptsCount', 'asc')
            order('beginDate', 'asc')
            maxResults((int) config.asynchronous.mail.messages.at.once)
        }

        // Send each message and save new status
        try {
            messages.each {AsynchronousMailMessage message ->
                log.trace("Found message: " + message.toString())

                Date now = new Date()
                Date attemptDate = new Date(now.getTime() - message.attemptInterval)
                if (
                        message.status == MessageStatus.CREATED
                                || (message.status == MessageStatus.ATTEMPTED && message.lastAttemptDate.before(attemptDate))
                ) {
                    message.lastAttemptDate = now
                    message.attemptsCount++

                    // It guarantee that e-mail can't be sent more than 1 time
                    message.status = MessageStatus.ERROR
                    message.save(flush: true)

                    // Attempt to send
                    try {
                        sendMessage(message)
                        message.sentDate = now
                        message.status = MessageStatus.SENT
                    } catch (MailException e) {
                        log.warn("Attempt to send message with id=${message.id} is fail.", e)
                        if (message.attemptsCount < message.maxAttemptsCount &&
                                !(e instanceof MailParseException || e instanceof MailPreparationException)
                        ) {
                            message.status = MessageStatus.ATTEMPTED
                        }

                        if (e instanceof MailAuthenticationException) {
                            throw e
                        }
                    } finally {
                        message.save(flush: true)
                    }

                    // Delete message if it sent successfully and can be deleted
                    if (message.status == MessageStatus.SENT && message.markDelete) {
                        message.delete()
                    }
                }
            }
        } catch (Exception e) {
            log.error('Abort mail sent.', e)
        }
    }

    /** Send message by SMTP    */
    private MailMessage sendMessage(AsynchronousMailMessage message) {
        return nonAsynchronousMailService.sendMail {
            if (message.attachments) {
                multipart true
            }
            to message.to
            subject message.subject
            if (message.headers && !message.headers.isEmpty() && isMimeCapable()) {
                headers message.headers
            }
            if (message.html && isMimeCapable()) {
                html message.text
            } else {
                body message.text
            }
            if (message.bcc && !message.bcc.isEmpty()) {
                bcc message.bcc
            }
            if (message.cc && !message.cc.isEmpty()) {
                cc message.cc
            }
            if (message.replyTo) {
                replyTo message.replyTo
            }
            if (message.from) {
                from message.from
            }
            if (isMimeCapable()) {
                message.attachments.each {AsynchronousMailAttachment attachment ->
                    if (!attachment.inline) {
                        attachBytes attachment.attachmentName, attachment.mimeType, attachment.content
                    } else {
                        inline attachment.attachmentName, attachment.mimeType, attachment.content
                    }
                }
            }
        }
    }
}

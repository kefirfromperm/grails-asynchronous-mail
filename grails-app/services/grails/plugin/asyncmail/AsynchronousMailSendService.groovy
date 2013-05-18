package grails.plugin.asyncmail

import grails.plugin.mail.MailService
import org.springframework.mail.MailMessage

class AsynchronousMailSendService {
    static transactional = false

    MailService nonAsynchronousMailService

    MailMessage send(AsynchronousMailMessage message) {
        return nonAsynchronousMailService.sendMail {
            if (message.attachments) {
                multipart true
            }
            if(message.to && !message.to.isEmpty()){
                to message.to
            }
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

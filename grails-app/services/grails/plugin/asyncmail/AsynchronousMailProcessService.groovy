package grails.plugin.asyncmail

import org.springframework.mail.*
import groovyx.gpars.GParsPool

class AsynchronousMailProcessService {
    def grailsApplication

    def asynchronousMailPersistenceService
    def asynchronousMailSendService

    public void findAndSendEmails() {
        // Get messages from DB
        def messagesIds = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        Integer gparsPoolSize = grailsApplication.config.asynchronous.mail.gparsPoolSize

        // Send each message and save new status
        try {
            GParsPool.withPool(gparsPoolSize) {
                messagesIds.eachParallel {Long messageId ->
                    AsynchronousMailMessage.withNewSession { session ->
                        processEmailMessage(messageId)
                    }
                }
            }
        } catch (Exception e) {
            log.error('Abort mail sent.', e)
        }
    }

    void processEmailMessage(long messageId) {
        boolean useFlushOnSave = grailsApplication.config.asynchronous.mail.useFlushOnSave

        def message = asynchronousMailPersistenceService.getMessage(messageId)
        log.trace("Found a message: " + message.toString())

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
            asynchronousMailPersistenceService.save(message, useFlushOnSave)

            // Attempt to send
            try {
                log.trace("Attempt to send the message with id=${message.id}.")
                asynchronousMailSendService.send(message)
                message.sentDate = now
                message.status = MessageStatus.SENT
                log.trace("The message with id=${message.id} was sent successfully.")
            } catch (MailException e) {
                log.warn("Attempt to send the message with id=${message.id} was failed.", e)
                if (message.attemptsCount < message.maxAttemptsCount &&
                        !(e instanceof MailParseException || e instanceof MailPreparationException)
                ) {
                    message.status = MessageStatus.ATTEMPTED
                }

                if (e instanceof MailAuthenticationException) {
                    throw e
                }
            } finally {
                asynchronousMailPersistenceService.save(message, useFlushOnSave)
            }

            // Delete message if it is sent successfully and can be deleted
            if (message.status == MessageStatus.SENT && message.markDelete) {
                long id = message.id
                asynchronousMailPersistenceService.delete(message);
                log.trace("The message with id=${id} was deleted.")
            }
        }
    }
}

package grails.plugin.asyncmail

import org.codehaus.groovy.grails.support.PersistenceContextInterceptor
import org.springframework.mail.*
import groovyx.gpars.GParsPool

class AsynchronousMailProcessService {
    static transactional = false

    def grailsApplication
    PersistenceContextInterceptor persistenceInterceptor

    def asynchronousMailPersistenceService
    def asynchronousMailSendService

    public void findAndSendEmails() {
        // Get messages from DB
        def messagesIds = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        // Send each message and save new status
        Integer gparsPoolSize = grailsApplication.config.asynchronous.mail.gparsPoolSize

        // Send each message and save new status
        GParsPool.withPool(gparsPoolSize) {
            messagesIds.eachParallel { Long messageId ->
                try {
                    persistenceInterceptor.init()
                    log.debug('Open a new persistence session.')
                    try {
                        processEmailMessage(messageId)
                        try {
                            persistenceInterceptor.flush()
                            persistenceInterceptor.clear()
                            log.debug('Flush the persistence session.')
                        } catch (Exception e) {
                            log.error("Failed to flush the persistence session.", e)
                        }
                    } finally {
                        try {
                            persistenceInterceptor.destroy();
                            log.debug('Destroy the persistence session.')
                        } catch (Exception e) {
                            log.error("Failed to finalize the persistence session after message sent.", e);
                        }
                    }
                } catch (Exception e) {
                    log.error('Abort mail sent.', e)
                }
            }
        }
    }

    void processEmailMessage(long messageId) {
        boolean useFlushOnSave = grailsApplication.config.asynchronous.mail.useFlushOnSave

        def message = asynchronousMailPersistenceService.getMessage(messageId)
        log.trace("Found a message: " + message.toString())

        Date now = new Date()
        Date attemptDate = new Date(now.getTime() - message.attemptInterval)
        boolean canAttempt = message.status == MessageStatus.ATTEMPTED && message.lastAttemptDate.before(attemptDate)
        if (message.status == MessageStatus.CREATED || canAttempt) {
            message.lastAttemptDate = now
            message.attemptsCount++

            // Guarantee that e-mail can't be sent more than 1 time
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
                canAttempt = message.attemptsCount < message.maxAttemptsCount
                boolean isParseOrPrepException = e instanceof MailParseException || e instanceof MailPreparationException
                if (canAttempt && !isParseOrPrepException) {
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

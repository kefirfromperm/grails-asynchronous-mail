package grails.plugin.asyncmail

import org.springframework.mail.*
import groovyx.gpars.GParsPool

class AsynchronousMailPersistenceService {
    def grailsApplication
    def asynchronousMailSendService

    private AsynchronousMailMessage save(AsynchronousMailMessage message, boolean flush = false) {
        return message.save(flush: flush)
    }

    void delete(AsynchronousMailMessage message) {
        message.delete()
    }

    List<Long> selectMessagesIdsForSend(){
        return AsynchronousMailMessage.withCriteria {
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
            maxResults((int) grailsApplication.config.asynchronous.mail.messages.at.once)
            projections {
                property('id')
            }
        }
    }

    public void findAndSendEmails() {
        // Get messages from DB
        def messagesIds = this.selectMessagesIdsForSend()

        boolean useFlushOnSave = grailsApplication.config.asynchronous.mail.useFlushOnSave
        Integer gparsPoolSize = grailsApplication.config.asynchronous.mail.gparsPoolSize

        // Send each message and save new status
        try {
            GParsPool.withPool(gparsPoolSize) {
                messagesIds.eachParallel { messageId ->
                    AsynchronousMailMessage.withNewSession { session ->
                        this.processEmailMessage(messageId, useFlushOnSave)
                    }
                }
            }
        } catch (Exception e) {
            log.error('Abort mail sent.', e)
        }
    }

    private void processEmailMessage(Long messageId, boolean useFlushOnSave) {
        def message = AsynchronousMailMessage.get(messageId)

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
            this.save(message, useFlushOnSave)

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
                this.save(message, useFlushOnSave)
            }

            // Delete message if it is sent successfully and can be deleted
            if (message.status == MessageStatus.SENT && message.markDelete) {
                long id = message.id
                this.delete(message);
                log.trace("The message with id=${id} was deleted.")
            }
        }
    }
}

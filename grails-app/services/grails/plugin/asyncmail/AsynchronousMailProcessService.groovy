package grails.plugin.asyncmail

import grails.async.Promises
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.asyncmail.enums.MessageStatus
import org.springframework.mail.MailException
import org.springframework.mail.MailParseException
import org.springframework.mail.MailPreparationException

import java.util.concurrent.ArrayBlockingQueue

import static grails.async.Promises.task

class AsynchronousMailProcessService implements GrailsConfigurationAware {
    static transactional = false

    Config configuration

    def asynchronousMailPersistenceService
    def asynchronousMailSendService

    void findAndSendEmails() {
        // Get messages from DB
        List<Long> messagesIds = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        if(messagesIds) {
            // Create the queue of ids for processing
            int messageCount = messagesIds.size()
            Queue<Long> idsQueue = new ArrayBlockingQueue<Long>(messageCount, false, messagesIds)

            // Create some parallel tasks
            def promises = []
            int taskCount = Math.min(configuration.asynchronous.mail.taskPoolSize ?: 1, messageCount)
            for (int i = 0; i < taskCount; i++) {
                promises << task {
                    Long messageId
                    while ((messageId = idsQueue.poll()) != null) {
                        AsynchronousMailMessage.withNewSession {
                            try {
                                processEmailMessage(messageId)
                            } catch (Exception e) {
                                log.error(
                                        "An exception was thrown when attempting to send a message with id=${messageId}.",
                                        e
                                )
                            }
                        }
                    }
                }
            }

            // To prevent concurrent job execution we have to wait for all tasks when they will send all messages
            Promises.waitAll(promises)
        }
    }

    void processEmailMessage(long messageId) {
        boolean useFlushOnSave = configuration.asynchronous.mail.useFlushOnSave

        AsynchronousMailMessage message = asynchronousMailPersistenceService.getMessage(messageId)
        log.trace("Found a message: " + message.toString())

        Date now = new Date()
        Date attemptDate = new Date(now.getTime() - message.attemptInterval)
        boolean canAttempt = message.hasAttemptedStatus() && message.lastAttemptDate.before(attemptDate)
        if (message.hasCreatedStatus() || canAttempt) {
            message.lastAttemptDate = now
            message.attemptsCount++

            // Guarantee that e-mail can't be sent more than 1 time
            message.status = MessageStatus.ERROR
            asynchronousMailPersistenceService.save(message, useFlushOnSave)

            // Attempt to send
            try {
                log.trace("An attempt to send the message with id=${message.id}.")
                asynchronousMailSendService.send(message)
                message.sentDate = now
                message.status = MessageStatus.SENT
                log.trace("The message with id=${message.id} was sent successfully.")
            } catch (MailException e) {
                log.warn("An attempt to send the message with id=${message.id} was failed.", e)
                canAttempt = message.attemptsCount < message.maxAttemptsCount
                boolean fatalException = e instanceof MailParseException || e instanceof MailPreparationException
                if (canAttempt && !fatalException) {
                    message.status = MessageStatus.ATTEMPTED
                }
            } finally {
                asynchronousMailPersistenceService.save(message, useFlushOnSave)
            }

            // Delete message if it is sent successfully and can be deleted
            long id = message.id
            if (message.hasSentStatus() && message.markDelete) {
                asynchronousMailPersistenceService.delete(message)
                log.trace("The message with id=${id} was deleted.")
            } else if (message.hasSentStatus() && message.markDeleteAttachments) {
                asynchronousMailPersistenceService.deleteAttachments(message)
                log.trace("The message with id=${id} had all its attachments deleted.")
            } else {
                log.trace("The message with id=${id} will not be deleted.")
            }
        }
    }
}

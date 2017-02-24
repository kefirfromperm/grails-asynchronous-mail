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
    Config configuration

    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailSendService asynchronousMailSendService

    void findAndSendEmails() {
        // Get messages from DB
        List<Long> messagesIds = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        if (messagesIds) {
            log.debug("Found ${messagesIds} messages to send.")

            // Create the queue of ids for processing
            int messageCount = messagesIds.size()
            Queue<Long> idsQueue = new ArrayBlockingQueue<Long>(messageCount, false, messagesIds)

            // Create some parallel tasks
            def promises = []
            int taskCount = Math.min(configuration.asynchronous.mail.taskPoolSize ?: 1, messageCount)

            log.debug("Starts $taskCount send tasks.")

            for (int i = 0; i < taskCount; i++) {
                promises << task {
                    AsynchronousMailMessage.withNewSession {
                        Long messageId
                        while ((messageId = idsQueue.poll()) != null) {
                            try {
                                processEmailMessage(messageId)
                            } catch (Exception e) {
                                log.error(
                                        "An exception was thrown when attempt to send a message with id=${messageId}.",
                                        e
                                )
                            }
                        }
                    }
                }
            }

            // To prevent concurrent job execution we have to wait for all tasks when they will send all messages
            Promises.waitAll(promises)
            log.debug("$taskCount tasks are completed.")
        } else {
            log.trace("Messages to send have not be found.")
        }
    }

    void processEmailMessage(long messageId) {
        boolean useFlushOnSave = configuration.asynchronous.mail.useFlushOnSave

        AsynchronousMailMessage message = asynchronousMailPersistenceService.getMessage(messageId)

        if (!message) {
            log.error("Can't find message with id=${messageId}.")
            return
        }

        log.trace("Got a message: " + message.toString())

        Date now = new Date()
        Date attemptDate = new Date(now.getTime() - message.attemptInterval)
        boolean canAttempt = message.hasAttemptedStatus() && message.lastAttemptDate.before(attemptDate)
        if (message.hasCreatedStatus() || canAttempt) {
            message.lastAttemptDate = now
            message.attemptsCount++

            // Guarantee that e-mail can't be sent more than 1 time
            message.status = MessageStatus.ERROR
            asynchronousMailPersistenceService.save(message, useFlushOnSave, false)

            // Validate message
            if (!message.validate()) {
                message.errors.allErrors.each {
                    log.error(it)
                }
                return
            }

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
                asynchronousMailPersistenceService.save(message, useFlushOnSave, false)
            }

            // Delete message if it is sent successfully and can be deleted
            if (message.hasSentStatus() && message.markDelete) {
                asynchronousMailPersistenceService.delete(message)
                log.trace("The message with id=${messageId} was deleted.")
            } else if (message.hasSentStatus() && message.markDeleteAttachments) {
                asynchronousMailPersistenceService.deleteAttachments(message)
                log.trace("The message with id=${messageId} had all its attachments deleted.")
            } else {
                log.trace("The message with id=${messageId} will not be deleted.")
            }
        }
    }
}

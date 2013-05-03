package grails.plugin.asyncmail

import org.springframework.mail.*

/**
 * Send asynchronous messages
 */
class AsynchronousMailJob {
    static triggers = {}

    def concurrent = false
    def group = "AsynchronousMail"

    // Dependency injection
    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailSendService asynchronousMailSendService

    def execute() {
        log.trace('Enter to execute method.')

        // Get messages from DB
        def messages = asynchronousMailPersistenceService.selectMessagesForSend()

        // Send each message and save new status
        try {
            messages.each {AsynchronousMailMessage message ->
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
                    asynchronousMailPersistenceService.save(message, true)

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
                        asynchronousMailPersistenceService.save(message, true)
                    }

                    // Delete message if it is sent successfully and can be deleted
                    if (message.status == MessageStatus.SENT && message.markDelete) {
                        long id = message.id
                        asynchronousMailPersistenceService.delete(message);
                        log.trace("The message with id=${id} was deleted.")
                    }
                }
            }
        } catch (Exception e) {
            log.error('Abort mail sent.', e)
        }

        log.trace("Exit from execute method.");
    }
}

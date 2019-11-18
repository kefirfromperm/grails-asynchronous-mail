package grails.plugin.asyncmail

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.validation.ObjectError

class AsynchronousMailService {

    {
        log.trace("TRACE MODE FOR AsynchronousMailService is [ON]")
    }

    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailMessageBuilderFactory asynchronousMailMessageBuilderFactory
    GrailsApplication grailsApplication

    /**
     * Create asynchronous message and save it to the DB.
     *
     * If configuration flag asynchronous.mail.send.immediately is true (default)
     * then this method starts the send job after message is created
     */
    def sendAsynchronousMail(Closure callable) {
        def messageBuilder = asynchronousMailMessageBuilderFactory.createBuilder()
        callable.delegate = messageBuilder
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable.call()
        // Mail message
        AsynchronousMailMessage message = messageBuilder.message
        log.trace("Scheduling message ${message.id} to sent to ${message.to}")

        // Get immediately behavior configuration
        boolean immediately
        if (messageBuilder.immediatelySetted) {
            immediately = messageBuilder.immediately
        } else {
            immediately = grailsApplication.config.asynchronous.mail.send.immediately
        }
        immediately =
                immediately &&
                        message.beginDate.time <= System.currentTimeMillis() &&
                        !grailsApplication.config.asynchronous.mail.disable
        log.trace("Is message scheduled to be sent immediately ${immediately}")
        // Save message to DB
        def savedMessage = null
        if(immediately && grailsApplication.config.asynchronous.mail.newSessionOnImmediateSend) {
            AsynchronousMailMessage.withNewSession {
                log.trace("Thread count ${Thread.activeCount()}")
                savedMessage = asynchronousMailPersistenceService.save(message, true)
            }
        } else {
            log.trace("Thread count ${Thread.activeCount()}")
            savedMessage = asynchronousMailPersistenceService.save(message, immediately)
        }

        if (!savedMessage) {
            StringBuilder errorMessage = new StringBuilder()
            message.errors?.allErrors?.each {ObjectError error ->
                errorMessage.append(error.getDefaultMessage())
            }
            log.trace("Message ${message.id} not sent because of : ${errorMessage.toString()}")
            throw new Exception(errorMessage.toString())
        } else {
            log.trace("Message ${savedMessage.id} sent succesfully")
        }

        // Start job immediately
        if (immediately) {
            log.trace("Start send job immediately for message ${message.id}")
            sendImmediately()
        }

        return message
    }

    /**
     * @see #sendAsynchronousMail
     */
    def sendMail(Closure callable) {
        return sendAsynchronousMail(callable)
    }

    /**
     * Start send job immediately. If you send more than one message in one method,
     * you can disable asynchronous.mail.send.immediately flag (default true) and use this method
     * after creating all messages.
     *
     * <code>asynchronousMailService.sendAsynchronousMail{...}<br/>
     * asynchronousMailService.sendAsynchronousMail{...}<br/>
     * asynchronousMailService.sendAsynchronousMail{...}<br/>
     * asynchronousMailService.sendImmediately()</code>
     */
    def sendImmediately() {
        AsynchronousMailJob.triggerNow()
    }
}

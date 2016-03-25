package grails.plugin.asyncmail

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import org.springframework.validation.ObjectError

class AsynchronousMailService implements GrailsConfigurationAware {
    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailMessageBuilderFactory asynchronousMailMessageBuilderFactory
    Config configuration

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

        // Get immediately behavior configuration
        boolean immediately
        if (messageBuilder.immediatelySetted) {
            immediately = messageBuilder.immediately
        } else {
            immediately = configuration.asynchronous.mail.send.immediately
        }
        immediately =
            immediately &&
                    message.beginDate.time <= System.currentTimeMillis() &&
                    !configuration.asynchronous.mail.disable

        // Save message to DB
		def savedMessage = null
		if(immediately && configuration.asynchronous.mail.newSessionOnImmediateSend) {
            AsynchronousMailMessage.withNewSession {
                savedMessage = asynchronousMailPersistenceService.save(message, true)
            }
        } else {
            savedMessage = asynchronousMailPersistenceService.save(message, immediately)
        }

        if (!savedMessage) {
            StringBuilder errorMessage = new StringBuilder()
            message.errors?.allErrors?.each {ObjectError error ->
                errorMessage.append(error.getDefaultMessage())
            }
            throw new Exception(errorMessage.toString())
        }

        // Start job immediately
        if (immediately) {
            log.trace("Start send job immediately.")
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

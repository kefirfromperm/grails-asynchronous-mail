package grails.plugin.asyncmail

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.validation.ObjectError

class AsynchronousMailService {
    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailMessageBuilderFactory asynchronousMailMessageBuilderFactory
    GrailsApplication grailsApplication

    /**
     * Create asynchronous message and save it to DB.
     *
     * If configuration flag asynchronous.mail.send.immediately is true (default)
     * then this method start send job after create message
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
            immediately = grailsApplication.config.asynchronous.mail.send.immediately
        }
        immediately =
            immediately &&
                    message.beginDate.time <= System.currentTimeMillis() &&
                    !grailsApplication.config.asynchronous.mail.disable

        // Save message to DB
		def savedMessage = null
		if(immediately && grailsApplication.config.asynchronous.mail.newSessionOnImmediateSend) {
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
     * after then create all messages.
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

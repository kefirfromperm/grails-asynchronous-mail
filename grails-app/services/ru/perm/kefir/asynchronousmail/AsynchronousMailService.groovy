package ru.perm.kefir.asynchronousmail

import org.springframework.validation.ObjectError
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AsynchronousMailService {
    boolean transactional = true

    /**
     * Create synchronous message and save it to DB.
     *
     * If configuration flag asynchronous.mail.send.immediately is true (default)
     * then this method start send job after create message
     */
    def sendAsynchronousMail(Closure callable) {
        def messageBuilder = new AsynchronousMailMessageBuilder();
        messageBuilder.init();
        callable.delegate = messageBuilder;
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable.call()

        // Get immediately behavior configuration
        boolean immediately = ConfigurationHolder.config.asynchronous.mail.send.immediately

        // Save message to DB
        AsynchronousMailMessage message = messageBuilder.message;
        immediately = immediately && message.beginDate.time <= System.currentTimeMillis();
        if (!message.save(flush: immediately)) {
            StringBuilder errorMessage = new StringBuilder();
            message.errors?.allErrors?.each {ObjectError error ->
                errorMessage.append(error.getDefaultMessage());
            }
            throw new Exception(errorMessage.toString());
        }

        // Start job immediately
        if (immediately) {
            log.trace("Start send job immediately.");
            sendImmediately();
        }
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
        AsynchronousMailJob.triggerNow(['messagesAtOnce': ConfigurationHolder.config.asynchronous.mail.messages.at.once])
    }
}

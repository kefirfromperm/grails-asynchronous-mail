package ru.perm.kefir.asynchronousmail

import org.springframework.validation.ObjectError

class AsynchronousMailService {

    boolean transactional = true

    def sendAsynchronousMail(Closure callable) {
        def messageBuilder = new AsynchronousMailMessageBuilder();
        messageBuilder.init();
        callable.delegate = messageBuilder;
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable.call()

        AsynchronousMailMessage message = messageBuilder.message;
        if(!message.save()){
            StringBuilder errorMessage = new StringBuilder();
            message.errors.allErrors.each {ObjectError error->
                errorMessage.append(error.getDefaultMessage());
            }
            throw new Exception(errorMessage.toString());
        }
    }
}

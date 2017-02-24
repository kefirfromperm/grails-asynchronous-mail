package grails.plugin.asyncmail

import grails.config.Config
import grails.core.support.GrailsConfigurationAware

class AsynchronousMailConfigService implements GrailsConfigurationAware {
    Config configuration

    long getDefaultAttemptInterval() {
        return configuration.asynchronous.mail.default.attempt.interval
    }

    int getDefaultMaxAttemptCount() {
        return configuration.asynchronous.mail.default.max.attempts.count
    }

    long getSendRepeatInterval() {
        return configuration.asynchronous.mail.send.repeat.interval
    }

    long getExpiredCollectorRepeatInterval() {
        return configuration.asynchronous.mail.expired.collector.repeat.interval
    }

    int getMessagesAtOnce() {
        return configuration.asynchronous.mail.messages.at.once
    }

    boolean isSendImmediately() {
        return configuration.asynchronous.mail.send.immediately
    }

    boolean isClearAfterSent() {
        return configuration.asynchronous.mail.clear.after.sent
    }

    boolean isDisable() {
        return configuration.asynchronous.mail.disable
    }

    boolean isUseFlushOnSave() {
        return configuration.asynchronous.mail.useFlushOnSave
    }

    String getPersistenceProvider() {
        return configuration.asynchronous.mail.persistence.provider
    }

    boolean getNewSessionOnImmediateSend() {
        return configuration.asynchronous.mail.newSessionOnImmediateSend
    }

    int getTaskPoolSize() {
        return configuration.asynchronous.mail.taskPoolSize
    }

    boolean isMongo(){
        getPersistenceProvider() == 'mongodb'
    }
}

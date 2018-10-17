package grails.plugin.asyncmail

import grails.test.mixin.integration.Integration
import spock.lang.Specification

/**
 * Test default configuration
 */
@Integration
class AsynchronousMailConfigServiceSpec extends Specification {
    AsynchronousMailConfigService asynchronousMailConfigService

    void "test configuration"() {
        expect: "the service returns defult values"
            asynchronousMailConfigService.defaultAttemptInterval == 300000l
            asynchronousMailConfigService.defaultMaxAttemptCount == 1
            asynchronousMailConfigService.sendRepeatInterval == 60000l
            asynchronousMailConfigService.expiredCollectorRepeatInterval == 607000l
            asynchronousMailConfigService.messagesAtOnce == 100
            asynchronousMailConfigService.sendImmediately
            !asynchronousMailConfigService.clearAfterSent
            !asynchronousMailConfigService.disable
            asynchronousMailConfigService.useFlushOnSave
            asynchronousMailConfigService.persistenceProvider == 'hibernate4'
            !asynchronousMailConfigService.newSessionOnImmediateSend
            asynchronousMailConfigService.taskPoolSize == 1
            !asynchronousMailConfigService.mongo
    }
}

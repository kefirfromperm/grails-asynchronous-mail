package grails.plugin.asyncmail

import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AsynchronousMailProcessService)
class AsynchronousMailProcessServiceTests {
    AsynchronousMailProcessService asynchronousMailProcessService
    def asynchronousMailPersistenceService

    @Before
    void init(){
        asynchronousMailProcessService = new AsynchronousMailProcessService()
        asynchronousMailPersistenceService = new AsynchronousMailPersistenceServiceMock()
        asynchronousMailProcessService.asynchronousMailPersistenceService = asynchronousMailPersistenceService
        asynchronousMailProcessService.asynchronousMailSendService = new AsynchronousMailSendServiceMock()

        grailsApplication.config.asynchronous.mail.useFlushOnSave = true
        asynchronousMailProcessService.grailsApplication = grailsApplication
    }

    void testProcessEmailMessage() {
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text'
        )

        asynchronousMailPersistenceService.save(message)
        asynchronousMailProcessService.processEmailMessage(1)
        assert message.status == MessageStatus.SENT
        assert message.sentDate !=null
    }
}

class AsynchronousMailPersistenceServiceMock{
    def message

    void save(message, boolean flush = true){
        this.message = message
    }

    def getMessage(id){
        return message
    }
}

class AsynchronousMailSendServiceMock{
    void send(message){
        // Nothing!
    }
}
package grails.plugin.asyncmail
import grails.test.mixin.TestFor
import spock.lang.Specification

import static grails.plugin.asyncmail.enums.MessageStatus.SENT
/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
@TestFor(AsynchronousMailProcessService)
class AsynchronousMailProcessServiceSpec extends Specification {

    def asynchronousMailPersistenceService
    def asynchronousMailSendService

    void setup() {
        asynchronousMailSendService = Mock(AsynchronousMailSendService)
    }

    void testProcessEmail() {
        setup:
        grailsApplication.config.asynchronous.mail.useFlushOnSave = true
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text'
        )
        1 * asynchronousMailSendService.send(_)
        asynchronousMailPersistenceService = new AsynchronousMailPersistenceServiceMock()

        when:
        asynchronousMailPersistenceService.save message
        service.asynchronousMailPersistenceService = asynchronousMailPersistenceService
        service.asynchronousMailSendService = asynchronousMailSendService
        service.processEmailMessage(1l)

        then:
        message.status == SENT
        message.sentDate
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

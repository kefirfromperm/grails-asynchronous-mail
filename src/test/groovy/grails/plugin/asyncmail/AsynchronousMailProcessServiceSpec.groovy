package grails.plugin.asyncmail

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

import static grails.plugin.asyncmail.enums.MessageStatus.SENT

/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
@TestFor(AsynchronousMailProcessService)
@Mock(AsynchronousMailMessage)
class AsynchronousMailProcessServiceSpec extends Specification {

    AsynchronousMailPersistenceService asynchronousMailPersistenceService
    AsynchronousMailSendService asynchronousMailSendService
    AsynchronousMailConfigService asynchronousMailConfigService

    void setup() {
        asynchronousMailSendService = Mock(AsynchronousMailSendService)
        asynchronousMailPersistenceService = Stub(AsynchronousMailPersistenceService) {
            save(_, _, _) >> { AsynchronousMailMessage message, boolean flush, boolean validate ->
                message.save(flush: flush, validata: validate)
            }

            getMessage(_) >> { long id ->
                AsynchronousMailMessage.get(id)
            }
        }
        asynchronousMailConfigService = Stub(AsynchronousMailConfigService) {
            getTaskPoolSize() >> 1
            isUseFlushOnSave() >> true
        }

        service.asynchronousMailConfigService = asynchronousMailConfigService
        service.asynchronousMailPersistenceService = asynchronousMailPersistenceService
        service.asynchronousMailSendService = asynchronousMailSendService
    }

    void testProcessEmail() {
        setup:
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: 'Text'
            )
            asynchronousMailPersistenceService.save message, true, true

        when:
            service.processEmailMessage(message.id)

        then:
            1 * asynchronousMailSendService.send(_)
            message.status == SENT
            message.sentDate
    }
}


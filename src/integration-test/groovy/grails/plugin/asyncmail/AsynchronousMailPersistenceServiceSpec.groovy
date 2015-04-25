package grails.plugin.asyncmail
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.IgnoreRest
import spock.lang.Specification

import static grails.plugin.asyncmail.enums.MessageStatus.EXPIRED
/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
@Integration
@Rollback
class AsynchronousMailPersistenceServiceSpec extends Specification {

    @Autowired
    AsynchronousMailPersistenceService asynchronousMailPersistenceService

    @IgnoreRest
    void testCycle() {
        when: 'a message is instantiated and saved'
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text'
        )
        message.save flush
//        asynchronousMailPersistenceService.save(message, true)

        then: 'selectMessagesIdsForSend should return list with 1 messageId'
        1 == AsynchronousMailMessage.count()

       /* when: 'deleted the message'
        asynchronousMailPersistenceService.delete(message)

        then: 'message to be send count should be 0'
        0 == asynchronousMailPe*/rsistenceService.selectMessagesIdsForSend()?.size()
    }

    void testSaveSimpleMessage(){
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                bcc: ['mary@example.com'],
                subject: 'Subject',
                text: 'Text'
        )

        expect:
        asynchronousMailPersistenceService.save(message, true)

        cleanup:
        asynchronousMailPersistenceService.delete(message)
    }

    void testUpdateExpiredMessages(){
        when: 'message is saved with expired endDate'
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text',
                beginDate: new Date(System.currentTimeMillis()-2),
                endDate: new Date(System.currentTimeMillis()-1)
        )
        asynchronousMailPersistenceService.save message, true

        then: 'selectMessagesIdsForSend should return empty list'
        0 == asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size()

        when:
        asynchronousMailPersistenceService.updateExpiredMessages()
        message.refresh()

        then:
        EXPIRED == message.status
    }
}

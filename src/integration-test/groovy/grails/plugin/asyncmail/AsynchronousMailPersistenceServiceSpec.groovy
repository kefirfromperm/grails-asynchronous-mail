package grails.plugin.asyncmail

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
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

    void testCycle() {
        given: "a message"
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: 'Text'
            )
        when: 'a message is instantiated and saved'
            //message.save flush
            asynchronousMailPersistenceService.save(message, true, true)
            List<Long> ids = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        then: 'selectMessagesIdsForSend should return list with 1 messageId'
            1 == ids.size()
            ids[0] == message.id

        when: "get message"
            AsynchronousMailMessage message1 = asynchronousMailPersistenceService.getMessage(ids[0])

        then: "message1 id message"
            message1.id == message.id
            message1.from == 'John Smith <john@example.com>'
            message1.to == ['Mary Smith <mary@example.com>']
            message1.subject == 'Subject'
            message1.text == 'Text'

        when: 'deleted the message'
            asynchronousMailPersistenceService.delete(message1, true)

        then: 'message to be send count should be 0'
            0 == asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size()
    }

    void testDeleteAttachments() {
        given: "a message"
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: 'Text',
                    attachments: [
                            new AsynchronousMailAttachment(
                                    attachmentName: 'name',
                                    content: 'Grails'.getBytes()
                            )
                    ]
            )
        when: 'a message is instantiated and saved'

            asynchronousMailPersistenceService.save(message, true, true)
            List<Long> ids = asynchronousMailPersistenceService.selectMessagesIdsForSend()

        then: 'selectMessagesIdsForSend should return list with 1 messageId'
            1 == ids.size()

        when: "get message"
            AsynchronousMailMessage message1 = asynchronousMailPersistenceService.getMessage(ids[0])

        then: "it contains attachments"
            message1.attachments.size() == 1

        when: 'deleted the message'
            asynchronousMailPersistenceService.deleteAttachments(message, true)
            message1 = asynchronousMailPersistenceService.getMessage(ids[0])

        then: 'the message should have no attachments'
            message1.attachments ? false : true
    }

    void testSaveSimpleMessage() {
        setup:
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    bcc: ['mary@example.com'],
                    subject: 'Subject',
                    text: 'Text'
            )

        when:
            asynchronousMailPersistenceService.save(message, true, true)
        then:
            AsynchronousMailMessage dbMessage = asynchronousMailPersistenceService.getMessage(message.id)
            dbMessage.bcc == ['mary@example.com']

        cleanup:
            asynchronousMailPersistenceService.delete(message, true)
    }

    void testUpdateExpiredMessages() {
        when: 'message is saved with expired endDate'
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: 'Text',
                    beginDate: new Date(System.currentTimeMillis() - 2),
                    endDate: new Date(System.currentTimeMillis() - 1)
            )
            asynchronousMailPersistenceService.save message, true, true

        then: 'selectMessagesIdsForSend should return empty list'
            0 == asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size()

        when:
            asynchronousMailPersistenceService.updateExpiredMessages()
            message.refresh()

        then:
            EXPIRED == message.status
    }
}

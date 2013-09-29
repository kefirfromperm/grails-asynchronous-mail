package grails.plugin.asyncmail

import org.junit.Assert

/**
 * Test for persistence service.
 */
class AsynchronousMailPersistenceServiceTests extends GroovyTestCase {
    AsynchronousMailPersistenceService asynchronousMailPersistenceService

    void testCycle() {
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text'
        )

        Assert.assertNotNull(asynchronousMailPersistenceService.save(message, true))
        Assert.assertEquals(1, asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size())
        asynchronousMailPersistenceService.delete(message)
        Assert.assertEquals(0, asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size())
    }

    void testSaveSimpleMessage(){
        def message = new AsynchronousMailMessage(
                bcc: ['mary@example.com'],
                subject: 'Subject',
                text: 'Text'
        )

        Assert.assertNotNull(asynchronousMailPersistenceService.save(message, true))
        asynchronousMailPersistenceService.delete(message)
    }

    void testUpdateExpiredMessages(){
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text',
                beginDate: new Date(System.currentTimeMillis()-2),
                endDate: new Date(System.currentTimeMillis()-1)
        )

        Assert.assertNotNull(asynchronousMailPersistenceService.save(message, true))
        Assert.assertEquals(0, asynchronousMailPersistenceService.selectMessagesIdsForSend()?.size())
        asynchronousMailPersistenceService.updateExpiredMessages()
        message.refresh()
        assert message.getStatus() == MessageStatus.EXPIRED
    }
}

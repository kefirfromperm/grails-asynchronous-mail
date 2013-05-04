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
        Assert.assertEquals(1, asynchronousMailPersistenceService.selectMessagesForSend()?.size())
        asynchronousMailPersistenceService.delete(message)
        Assert.assertEquals(0, asynchronousMailPersistenceService.selectMessagesForSend()?.size())
    }
}

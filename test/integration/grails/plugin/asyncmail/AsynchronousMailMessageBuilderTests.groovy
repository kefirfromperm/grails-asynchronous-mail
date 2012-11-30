package grails.plugin.asyncmail

import grails.test.GrailsUnitTestCase

/**
 * Unit tests for AsynchronousMailMessageBuilder
 */
class AsynchronousMailMessageBuilderTests extends GrailsUnitTestCase {

    static transactional = false

    AsynchronousMailMessageBuilderFactory asynchronousMailMessageBuilderFactory

    @Override
    protected void setUp() {
        super.setUp()

        // Apply constraints for message objects
        mockForConstraintsTests(AsynchronousMailMessage)

        // Apply constraints for attachment objects
        mockForConstraintsTests(AsynchronousMailAttachment)
    }

    void testBuilder() {
        def c = {
            from 'John Smith <john@example.com>'
            to 'test1@example.com'
            subject 'Subject'
            text 'Text'
            immediate false
            delete true
            priority 1
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        assertEquals('John Smith <john@example.com>', message.from)
        assertEquals(['test1@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertEquals('Text', message.text)
        assertEquals(MessageStatus.CREATED, message.status)
        assertTrue(message.markDelete)

        // Priority
        assertEquals(1, message.priority)

        // Immediately
        assertTrue builder.immediatelySetted
        assertFalse builder.immediately
    }

    void testMail1() {
        // make test data
        Map hdr = [test: 'test']
        List toList = ['test3@example.com']
        List bccList = ['test4@example.com']
        List ccList = ['test5@example.com']
        def titleString = 'Test title'
        def bodyString = 'Body test'

        // Apply test data
        def c = {
            headers(hdr)
            to(toList)
            bcc(bccList)
            cc(ccList)
            replyTo('test6@example.com')
            from('test7@example.com')
            title(titleString)
            body(bodyString)
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(hdr, message.headers)
        assertEquals(toList, message.to)
        assertEquals(bccList, message.bcc)
        assertEquals(ccList, message.cc)
        assertEquals('test6@example.com', message.replyTo)
        assertEquals('test7@example.com', message.from)
        assertEquals(titleString, message.subject)
        assertEquals(bodyString, message.text)
        assertEquals(false, message.html)
    }

    void testMail2() {
        // make test data
        String[] toArray = ['test1@example.com'] as String[]
        String[] bccArray = ['test2@example.com'] as String[]
        String[] ccArray = ['test3@example.com'] as String[]
        String subjectString = 'Test subject'
        String textString = 'Text test'

        // Apply test data
        def c = {
            to(toArray)
            bcc(bccArray)
            cc(ccArray)
            subject(subjectString)
            text(textString)
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(toArray as List, message.to)
        assertEquals(bccArray as List, message.bcc)
        assertEquals(ccArray as List, message.cc)
        assertEquals(subjectString, message.subject)
        assertEquals(textString, message.text)
        assertEquals(false, message.html)
    }

    void testMail3() {
        // make test data
        Object[] toArray = ['test1@example.com'] as Object[]
        Object[] bccArray = ['test2@example.com'] as Object[]
        Object[] ccArray = ['test3@example.com'] as Object[]
        String subjectString = 'Test subject'
        String textString = 'Text test'

        // Apply test data
        def c = {
            to(toArray)
            bcc(bccArray)
            cc(ccArray)
            subject(subjectString)
            text(textString)
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(toArray as List, message.to)
        assertEquals(bccArray as List, message.bcc)
        assertEquals(ccArray as List, message.cc)
        assertEquals(subjectString, message.subject)
        assertEquals(textString, message.text)
        assertEquals(false, message.html)
    }

    void testHtml(){
        String htmlString = '<html><head></head><body></body></html>'

        def c = {
            to 'test@example.com'
            subject 'Subject'
            html htmlString
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(['test@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertEquals(htmlString, message.text)
        assertTrue(message.html)
    }

    void testBodyTextRender(){
        def c = {
            to 'test@example.com'
            subject 'Subject'
            body view:'/test/plain'
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(['test@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertFalse(message.html)
        assertNotNull(message.text)
    }

    void testBodyHtmlRender(){
        def c = {
            to 'test@example.com'
            subject 'Subject'
            body view:'/test/html'
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(['test@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertTrue(message.html)
        assertNotNull(message.text)
    }

    void testTextRender(){
        def c = {
            to 'test@example.com'
            subject 'Subject'
            locale 'en_us'
            text view:'/test/plain'
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(['test@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertFalse(message.html)
        assertNotNull(message.text)
    }

    void testHtmlRender(){
        def c = {
            to 'test@example.com'
            subject 'Subject'
            locale Locale.ENGLISH
            html view:'/test/html'
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder()
        c.delegate = builder
        c.call()

        // Message
        AsynchronousMailMessage message = builder.message

        // Validate message
        assertTrue(message.validate())

        // Assert data
        assertEquals(['test@example.com'], message.to)
        assertEquals('Subject', message.subject)
        assertTrue(message.html)
        assertNotNull(message.text)
    }
}

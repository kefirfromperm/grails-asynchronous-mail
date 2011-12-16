package grails.plugin.asyncmail

import grails.test.GrailsUnitTestCase

/**
 * Unit tests for AsynchronousMailMessageBuilder
 */
class AsynchronousMailMessageBuilderTests extends GrailsUnitTestCase {
    static transactional = false;
    AsynchronousMailMessageBuilderFactory asynchronousMailMessageBuilderFactory;

    @Override
    protected void setUp() {
        super.setUp();

        // Apply constraints for message objects
        def existingMessage = new AsynchronousMailMessage();
        mockForConstraintsTests(AsynchronousMailMessage, [existingMessage]);

        // Apply constraints for attachment objects
        def existingAttachment = new AsynchronousMailAttachment();
        mockForConstraintsTests(AsynchronousMailAttachment, [existingAttachment]);
    }

    void testBuilder() {
        def c = {
            to 'kefir@perm.ru';
            subject 'Subject';
            text 'Text';
            immediate false;
            delete true;
            priority 1;
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        assertEquals(['kefir@perm.ru'], message.to);
        assertEquals('Subject', message.subject);
        assertEquals('Text', message.text);
        assertEquals(MessageStatus.CREATED, message.status);
        assertTrue(message.markDelete);

        // Priority
        assertEquals(1, message.priority);

        // Immediately
        assertTrue builder.immediatelySetted;
        assertFalse builder.immediately;
    }

    void testMail1() {
        // make test data
        Map hdr = [test: 'test'];
        List toList = ['kefir@perm.ru'];
        List bccList = ['kefirfromperm@gmail.com'];
        List ccList = ['kefirfromperm@yandex.ru'];
        def titleString = 'Test title'
        def bodyString = 'Body test'

        // Apply test data
        def c = {
            headers(hdr);
            to(toList);
            bcc(bccList);
            cc(ccList);
            replyTo('kefirfromperm@mail.ru');
            from('kefirfromperm@rambler.ru');
            title(titleString);
            body(bodyString);
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        // Assert data
        assertEquals(hdr, message.headers);
        assertEquals(toList, message.to);
        assertEquals(bccList, message.bcc);
        assertEquals(ccList, message.cc);
        assertEquals('kefirfromperm@mail.ru', message.replyTo);
        assertEquals('kefirfromperm@rambler.ru', message.from);
        assertEquals(titleString, message.subject);
        assertEquals(bodyString, message.text);
        assertEquals(false, message.html);
    }

    void testMail2() {
        // make test data
        String[] toArray = ['kefir@perm.ru'] as String[];
        String[] bccArray = ['kefirfromperm@gmail.com'] as String[];
        String[] ccArray = ['kefirfromperm@yandex.ru'] as String[];
        String subjectString = 'Test subject';
        String textString = 'Text test';

        // Apply test data
        def c = {
            to(toArray);
            bcc(bccArray);
            cc(ccArray);
            subject(subjectString);
            text(textString);
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        // Assert data
        assertEquals(toArray as List, message.to);
        assertEquals(bccArray as List, message.bcc);
        assertEquals(ccArray as List, message.cc);
        assertEquals(subjectString, message.subject);
        assertEquals(textString, message.text);
        assertEquals(false, message.html);
    }

    void testHtml(){
        String htmlString = '<html><head></head><body></body></html>';

        def c = {
            to 'kefir@perm.ru';
            subject 'Subject';
            html htmlString;
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        // Assert data
        assertEquals(['kefir@perm.ru'], message.to);
        assertEquals('Subject', message.subject);
        assertEquals(htmlString, message.text);
        assertTrue(message.html);
    }

    void testBodyTextRender(){
        def c = {
            to 'kefir@perm.ru';
            subject 'Subject';
            body view:'/test/plain';
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        // Assert data
        assertEquals(['kefir@perm.ru'], message.to);
        assertEquals('Subject', message.subject);
        assertFalse(message.html);
    }

    void testBodyHtmlRender(){
        def c = {
            to 'kefir@perm.ru';
            subject 'Subject';
            body view:'/test/html';
        }

        def builder = asynchronousMailMessageBuilderFactory.createBuilder();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Validate message
        assertTrue(message.validate());

        // Assert data
        assertEquals(['kefir@perm.ru'], message.to);
        assertEquals('Subject', message.subject);
        assertTrue(message.html);
    }
}

package ru.perm.kefir.asynchronousmail

import grails.test.GrailsUnitTestCase

/**
 * Unit tests for AsynchronousMailMessageBuilder
 */
class AsynchronousMailMessageBuilderTests extends GrailsUnitTestCase  {
    void testBuilder(){
        def c = {
            to 'kefir@perm.ru';
            subject 'Subject';
            text 'Text';
            immediate false;
            delete true;
        }

        def builder = new AsynchronousMailMessageBuilder(null);
        builder.init();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;
        assertEquals(['kefir@perm.ru'], message.to);
        assertEquals('Subject', message.subject);
        assertEquals('Text', message.text);
        assertEquals MessageStatus.CREATED, message.status;
        assertTrue message.markDelete

        // Immediately
        assertTrue builder.immediatelySetted;
        assertFalse builder.immediately;
    }
}

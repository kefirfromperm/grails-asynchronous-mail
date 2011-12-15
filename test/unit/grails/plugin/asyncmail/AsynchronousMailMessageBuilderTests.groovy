package grails.plugin.asyncmail

import grails.test.GrailsUnitTestCase
import grails.plugin.asyncmail.AsynchronousMailMessage
import grails.plugin.asyncmail.MessageStatus
import grails.plugin.asyncmail.AsynchronousMailMessageBuilder

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
            priority 1;
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
        assertEquals(MessageStatus.CREATED, message.status);
        assertTrue(message.markDelete);

        // Priority
        assertEquals(1, message.priority);

        // Immediately
        assertTrue builder.immediatelySetted;
        assertFalse builder.immediately;
    }
    
    void testMail1(){
        // make test data
        Map hdr = [test:'test'];
        List toList = ['kefir@perm.ru'];
        List bccList = ['kefirfromperm@gmail.com'];
        List ccList = ['kefirfromperm@yandex.ru'];
        def titleString = 'Test title'
        
        // Apply test data
        def c = {
            headers(hdr);
            to(toList);
            bcc(bccList);
            cc(ccList);
            replyTo('kefirfromperm@mail.ru');
            from('kefirfromperm@rambler.ru');
            title(titleString);
        }

        def builder = new AsynchronousMailMessageBuilder(null);
        builder.init();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Assert data
        assertEquals(hdr, message.headers);
        assertEquals(toList, message.to);
        assertEquals(bccList, message.bcc);
        assertEquals(ccList, message.cc);
        assertEquals('kefirfromperm@mail.ru', message.replyTo);
        assertEquals('kefirfromperm@rambler.ru', message.from);
        assertEquals(titleString, message.subject);
    }

    void testMail2(){
        // make test data
        String[] toArray = ['kefir@perm.ru'] as String[];
        String[] bccArray = ['kefirfromperm@gmail.com'] as String[];
        String[] ccArray = ['kefirfromperm@yandex.ru'] as String[];
        String subjectString = 'Test subject';

        // Apply test data
        def c = {
            to(toArray);
            bcc(bccArray);
            cc(ccArray);
            subject(subjectString);
        }

        def builder = new AsynchronousMailMessageBuilder(null);
        builder.init();
        c.delegate = builder;
        c.call();

        // Message
        AsynchronousMailMessage message = builder.message;

        // Assert data
        assertEquals(toArray as List, message.to);
        assertEquals(bccArray as List, message.bcc);
        assertEquals(ccArray as List, message.cc);
        assertEquals(subjectString, message.subject);
    }
}

package grails.plugin.asyncmail

/**
 * Tests for service.
 *
 * @author Vitalii Samolovskikh aka Kefir
 */
class AsynchronousMailTests extends GroovyTestCase {
    public static final String VALUE_MAIL = 'test@example.com'

    AsynchronousMailService asynchronousMailService;
    AsynchronousMailService asyncMailService;

    void testSendAsynchronousMail(){
        asyncMailService.sendMail {
            to VALUE_MAIL
            subject 'Test'
            text 'Test'

            immediate false
        }

        AsynchronousMailMessage message = AsynchronousMailMessage.findAll()[0]

        assertEquals(VALUE_MAIL, message.to[0])
        assertEquals(MessageStatus.CREATED, message.status)
    }

    void testServiceAlias(){
        assertEquals(asynchronousMailService, asyncMailService)
    }
}

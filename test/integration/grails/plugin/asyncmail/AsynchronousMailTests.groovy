package grails.plugin.asyncmail

/**
 * Tests for service.
 *
 * @author Vitalii Samolovskikh aka Kefir
 */
class AsynchronousMailTests extends GroovyTestCase {
    AsynchronousMailService asynchronousMailService;
    AsynchronousMailService asyncMailService;

    void testSendAsynchronousMail(){
        assertTrue(false)
    }

    void testServiceAlias(){
        assertEquals(asynchronousMailService, asyncMailService)
    }
}

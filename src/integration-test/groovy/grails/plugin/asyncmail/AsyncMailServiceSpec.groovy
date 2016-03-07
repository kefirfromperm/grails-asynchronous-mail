package grails.plugin.asyncmail

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

import javax.annotation.Resource

import static grails.plugin.asyncmail.enums.MessageStatus.CREATED

/**
 * Test for the service alias
 * @author kefir
 */
@Integration
@Rollback
class AsyncMailServiceSpec extends Specification {
    public static final String VALUE_MAIL = 'test@example.com'

    @Resource(name="asyncMailService")
    def asyncMailService

    void testSendAsynchronousMail(){
        when:
        asyncMailService.sendMail {
            to VALUE_MAIL
            subject 'Test'
            text 'Test'
            immediate false
        }
        AsynchronousMailMessage message = AsynchronousMailMessage.findAll()[0]

        then:
        VALUE_MAIL == message.to[0]
        CREATED == message.status
    }
}

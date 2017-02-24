package grails.plugin.asyncmail

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static grails.plugin.asyncmail.enums.MessageStatus.CREATED

/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
@Integration
@Rollback
class AsynchronousMailServiceSpec extends Specification {

    public static final String VALUE_MAIL = 'test@example.com'

    @Autowired
    AsynchronousMailService asynchronousMailService

    void testSendAsynchronousMail() {
        when:
            asynchronousMailService.sendMail {
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

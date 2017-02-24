package grails.plugin.asyncmail

import grails.plugins.mail.MailService
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Test for synchornous send service
 */
@TestFor(AsynchronousMailSendService)
class AsynchronousMailSendServiceTest extends Specification {
    void setup() {
        service.mailService = Mock(MailService)
    }

    def "test send"() {
        given: "a message"
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: 'Text'
            )
        when: "send"
            service.send(message)
        then: "calls mailService.sendMail() method"
            1 * service.mailService.sendMail(_ as Closure)
    }
}

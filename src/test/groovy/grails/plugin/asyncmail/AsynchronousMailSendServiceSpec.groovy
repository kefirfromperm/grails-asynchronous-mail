package grails.plugin.asyncmail

import grails.plugins.mail.MailService
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

/**
 * Test for synchornous send service
 */
class AsynchronousMailSendServiceSpec extends Specification implements ServiceUnitTest<AsynchronousMailSendService> {
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

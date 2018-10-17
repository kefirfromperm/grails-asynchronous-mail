package grails.plugin.asyncmail

import grails.plugins.mail.MailMessageBuilder
import grails.plugins.mail.MailService
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Test for synchornous send service
 */
@TestFor(AsynchronousMailSendService)
class AsynchronousMailSendServiceSpec extends Specification {
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

    def "test text alternative and multipart"() {
        given: 'a message with alternative'
            AsynchronousMailMessage message = new AsynchronousMailMessage(
                    from: 'John Smith <john@example.com>',
                    to: ['Mary Smith <mary@example.com>'],
                    subject: 'Subject',
                    text: '<html>HTML text</html>',
                    html: true,
                    alternative: 'Alternative text'
            )
        and: 'stub method sendMail'
            def mockMessageBuilder = Mock(MailMessageBuilder) {
                isMimeCapable() >> true
            }
            service.mailService.sendMail(_ as Closure) >> { Closure cl ->
                cl.delegate = mockMessageBuilder
                cl.call()
            }
        when: "send"
            service.send(message)
        then: 'call mailService with multipart'
            1 * mockMessageBuilder.multipart(true)
    }
}

package grails.plugin.asyncmail

import grails.testing.gorm.DomainUnitTest
import spock.lang.Ignore
import spock.lang.Specification

import static grails.plugin.asyncmail.enums.MessageStatus.*

/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
class AsynchronousMailMessageSpec extends Specification implements DomainUnitTest<AsynchronousMailMessage> {

    void "testing default constructor"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage()

        expect:
        !message.to
        !message.subject
        !message.headers
        !message.text
        !message.html
        !message.bcc
        !message.cc
        !message.replyTo
        !message.from
        !message.envelopeFrom
        !message.attachments
        message.status == CREATED
        message.createDate
        !message.sentDate
        message.beginDate
        message.endDate
        message.attemptsCount == 0
        message.maxAttemptsCount == 1
        !message.lastAttemptDate
        message.attemptInterval == 300000l
            !message.markDelete
    }

    void "message should pass validation"() {
        expect:
        new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                cc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                bcc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                subject: 'Subject',
                text: 'Text'
        ).validate()
    }

    void "message should fail validation as all addresses are null"() {
        expect:
        !new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                subject: 'Subject',
                text: 'Text'
        ).validate()
    }

    void "message should fail validation as all addresses are empty"() {
        expect:
        !new AsynchronousMailMessage(
                to: [],
                cc: [],
                bcc: [],
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                subject: 'Subject',
                text: 'Text'
        ).validate()
    }

    void "message subject cannot be null"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage([:])

        when:
        message.validate()

        then:
        message.errors.getFieldError('subject').code == 'nullable'
    }


    void "message text cannot be null"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage([:])

        when:
        message.validate()

        then:
        message.errors.getFieldError('text').code == 'nullable'
    }

    void "message status cannot be null"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(status: null)

        when:
        message.validate()

        then:
        message.errors.getFieldError('status').code == 'nullable'
    }

    void "message createDate cannot be null"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(createDate: null)

        when:
        message.validate()

        then:
        message.errors.getFieldError('createDate').code == 'nullable'
    }

    void "message beginDate cannot be null"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(beginDate: null)

        when:
        message.validate()

        then:
        message.errors.getFieldError('beginDate').code == 'nullable'
    }

    void "message endDate should fail custom validation as invalid"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(endDate: new Date(System.currentTimeMillis() - 1000))

        when:
        message.validate()

        then:
        message.errors.getFieldError('endDate').code == 'validator.invalid'
    }

    void "message attemptsCount cannot be less than 0"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(attemptsCount: -1)

        when:
        message.validate()

        then:
        message.errors.getFieldError('attemptsCount').code == 'min.notmet'
    }

    void "message maxAttemptsCount cannot be less than 0"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(maxAttemptsCount: -1)

        when:
        message.validate()

        then:
        message.errors.getFieldError('maxAttemptsCount').code == 'min.notmet'
    }

    void "message attemptInterval cannot be less than 0"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(attemptInterval: -1)

        when:
        message.validate()

        then:
        message.errors.getFieldError('attemptInterval').code == 'min.notmet'
    }

    void "cc email addresses should be valid"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(cc: ['mary example com'], subject: 'Subject', text: 'Text')

        when:
        message.validate()

        then:
        message.errors.getFieldError('cc').code == 'asynchronous.mail.mailbox.invalid'
    }

    void "bcc email addresses should be valid"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(bcc: ['mary example com'], subject: 'Subject', text: 'Text')

        when:
        message.validate()

        then:
        message.errors.getFieldError('bcc').code == 'asynchronous.mail.mailbox.invalid'
    }

    void "should fail validation as bad envelop"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                to: ['john@example.com'],
                envelopeFrom: 'mary example com',
                subject: 'Subject',
                text: 'Text'
        )

        when:
        message.validate()

        then:
        message.errors.getFieldError('envelopeFrom').code == 'validator.invalid'
    }

    void "testing when message is abortable"() {
        setup:
        AsynchronousMailMessage message

        when: 'message with default status'
        message = new AsynchronousMailMessage()

        then: 'is abortable'
        message.isAbortable()

        when: 'message with attempted status'
        message.status = ATTEMPTED

        then: 'is abortable'
        message.isAbortable()

        when: 'message with sent status'
        message.status = SENT

        then: 'is not abortable'
        !message.isAbortable()

        when: 'message with error status'
        message.status == ERROR

        then: 'is not abortable'
        !message.isAbortable()

        when: 'message is expired'
        message.status = EXPIRED

        then: 'is not abortable'
        !message.isAbortable()

        when: 'message is abort'
        message.status == ABORT

        then: 'is not abortable'
        !message.isAbortable()

    }

    void "message with headers should pass validation"() {
        expect:
        new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                to: ['Mary Smith <mary@example.com>'],
                subject: 'Subject',
                text: 'Text',
                headers: ['Content-Type': 'text/plain', 'Content-Language': 'en']
        ).validate()
    }

    @Ignore
    void "testing message.toString()"() {
        setup:
        AsynchronousMailMessage message = new AsynchronousMailMessage(
                to: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                subject: 'Subject',
        )
        message.id = 1

        expect:
        message.toString() == 'grails.plugin.asyncmail.AsynchronousMailMessage(id:1, to:[Mary Smith <mary@example.com>, carl@example.com], subject:Subject, status:CREATED)'
    }
}

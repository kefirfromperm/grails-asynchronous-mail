package grails.plugin.asyncmail

import grails.test.mixin.TestFor
import org.junit.Before

import static grails.plugin.asyncmail.MessageStatus.*

/**
 * Test AsynchronousMailMessage constraints
 */
@TestFor(AsynchronousMailMessage)
class AsynchronousMailMessageTests {

    @Before
    public void prepareMock() {
        // Apply constraints for message objects
        mockDomain(AsynchronousMailMessage)
    }

    void testDefault() {
        def message = new AsynchronousMailMessage()
        assertNull message.to
        assertNull message.subject
        assertNull message.headers
        assertNull message.text
        assertFalse message.html
        assertNull message.bcc
        assertNull message.cc
        assertNull message.replyTo
        assertNull message.from
        assertNull message.attachments
        assertEquals CREATED, message.status
        assertNotNull message.createDate
        assertNull message.sentDate
        assertNotNull message.beginDate
        assertNotNull message.endDate
        assertEquals 0, message.attemptsCount
        assertEquals 1, message.maxAttemptsCount
        assertNull message.lastAttemptDate
        assertEquals 300000l, message.attemptInterval
        assertFalse message.markDelete
        assertEquals 0, message.priority
    }

    void testValid() {
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                to: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                cc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                bcc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                subject: 'Subject',
                text: 'Text'
        )
        assertTrue message.validate()
    }

    void testValidWithNoToAdress() {
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                cc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                bcc: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                subject: 'Subject',
                text: 'Text'
        )
        assertTrue message.validate()
    }

    void testAllAdressesNull() {
        def message = new AsynchronousMailMessage(
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                subject: 'Subject',
                text: 'Text'
        )
        assertFalse message.validate()
    }

    void testAllAdressesEmpty() {
        def message = new AsynchronousMailMessage(
                to: [],
                cc: [],
                bcc: [],
                from: 'John Smith <john@example.com>',
                replyTo: 'James Smith <james@example.com>',
                subject: 'Subject',
                text: 'Text'
        )
        assertFalse message.validate()
    }

    void testConstraints() {
        // Constraints on default message
        def message = new AsynchronousMailMessage([:])
        assertFalse message.validate()
        assertTrue message.errors.hasGlobalErrors()
        assertEquals 1, message.errors.globalErrors.size()
        assertEquals "nullable", message.errors["subject"].codes.find { it == "nullable" }
        assertEquals "nullable", message.errors["text"].codes.find { it == "nullable" }

        // Constraint on all fields
        message = new AsynchronousMailMessage(
                to: [],
                subject: '',
                text: '',
                status: null,
                createDate: null,
                beginDate: null,
                endDate: new Date(System.currentTimeMillis() - 1000),
                attemptsCount: -1,
                maxAttemptsCount: -1,
                attemptInterval: -1
        )
        assertFalse message.validate()
        assertEquals "blank", message.errors["subject"].codes.find { it == "blank" }
        assertEquals "blank", message.errors["text"].codes.find { it == "blank" }
        assertEquals "nullable", message.errors["status"].codes.find { it == "nullable" }
        assertEquals "nullable", message.errors["createDate"].codes.find { it == "nullable" }
        assertEquals "nullable", message.errors["beginDate"].codes.find { it == "nullable" }
        assertEquals "validator.invalid", message.errors["endDate"].codes.find { it == "validator.invalid" }
        assertEquals "min.notmet", message.errors["attemptsCount"].codes.find { it == "min.notmet" }
        assertEquals "min.notmet", message.errors["maxAttemptsCount"].codes.find { it == "min.notmet" }
        assertEquals "min.notmet", message.errors["attemptInterval"].codes.find { it == "min.notmet" }
    }

    void testBadToMailbox() {
        def message = new AsynchronousMailMessage(
                to: ['mary example com'],
                subject: 'Subject',
                text: 'Text'
        )

        assertFalse(message.validate())
        assertEquals 'asynchronous.mail.mailbox.invalid', message.errors['to'].codes.find { it == 'asynchronous.mail.mailbox.invalid'}
    }

    void testBadCcMailbox() {
        def message = new AsynchronousMailMessage(
                cc: ['mary example com'],
                subject: 'Subject',
                text: 'Text'
        )

        assertFalse(message.validate())
        assertEquals 'asynchronous.mail.mailbox.invalid', message.errors['cc'].codes.find { it == 'asynchronous.mail.mailbox.invalid'}
    }

    void testIsAbortable() {
        def messageWithDefaultStatus = new AsynchronousMailMessage()
        assert messageWithDefaultStatus.status == CREATED
        assert messageWithDefaultStatus.isAbortable()
        def messageWithAttemptedStatus = new AsynchronousMailMessage(status: ATTEMPTED)
        assert messageWithAttemptedStatus.isAbortable()
        def messageWithSentStatus = new AsynchronousMailMessage(status: SENT)
        assert !messageWithSentStatus.isAbortable()
        def messageWithErrorStatus = new AsynchronousMailMessage(status: ERROR)
        assert !messageWithErrorStatus.isAbortable()
        def messageWithExpiredStatus = new AsynchronousMailMessage(status: EXPIRED)
        assert !messageWithExpiredStatus.isAbortable()
        def messageWithAbortStatus = new AsynchronousMailMessage(status: ABORT)
        assert !messageWithAbortStatus.isAbortable()
    }

    void testToString() {
        def message = new AsynchronousMailMessage(
                to: ['Mary Smith <mary@example.com>', 'carl@example.com'],
                subject: 'Subject',
        )
        message.id = 1
        assert message.toString() == 'AsynchronousMailMessage(id:1, to:[Mary Smith <mary@example.com>, carl@example.com], subject:Subject, status:CREATED)'
    }

}

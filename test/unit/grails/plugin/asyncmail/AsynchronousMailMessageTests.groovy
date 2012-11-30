package grails.plugin.asyncmail

import grails.test.mixin.TestFor

/**
 * Test AsynchronousMailMessage constraints
 */
@TestFor(AsynchronousMailMessage)
class AsynchronousMailMessageTests {
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
        assertEquals MessageStatus.CREATED, message.status
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

    void testValid(){
        // Apply constraints for message objects
        mockForConstraintsTests(AsynchronousMailMessage)

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

    void testConstraints() {
        // Apply constraints for message objects
        mockForConstraintsTests(AsynchronousMailMessage)

        // Constraints on default message
        def message = new AsynchronousMailMessage()
        assertFalse message.validate()
        assertEquals "nullable", message.errors["to"]
        assertEquals "nullable", message.errors["subject"]
        assertEquals "nullable", message.errors["text"]

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
        assertEquals "minSize", message.errors["to"]
        assertEquals "blank", message.errors["subject"]
        assertEquals "blank", message.errors["text"]
        assertEquals "nullable", message.errors["status"]
        assertEquals "nullable", message.errors["createDate"]
        assertEquals "nullable", message.errors["beginDate"]
        assertEquals "validator", message.errors["endDate"]
        assertEquals "min", message.errors["attemptsCount"]
        assertEquals "min", message.errors["maxAttemptsCount"]
        assertEquals "min", message.errors["attemptInterval"]
    }
}

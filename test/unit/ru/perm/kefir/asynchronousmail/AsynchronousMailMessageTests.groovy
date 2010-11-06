package ru.perm.kefir.asynchronousmail

import grails.test.GrailsUnitTestCase

/**
 * Test AsynchronousMailMessage constraints
 */
class AsynchronousMailMessageTests extends GrailsUnitTestCase {
    void testDefault() {
        def message = new AsynchronousMailMessage();
        assertNull message.to;
        assertNull message.subject;
        assertNull message.headers;
        assertNull message.text;
        assertFalse message.html;
        assertNull message.bcc;
        assertNull message.cc;
        assertNull message.replyTo;
        assertNull message.from;
        assertNull message.attachments;
        assertEquals MessageStatus.CREATED, message.status;
        assertNotNull message.createDate;
        assertNull message.sentDate;
        assertNotNull message.beginDate;
        assertNotNull message.endDate;
        assertEquals 0, message.attemptsCount;
        assertEquals 1, message.maxAttemptsCount;
        assertNull message.lastAttemptDate;
        assertEquals 300000l, message.attemptInterval;
        assertFalse message.markDelete;
    }

    void testConstraints() {
        // Apply constraints for message objects
        def existingMessage = new AsynchronousMailMessage();
        mockForConstraintsTests(AsynchronousMailMessage, [existingMessage]);

        // Constraints on default message
        def message = new AsynchronousMailMessage();
        assertFalse message.validate();
        assertEquals "nullable", message.errors["to"];
        assertEquals "nullable", message.errors["subject"];
        assertEquals "nullable", message.errors["text"];

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
        );
        assertFalse message.validate();
        assertEquals "validator", message.errors["to"];
        assertEquals "blank", message.errors["subject"];
        assertEquals "blank", message.errors["text"];
        assertEquals "nullable", message.errors["status"];
        assertEquals "nullable", message.errors["createDate"];
        assertEquals "nullable", message.errors["beginDate"];
        assertEquals "validator", message.errors["endDate"];
        assertEquals "min", message.errors["attemptsCount"];
        assertEquals "min", message.errors["maxAttemptsCount"];
        assertEquals "min", message.errors["attemptInterval"];

        // Minimal valid message
        message = new AsynchronousMailMessage(
                to: ['kefir@perm.ru'],
                subject: 'Subject',
                text: 'Text'
        );
        assertTrue message.validate();
    }
}

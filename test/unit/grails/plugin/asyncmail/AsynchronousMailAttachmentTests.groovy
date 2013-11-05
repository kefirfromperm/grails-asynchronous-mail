package grails.plugin.asyncmail

import grails.test.mixin.TestFor

/**
 * Attachment unit tests
 */
@TestFor(AsynchronousMailAttachment)
class AsynchronousMailAttachmentTests {
    void testDefault(){
        def attachment = new AsynchronousMailAttachment()
        assertNull attachment.attachmentName
        assertEquals AsynchronousMailAttachment.DEFAULT_MIME_TYPE, attachment.mimeType
        assertNull attachment.content
        assertFalse attachment.inline
    }

    void testConstraints(){
        // Apply constraints for attachment objects
        mockForConstraintsTests(AsynchronousMailAttachment)

        // Constraints on default attachment
        def attachment = new AsynchronousMailAttachment()
        assertFalse attachment.validate()
        assertEquals "nullable", attachment.errors['attachmentName']
        assertEquals "nullable", attachment.errors['content']
        assertEquals "nullable", attachment.errors['message']

        attachment = new AsynchronousMailAttachment(
                mimeType:null
        )
        attachment.attachmentName =' \t\n'
        assertFalse attachment.validate()
        assertEquals "blank", attachment.errors['attachmentName']
        assertEquals "nullable", attachment.errors['mimeType']
        assertEquals "nullable", attachment.errors['content']
        assertEquals "nullable", attachment.errors['message']

        // Valid attachment
        attachment = new AsynchronousMailAttachment(
                attachmentName:'name',
                content:'Grails'.getBytes(),
                message: new AsynchronousMailMessage()
        )
        assertTrue attachment.validate()
    }
}

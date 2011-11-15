package grails.plugin.asyncmail

import grails.test.GrailsUnitTestCase
import grails.plugin.asyncmail.AsynchronousMailAttachment
import grails.plugin.asyncmail.AsynchronousMailMessage

/**
 * Attachment unit tests
 */
class AsynchronousMailAttachmentTests extends GrailsUnitTestCase {
    void testDefault(){
        def attachment = new AsynchronousMailAttachment();
        assertNull attachment.attachmentName;
        assertEquals AsynchronousMailAttachment.DEFAULT_MIME_TYPE, attachment.mimeType;
        assertNull attachment.content;
    }

    void testConstraints(){
        // Apply constraints for attachment objects
        def existingAttachment = new AsynchronousMailAttachment();
        mockForConstraintsTests(AsynchronousMailAttachment, [existingAttachment]);

        // Constraints on default attachment
        def attachment = new AsynchronousMailAttachment();
        assertFalse attachment.validate();
        assertEquals "nullable", attachment.errors['attachmentName'];
        assertEquals "nullable", attachment.errors['content'];
        assertEquals "nullable", attachment.errors['message'];

        attachment = new AsynchronousMailAttachment(
                attachmentName:'',
                mimeType:null
        );
        assertFalse attachment.validate();
        assertEquals "blank", attachment.errors['attachmentName'];
        assertEquals "nullable", attachment.errors['mimeType'];
        assertEquals "nullable", attachment.errors['content'];
        assertEquals "nullable", attachment.errors['message'];

        // Valid attachment
        attachment = new AsynchronousMailAttachment(
                attachmentName:'name',
                content:'Grails'.getBytes(),
                message: new AsynchronousMailMessage()
        );
        assertTrue attachment.validate();
    }
}

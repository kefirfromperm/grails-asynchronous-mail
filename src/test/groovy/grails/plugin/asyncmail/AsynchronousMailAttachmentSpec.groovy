package grails.plugin.asyncmail

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

import static grails.plugin.asyncmail.AsynchronousMailAttachment.DEFAULT_MIME_TYPE

/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
class AsynchronousMailAttachmentSpec extends Specification implements DomainUnitTest<AsynchronousMailAttachment> {


    void "testing default constructor"() {
        when:
        AsynchronousMailAttachment attachment = new AsynchronousMailAttachment()

        then:
        !attachment.attachmentName
        attachment.mimeType == DEFAULT_MIME_TYPE
        !attachment.content
        !attachment.inline
    }

    void "testing constraints"() {
        setup:
        AsynchronousMailAttachment attachment

        when: 'constraints on default attachment'
        attachment = new AsynchronousMailAttachment()

        then: 'should fail validation'
        !attachment.validate()
        attachment.errors.getFieldError('attachmentName').code == 'nullable'
        attachment.errors.getFieldError('content').code == 'nullable'
        attachment.errors.getFieldError('message').code == 'nullable'

        when: 'attachmentName is empty'
        attachment = new AsynchronousMailAttachment(attachmentName: '')

        then: 'should fail validation with nullable error on attachmentName'
        !attachment.validate()
        attachment.errors.getFieldError('attachmentName').code == 'nullable'

        when: 'mimeType is null'
        attachment = new AsynchronousMailAttachment(mimeType: null)

        then: 'should fail validation on mimeType'
        !attachment.validate()
        attachment.errors.getFieldError('mimeType').code == 'nullable'

        when: 'a valid attachment'
        attachment =  new AsynchronousMailAttachment(
                attachmentName:'name',
                content:'Grails'.getBytes(),
                message: new AsynchronousMailMessage()
        )

        then: 'should pass all validations'
        attachment.validate()

    }
}

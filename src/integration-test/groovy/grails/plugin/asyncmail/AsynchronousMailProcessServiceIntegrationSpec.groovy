package grails.plugin.asyncmail

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.asyncmail.enums.MessageStatus
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.springframework.transaction.TransactionDefinition
import spock.lang.Specification
import spock.lang.Unroll

import static grails.plugin.asyncmail.enums.MessageStatus.SENT

/**
 * Integration tests for process service
 */
@Integration
@Rollback
class AsynchronousMailProcessServiceIntegrationSpec extends Specification implements GrailsConfigurationAware {
    AsynchronousMailProcessService asynchronousMailProcessService
    Config configuration

    void setup() {
        asynchronousMailProcessService.asynchronousMailSendService = Mock(AsynchronousMailSendService)
    }

    void cleanup() {
        AsynchronousMailMessage.list()*.delete()
        configuration.asynchronous.mail.taskPoolSize = 1
        configuration.asynchronous.mail.useFlushOnSave = true
    }

    @Unroll
    void "test process #messageCount messages with #taskCount tasks and flush=#flush"(
            int messageCount, int taskCount, boolean flush
    ) {
        given: "some messages"
            AsynchronousMailMessage.withTransaction(
                    propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW
            ) {
                for (int i = 0; i < messageCount; i++) {
                    new AsynchronousMailMessage(
                            from: 'john.smith@example.com',
                            to: ['jane.smith@example.con'],
                            subject: 'Subject',
                            text: 'Body'
                    ).save(flush: true, failonError: true)
                }
            }

            configuration.asynchronous.mail.taskPoolSize = taskCount
            configuration.asynchronous.mail.useFlushOnSave = flush
        when: "process"
            asynchronousMailProcessService.findAndSendEmails()
        then: "all messages have status SENT"
            messageCount * asynchronousMailProcessService.asynchronousMailSendService.send(_ as AsynchronousMailMessage)
            AsynchronousMailMessage.countByStatus(SENT) == messageCount
            AsynchronousMailMessage.countByStatusNotEqual(SENT) == 0
        where:
            messageCount | taskCount | flush
            1            | 1         | false
            1            | 10        | false
            10           | 1         | false
            9            | 10        | false
            10           | 10        | false
            11           | 10        | false
            1            | 1         | true
            1            | 10        | true
            10           | 1         | true
            9            | 10        | true
            10           | 10        | true
            11           | 10        | true
    }

    void "test delete messages after sent"() {
        given: "some meeesage with mark delete true"
            AsynchronousMailMessage.withTransaction(
                    propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW
            ) {
                for (int i = 0; i < 5; i++) {
                    new AsynchronousMailMessage(
                            from: 'john.smith@example.com',
                            to: ['jane.smith@example.con'],
                            subject: 'Subject',
                            text: 'Body',
                            markDelete: true
                    ).save(flush: true, failonError: true)
                }
            }
        when: "process them"
            asynchronousMailProcessService.findAndSendEmails()
        then: "all have been deleted"
            5 * asynchronousMailProcessService.asynchronousMailSendService.send(_ as AsynchronousMailMessage)
            AsynchronousMailMessage.count() == 0
    }

    void "test delet attachments"() {
        given: "some meeesage with mark delete true"
            AsynchronousMailMessage.withTransaction(
                    propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW
            ) {
                for (int i = 0; i < 5; i++) {
                    new AsynchronousMailMessage(
                            from: 'john.smith@example.com',
                            to: ['jane.smith@example.con'],
                            subject: 'Subject',
                            text: 'Body',
                            markDeleteAttachments: true,
                            attachments: [
                                    new AsynchronousMailAttachment(
                                            attachmentName: "Attachment $i",
                                            content: '' as byte[]
                                    )
                            ]
                    ).save(flush: true, failonError: true)
                }
            }
        when: "process them"
            asynchronousMailProcessService.findAndSendEmails()
        then: "all have been deleted"
            5 * asynchronousMailProcessService.asynchronousMailSendService.send(_ as AsynchronousMailMessage)
            AsynchronousMailMessage.count() == 5
            AsynchronousMailAttachment.count() == 0
    }

    void "test process error message"() {
        given: "A message with an error in DB"
            AsynchronousMailMessage.withTransaction(
                    propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW
            ) {
                new AsynchronousMailMessage(
                        from: 'john.smith@example.com',
                        subject: 'Subject',
                        text: 'Body'
                ).save(flush: true, failonError: true, validate: false)
            }
        when: "process"
            asynchronousMailProcessService.findAndSendEmails()
        then: "it's status is error"
            AsynchronousMailMessage.list()[0].status == MessageStatus.ERROR
    }
}

package grails.plugin.asyncmail

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.transaction.TransactionDefinition
import spock.lang.Specification
import spock.lang.Unroll

import static grails.plugin.asyncmail.enums.MessageStatus.SENT

/**
 * Integration tests for process service
 */
@Integration
@Rollback
class AsynchronousMailProcessServiceIntegrationSpec extends Specification {
    AsynchronousMailProcessService asynchronousMailProcessService

    void setup() {
        asynchronousMailProcessService.asynchronousMailSendService = Mock(AsynchronousMailSendService)
    }

    void cleanup() {
        AsynchronousMailMessage.list()*.delete()
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
                    ).save(flush:true, failonError:true)
                }
            }

            asynchronousMailProcessService.configuration.asynchronous.mail.taskPoolSize = taskCount
            asynchronousMailProcessService.configuration.asynchronous.mail.useFlushOnSave = flush
        when: "process"
            asynchronousMailProcessService.findAndSendEmails()
        then: "all messages have status SENT"
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
}

package grails.plugin.asyncmail

import groovy.util.logging.Slf4j

@Slf4j
class ExpiredMessagesCollectorJob {
    static triggers = {}

    def concurrent = false
    def group = "AsynchronousMail"

    AsynchronousMailPersistenceService asynchronousMailPersistenceService

    def execute() {
        log.trace('Entering execute method.')
        asynchronousMailPersistenceService.updateExpiredMessages()
        log.trace('Exiting execute method.')
    }
}

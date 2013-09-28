package grails.plugin.asyncmail

class ExpiredMessagesCollectorJob {
    static triggers = {}

    def concurrent = false
    def group = "AsynchronousMail"

    AsynchronousMailPersistenceService asynchronousMailPersistenceService

    def execute() {
        log.trace('Enter to execute method.')
        asynchronousMailPersistenceService.updateExpiredMessages()
        log.trace('Exit from execute method.')
    }
}

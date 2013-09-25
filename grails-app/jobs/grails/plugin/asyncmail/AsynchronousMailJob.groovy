package grails.plugin.asyncmail

/**
 * Send asynchronous messages
 */
class AsynchronousMailJob {
    static triggers = {}

    def concurrent = false
    def group = "AsynchronousMail"

    // Dependency injection
    def asynchronousMailPersistenceService

    def execute() {
        log.trace('Enter to execute method.')

        def startDate = new Date().time
        asynchronousMailPersistenceService.findAndSendEmails()
        def endDate = new Date().time
        log.debug("Execution time = ${endDate - startDate}")

        log.trace("Exit from execute method.");
    }
}

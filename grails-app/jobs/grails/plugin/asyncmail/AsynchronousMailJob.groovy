package grails.plugin.asyncmail

/**
 * Send asynchronous messages
 */
class AsynchronousMailJob {
    static triggers = {}

    def concurrent = false
    def group = "AsynchronousMail"

    // Dependency injection
    AsynchronousMailProcessService asynchronousMailProcessService

    def execute() {
        log.trace('Entering execute method.')
        def startDate = System.currentTimeMillis()

        asynchronousMailProcessService.findAndSendEmails()

        def endDate = System.currentTimeMillis()
        log.trace("Exiting execute method. Execution time = ${endDate - startDate}ms");
    }
}

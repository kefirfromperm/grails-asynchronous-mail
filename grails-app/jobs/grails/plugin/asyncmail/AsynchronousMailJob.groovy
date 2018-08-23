package grails.plugin.asyncmail

import groovy.util.logging.Slf4j

/**
 * Send asynchronous messages
 */
@Slf4j
class AsynchronousMailJob {
    static triggers = {}

    static concurrent = false
    static group = "AsynchronousMail"

    // Dependency injection
    AsynchronousMailProcessService asynchronousMailProcessService

    def execute() {
        log.trace('Entering execute method.')
        def startDate = System.currentTimeMillis()

        asynchronousMailProcessService.findAndSendEmails()

        def endDate = System.currentTimeMillis()
        log.trace("Exiting execute method. Execution time = ${endDate - startDate}ms")
    }
}

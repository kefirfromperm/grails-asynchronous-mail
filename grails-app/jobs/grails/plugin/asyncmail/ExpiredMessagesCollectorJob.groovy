package grails.plugin.asyncmail

class ExpiredMessagesCollectorJob {
    static triggers = {
        if (!config.asynchronous.mail.disable) {
            simple([repeatInterval: (Long) config.asynchronous.mail.expired.collector.repeat.interval])
        }
    }

    def concurrent = false
    def group = "AsynchronousMail"

    def execute() {
        log.trace('Enter to execute method.')
        int count = 0
        AsynchronousMailMessage.withTransaction {
            count = AsynchronousMailMessage.executeUpdate(
                    "update AsynchronousMailMessage amm set amm.status=:es where amm.endDate<:date and (amm.status=:cs or amm.status=:as)",
                    ["es": MessageStatus.EXPIRED, "date": new Date(), "cs": MessageStatus.CREATED, "as": MessageStatus.ATTEMPTED]
            )
        }
        log.trace("${count} expired messages was updated.")
        log.trace('Exit from execute method.')
    }
}

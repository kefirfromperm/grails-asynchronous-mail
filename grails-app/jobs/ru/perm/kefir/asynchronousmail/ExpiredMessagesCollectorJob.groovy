package ru.perm.kefir.asynchronousmail


class ExpiredMessagesCollectorJob {
    static triggers = {}
    def concurrent = false;

    def execute() {
        log.trace('Enter to execute method');
        AsynchronousMailMessage.withCriteria {
            lt('endDate', new Date());
            or {
                eq('status', MessageStatus.CREATED);
                eq('status', MessageStatus.ATTEMPTED);
            }
        }.each {AsynchronousMailMessage message ->
            message.status = MessageStatus.EXPIRED;
            message.save(flush: true);
        }
    }
}

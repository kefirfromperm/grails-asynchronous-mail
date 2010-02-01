import org.codehaus.groovy.grails.commons.ConfigurationHolder

import org.quartz.SimpleTrigger

import ru.perm.kefir.asynchronousmail.AsynchronousMailJob
import ru.perm.kefir.asynchronousmail.ExpiredMessagesCollectorJob

/** Start the jobs  */
class AsynchronousMailBootStrap {
    def init = {servletContext ->
        // Start the send job
        AsynchronousMailJob.schedule(
                (Long) ConfigurationHolder.config.asynchronous.mail.send.repeat.interval,
                SimpleTrigger.REPEAT_INDEFINITELY,
                ['messagesAtOnce': ConfigurationHolder.config.asynchronous.mail.messages.at.once]
        );

        // Start expired messages collector
        ExpiredMessagesCollectorJob.schedule(
                (Long) ConfigurationHolder.config.asynchronous.mail.expired.collector.repeat.interval,
                SimpleTrigger.REPEAT_INDEFINITELY, null
        );
    }
    def destroy = {}
}
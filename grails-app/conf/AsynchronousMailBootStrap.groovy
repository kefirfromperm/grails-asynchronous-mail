import grails.plugin.asyncmail.AsynchronousMailJob
import grails.plugin.asyncmail.ExpiredMessagesCollectorJob
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.quartz.SimpleTrigger

/** Start the jobs  */
class AsynchronousMailBootStrap {
    GrailsApplication grailsApplication;

    def init = {servletContext ->
        def config = grailsApplication.config;

        // Start the send job
        AsynchronousMailJob.schedule(
                (Long) config.asynchronous.mail.send.repeat.interval,
                SimpleTrigger.REPEAT_INDEFINITELY,
                ['messagesAtOnce': config.asynchronous.mail.messages.at.once]
        );

        // Start expired messages collector
        ExpiredMessagesCollectorJob.schedule(
                (Long) config.asynchronous.mail.expired.collector.repeat.interval,
                SimpleTrigger.REPEAT_INDEFINITELY, null
        );
    }
    def destroy = {}
}
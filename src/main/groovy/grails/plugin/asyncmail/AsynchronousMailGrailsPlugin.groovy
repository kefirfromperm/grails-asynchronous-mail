package grails.plugin.asyncmail

import grails.plugins.Plugin
import grails.plugins.quartz.JobDescriptor
import grails.plugins.quartz.JobManagerService
import grails.plugins.quartz.TriggerDescriptor
import groovy.util.logging.Slf4j
import org.quartz.Scheduler
import org.quartz.TriggerKey

@Slf4j
class AsynchronousMailGrailsPlugin extends Plugin {
    def grailsVersion = "3.3.0 > *"
    def loadAfter = ['mail', 'quartz', 'hibernate', 'hibernate3', 'hibernate4', 'hibernate5', 'mongodb']

    Closure doWithSpring() { { ->
            asynchronousMailMessageBuilderFactory(AsynchronousMailMessageBuilderFactory) {
                it.autowire = true
            }

            springConfig.addAlias 'asyncMailService', 'asynchronousMailService'
        }
    }

    @Override
    void onStartup(Map<String, Object> event) {
        // Starts jobs
        startJobs(applicationContext)
    }

    /**
     * Start the send job and the messages collector.
     *
     * If the plugin is used in cluster we have to remove old triggers.
     */
    def startJobs(applicationContext) {
        def asyncMailConfig = grailsApplication.config.asynchronous.mail
        if (!asyncMailConfig.disable) {
            JobManagerService jobManagerService = applicationContext.jobManagerService
            Scheduler quartzScheduler = applicationContext.quartzScheduler

            // Get our jobs
            List<JobDescriptor> jobDescriptors = jobManagerService.getJobs("AsynchronousMail")

            // Remove old triggers for the send job
            log.debug("Removing old triggers for the AsynchronousMailJob")
            JobDescriptor sjd = jobDescriptors.find { it.name == 'grails.plugin.asyncmail.AsynchronousMailJob' }
            sjd?.triggerDescriptors?.each {TriggerDescriptor td ->
                def triggerKey = new TriggerKey(td.name, td.group)
                quartzScheduler.unscheduleJob(triggerKey)
                log.debug("Removed the trigger ${triggerKey} for the AsynchronousMailJob")
            }

            // Schedule the send job
            def sendInterval = (Long) asyncMailConfig.send.repeat.interval
            log.debug("Scheduling the AsynchronousMailJob with repeat interval ${sendInterval}ms")
            AsynchronousMailJob.schedule(sendInterval)
            log.debug("Scheduled the AsynchronousMailJob with repeat interval ${sendInterval}ms")

            // Remove old triggers for the collector job
            log.debug("Removing old triggers for the ExpiredMessagesCollectorJob")
            JobDescriptor cjd = jobDescriptors.find { it.name == 'grails.plugin.asyncmail.ExpiredMessagesCollectorJob' }
            cjd?.triggerDescriptors?.each {TriggerDescriptor td ->
                def triggerKey = new TriggerKey(td.name, td.group)
                quartzScheduler.unscheduleJob(triggerKey)
                log.debug("Removed the trigger ${triggerKey} for the ExpiredMessagesCollectorJob")
            }

            // Schedule the collector job
            def collectInterval = (Long) asyncMailConfig.expired.collector.repeat.interval
            log.debug("Scheduling the ExpiredMessagesCollectorJob with repeat interval ${collectInterval}ms")
            ExpiredMessagesCollectorJob.schedule(collectInterval)
            log.debug("Scheduled the ExpiredMessagesCollectorJob with repeat interval ${collectInterval}ms")
        }
    }
}

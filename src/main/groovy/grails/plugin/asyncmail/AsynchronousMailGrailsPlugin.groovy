package grails.plugin.asyncmail

import grails.plugins.Plugin
import grails.plugins.mail.MailService
import grails.plugins.quartz.JobDescriptor
import grails.plugins.quartz.JobManagerService
import grails.plugins.quartz.TriggerDescriptor
import groovy.util.logging.Commons
import org.quartz.Scheduler
import org.quartz.TriggerKey
import org.springframework.context.ApplicationContext

@Commons
class AsynchronousMailGrailsPlugin extends Plugin {
    def version = "2.0.0-SNAPSHOT"
    def grailsVersion = "3.0.1 > *"
    def loadAfter = ['mail', 'quartz', 'hibernate', 'hibernate4', 'mongodb']

    def author = "Vitalii Samolovskikh"
    def authorEmail = "kefirfromperm@gmail.com"
    def title = "Asynchronous Mail Plugin"
    def description = 'The plugin realises asynchronous mail sending. ' +
            'It stores messages in the DB and sends them asynchronously by the quartz job.'
    def documentation = "http://www.grails.org/plugin/asynchronous-mail"

    String license = 'APACHE'

    def developers = [
            [name: 'Vitalii Samolovskikh', email: 'kefirfromperm@gmail.com'],
            [ name: "Puneet Behl", email: "puneet.behl007@gmail.com" ],
            [name: 'Sergey Ponomarev', email: 'stokito@gmail.com'],
            [name: 'Danny Casady'],
            [name: 'Shashank Agrawal'],
            [name: 'Iván López', email:'lopez.ivan@gmail.com'],
            [name: 'Alessandro Berbenni'],
            [name: 'Burt Beckwith', email:'burt@burtbeckwith.com']
    ]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/kefirfromperm/grails-asynchronous-mail/issues']
    def scm = [url: 'https://github.com/kefirfromperm/grails-asynchronous-mail']

    Closure doWithSpring() { { ->

            // The mail service from Mail plugin
            nonAsynchronousMailService(MailService) {
                mailMessageBuilderFactory = ref("mailMessageBuilderFactory")
                grailsApplication = grailsApplication
            }

            asynchronousMailMessageBuilderFactory(AsynchronousMailMessageBuilderFactory) {
                it.autowire = true
            }
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }


    void doWithApplicationContext() {
        // Configure sendMail methods
        configureSendMail(grailsApplication, applicationContext)

        // Starts jobs
        startJobs(grailsApplication, applicationContext)
    }

    void onChange(Map<String, Object> event) {
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        // Configure sendMail methods
        configureSendMail(grailsApplication, (ApplicationContext) event.ctx)
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }


    /**
     * Start the send job and the messages collector.
     *
     * If the plugin is used in cluster we have to remove old triggers.
     */
    def startJobs(application, applicationContext) {
        def asyncMailConfig = application.config.asynchronous.mail
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

    /**
     * Configure sendMail methods
     */
    static configureSendMail(application, ApplicationContext applicationContext){
        def asyncMailConfig = application.config.asynchronous.mail

        // Override the mailService
        if (asyncMailConfig.override) {
            applicationContext.mailService.metaClass*.sendMail = { Closure callable ->
                applicationContext.asynchronousMailService?.sendAsynchronousMail(callable)
            }
        } else {
            applicationContext.asynchronousMailService.metaClass*.sendMail = { Closure callable ->
                applicationContext.asynchronousMailService?.sendAsynchronousMail(callable)
            }
        }
    }
}

import grails.plugin.asyncmail.AsynchronousMailMessageBuilderFactory
import grails.plugin.mail.MailService
import grails.util.Environment

import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext

class AsynchronousMailGrailsPlugin {

    def version = "1.0-RC4"
    def grailsVersion = "2.2.1 > *"
    def loadAfter = ['mail', 'hibernate']
    def loadBefore = ['quartz']
    def pluginExcludes = [
            "grails-app/conf/DataSource.groovy",
            "grails-app/i18n/**",
            "grails-app/views/test/**",
            "web-app/WEB-INF/**",
            "web-app/images/**",
            "web-app/js/**",
            "web-app/css/errors.css",
            "web-app/css/main.css",
            "web-app/css/mobile.css"
    ]

    def author = "Vitalii Samolovskikh aka Kefir"
    def authorEmail = "kefir@perm.ru"
    def title = "Asynchronous Mail Plugin"
    def description = 'The plugin realises asynchronous mail sending. ' +
            'It stores messages in the DB and sends them asynchronously by the quartz job.'
    def documentation = "http://www.grails.org/plugin/asynchronous-mail"

    String license = 'APACHE'
    def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPASYNCHRONOUSMAIL']
    def scm = [url: 'https://github.com/kefirfromperm/grails-asynchronous-mail']

    def doWithSpring = {
        def config = application.config
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().classLoader)
        // merging default config into main application config
        config.merge(new ConfigSlurper(Environment.current.name).parse(classLoader.loadClass('DefaultAsynchronousMailConfig')))

        // merging user-defined config into main application config if provided
        try {
            config.merge(new ConfigSlurper(Environment.current.name).parse(classLoader.loadClass('AsynchronousMailConfig')))
        } catch (Exception ignored) {
            // ignore, just use the defaults
        }

        nonAsynchronousMailService(MailService) {
            mailMessageBuilderFactory = ref("mailMessageBuilderFactory")
            grailsApplication = ref("grailsApplication")
        }

        asynchronousMailMessageBuilderFactory(AsynchronousMailMessageBuilderFactory) {
            it.autowire = true
        }
    }

    def doWithApplicationContext = { GrailsApplicationContext applicationContext ->
        configureSendMail(application, applicationContext)
    }

    def onChange = { event ->
        configureSendMail(application, (GrailsApplicationContext) event.ctx)
    }

    def configureSendMail(application, GrailsApplicationContext applicationContext) {
        // Register alias for asynchronousMailService
        applicationContext.registerAlias('asynchronousMailService', 'asyncMailService')

        // Override mailService
        if (application.config.asynchronous.mail.override) {
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

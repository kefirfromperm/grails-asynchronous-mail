import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.grails.mail.MailService

class AsynchronousMailGrailsPlugin {
    // the plugin version
    def version = "0.2.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['mail': '0.9 > *', 'quartz': '0.4.2 > *', 'hibernate': '1.2.3 > *']
    def loadAfter = ['mail', 'quartz', 'hibernate'];
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Vitaliy Samolovskih aka Kefir"
    def authorEmail = "kefir@perm.ru"
    def title = "Asynchronous mail grails plugin"
    def description = '''\\
This plugin realise asynchronous mail sent. It place messages to DB and sent them by quartz job asynchronously.
'''

    // URL to the plugin's documentation
    def documentation = "http://www.grails.org/plugin/asynchronous-mail"

    def doWithSpring = {
        def config = ConfigurationHolder.config
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().classLoader)

        // merging default config into main application config
        config.merge(new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('DefaultAsynchronousMailConfig')))

        // merging user-defined config into main application config if provided
        try {
            config.merge(new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('AsynchronousMailConfig')))
        } catch (Exception ignored) {
            // ignore, just use the defaults
        }

        nonAsynchronousMailService(MailService) {
            mailSender = ref("mailSender")
        }
    }

    def doWithApplicationContext = { applicationContext ->
        configureSendMail(application, applicationContext)
    }

    def onChange = {event ->
        configureSendMail(event.application, event.ctx)
    }

    def configureSendMail(application, applicationContext) {
        // Override mailService
        if (ConfigurationHolder.config.asynchronous.mail.override) {
            applicationContext.mailService.metaClass*.sendMail = {Closure callable ->
                applicationContext.asynchronousMailService?.sendAsynchronousMail(callable)
            }
        }
    }
}

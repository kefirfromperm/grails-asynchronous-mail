import grails.plugin.asyncmail.AsynchronousMailMessageBuilderFactory
import grails.plugin.mail.MailService
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext

class AsynchronousMailGrailsPlugin {
    // the plugin version
    def version = "0.3-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['mail': '1.0 > *', 'quartz': '0.4.2 > *', 'hibernate': '2.0.0 > *']
    def loadAfter = ['mail', 'quartz', 'hibernate'];
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/test/html.gsp",
            "grails-app/views/test/plain.gsp"
    ]

    def author = "Vitalii Samolovskikh aka Kefir"
    def authorEmail = "kefir@perm.ru"
    def title = "Asynchronous mail grails plugin"
    def description = '''\\
This plugin realise asynchronous mail sent. It place messages to DB and sent them by quartz job asynchronously.
'''

    // URL to the plugin's documentation
    def documentation = "http://www.grails.org/plugin/asynchronous-mail"

    def doWithSpring = {
        def config = application.config
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
            mailMessageBuilderFactory = ref("mailMessageBuilderFactory");
        }

        asynchronousMailMessageBuilderFactory(AsynchronousMailMessageBuilderFactory){
            it.autowire = true
        }
    }

    def doWithApplicationContext = {GrailsApplicationContext applicationContext ->
        configureSendMail(application, applicationContext)
    }

    def onChange = {event ->
        configureSendMail(event.application, event.ctx)
    }

    def configureSendMail(application, GrailsApplicationContext applicationContext) {
        // Register alias for asynchronousMailService
        applicationContext.registerAlias('asynchronousMailService', 'asyncMailService');

        // Override mailService
        if (application.config.asynchronous.mail.override) {
            applicationContext.mailService.metaClass*.sendMail = {Closure callable ->
                applicationContext.asynchronousMailService?.sendAsynchronousMail(callable)
            }
        }
    }
}

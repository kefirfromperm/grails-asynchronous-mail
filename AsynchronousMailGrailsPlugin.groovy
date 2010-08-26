import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AsynchronousMailGrailsPlugin {
    // the plugin version
    def version = "0.1.5"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['mail':'0.9 > *','quartz':'0.4.2 > *','hibernate':'1.2.3 > *']
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
    }
}

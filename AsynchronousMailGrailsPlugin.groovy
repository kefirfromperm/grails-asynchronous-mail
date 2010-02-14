import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AsynchronousMailGrailsPlugin {
    // the plugin version
    def version = "0.1.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1.2"
    // the other plugins this plugin depends on
    def dependsOn = ['mail':'0.9 > *','quartz':'0.4.1 > *','hibernate':'1.1.2 > *']
    def loadAfter = ['quartz', 'mail', 'hibernate'];
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Vitaliy Samolovskih aka Kefir"
    def authorEmail = "kefir@perm.ru"
    def title = "Asynchronous mail grails plugin"
    def description = '''\\
This plugin realise asynchronous mail sent.
'''

    // URL to the plugin's documentation
    def documentation = "http://www.grails.org/plugin/asynchronous-mail"

    def doWithSpring = {
        def config = ConfigurationHolder.config
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().classLoader)

        // merging default Quartz config into main application config
        config.merge(new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('DefaultAsynchronousMailConfig')))

        // merging user-defined Quartz config into main application config if provided
        try {
            config.merge(new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('AsynchronousMailConfig')))
        } catch (Exception ignored) {
            // ignore, just use the defaults
        }
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}

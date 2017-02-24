import grails.util.BuildSettings
import grails.util.Environment


// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

if(Environment.current == Environment.DEVELOPMENT) {
    def targetDir = BuildSettings.TARGET_DIR
    if(targetDir) {

        appender("FULL_STACKTRACE", FileAppender) {

            file = "${targetDir}/stacktrace.log"
            append = true
            encoder(PatternLayoutEncoder) {
                pattern = "%level %logger - %msg%n"
            }
        }
        logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false )
    }
}

// Enable Asynchronous Mail plugin logging
logger('grails.app.jobs.grails.plugin.asyncmail', TRACE, ['STDOUT'], false)
logger('grails.app.services.grails.plugin.asyncmail', TRACE, ['STDOUT'], false)
logger('grails.plugin.asyncmail', TRACE, ['STDOUT'], false)

// Enable Quartz plugin logging
logger('grails.plugins.quartz', DEBUG, ['STDOUT'], false)
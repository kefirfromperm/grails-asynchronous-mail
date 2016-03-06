/**
 * Create the config file for the Grails Asynchronous Mail plugin
 */
target(installAsynchronousMailConfig: 'Create the config file for the Grails Asynchronous Mail plugin') {
    ant.copy(
            file: "${asynchronousMailPluginDir}/grails-app/conf/DefaultAsynchronousMailConfig.groovy",
            tofile: "$basedir/grails-app/conf/AsynchronousMailConfig.groovy",
            overwrite: false
    )
}

setDefaultTarget 'installAsynchronousMailConfig'

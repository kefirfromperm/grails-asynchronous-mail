/**
 * Create config file for asynchronous mail
 */

target(installAsynchronousMailConfig: 'Create config file for asynchronous mail grails plugin') {
    ant.copy(
            file: "${asynchronousMailPluginDir}/grails-app/conf/DefaultAsynchronousMailConfig.groovy",
            tofile: "$basedir/grails-app/conf/AsynchronousMailConfig.groovy",
            overwrite: false
    )
}

setDefaultTarget 'installAsynchronousMailConfig'

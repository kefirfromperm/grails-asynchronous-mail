/**
 * Create config file for asynchronous mail
 */

target('default': 'Create config file for asynchronous mail grails plugin') {
    Ant.copy(
            file: "${asynchronousMailPluginDir}/grails-app/conf/DefaultAsynchronousMailConfig.groovy",
            tofile: "$basedir/grails-app/conf/AsynchronousMailConfig.groovy",
            overwrite: false
    );
}
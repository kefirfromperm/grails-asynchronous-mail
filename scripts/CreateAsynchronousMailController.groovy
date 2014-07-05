import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("_GrailsInit")

/**
 * Create the controller and views for manage asynchronous messages.
 */
target(createAsynchronousMailController: "Create the controller and views for manage asynchronous messages.") {
    def packageName = argsMap?.package?:metadata.'app.name'?.replaceAll(/\-/, '')?:'app'

    File template = new File(
            "${asynchronousMailPluginDir}/src/templates/controllers/AsynchronousMailController.groovy"
    );

    def dirName
    if(!packageName.isEmpty()) {
        dirName = "${packageName.replaceAll(/\./, '/')}/"
    } else {
        dirName = '';
    }
    File out = new File("$basedir/grails-app/controllers/${dirName}AsynchronousMailController.groovy");

    if(!out.exists()){
        // in case it's in a package, create dirs
        ant.mkdir dir: out.parentFile

        out.withWriter { writer ->
            templateEngine = new SimpleTemplateEngine()
            templateEngine.createTemplate(template.text).make([packageName:packageName]).writeTo(writer)
        }
    }

    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/list.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/list.gsp",
            overwrite: false
    )
    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/show.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/show.gsp",
            overwrite: false
    )
    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/edit.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/edit.gsp",
            overwrite: false
    )
    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/_listAddr.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/_listAddr.gsp",
            overwrite: false
    )
    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/_flashMessage.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/_flashMessage.gsp",
            overwrite: false
    )

    event('StatusUpdate', ['The controller and views are created'])
}

setDefaultTarget 'createAsynchronousMailController'

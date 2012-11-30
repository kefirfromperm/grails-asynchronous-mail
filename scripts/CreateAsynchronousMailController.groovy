target(createAsynchronousMailController: "Create controller for manage asynchronous messages") {
    ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/controllers/AsynchronousMailController.groovy",
            tofile: "$basedir/grails-app/controllers/AsynchronousMailController.groovy",
            overwrite: false
    )
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
}

setDefaultTarget 'createAsynchronousMailController'

target('default': "Create controller for manage asynchronous messages") {
    Ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/controllers/AsynchronousMailController.groovy",
            tofile: "$basedir/grails-app/controllers/AsynchronousMailController.groovy",
            overwrite: false
    );
    Ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/list.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/list.gsp",
            overwrite: false
    );
    Ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/show.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/show.gsp",
            overwrite: false
    );
    Ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/views/asynchronousMail/edit.gsp",
            tofile: "$basedir/grails-app/views/asynchronousMail/edit.gsp",
            overwrite: false
    );
}

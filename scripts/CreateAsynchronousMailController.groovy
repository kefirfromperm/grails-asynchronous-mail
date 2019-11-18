import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

/**
 * Create a new controller and views for managing asynchronous messages.
 */
target('default': "Creates a new controller and views for managing asynchronous messages.") {

    depends(checkVersion, parseArguments)

    def type = "AsynchronousMailController"
    def name = argsMap["params"][0]

    if (name) {
        name = purgeRedundantArtifactSuffix(name, type)
    } else {
        name = "AsynchronousMail"
    }

    createArtifact(name: name, suffix: "Controller", type: type, path: "grails-app/controllers")
    createUnitTest(name: name, suffix: "Controller", superClass: "ControllerUnitTestCase")

    def views = ["list", "show", "edit", "_listAddr", "_flashMessage"]

    views.each() {
        def toFile = "${basedir}/grails-app/views/${propertyName}/${it}.gsp"
        ant.copy(
            file: "${asynchronousMailPluginDir}/src/templates/scaffolding/${it}.gsp",
            tofile: toFile,
            overwrite: false
        )
        event("CreatedFile", [toFile])
    }

}

USAGE = """
    create-asynchronous-mail-controller [NAME]
where
    NAME       = The name of the controller. If not provided, a
                 default of 'AsynchronousMail' will be used.
"""

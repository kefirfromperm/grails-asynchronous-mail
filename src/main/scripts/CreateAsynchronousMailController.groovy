description("Creates a new Asynchronous Mail Controller for managing asynchronous messages.") {
    usage "grails create-asynchornous-mail-controller [CONTROLLER NAME]"
    argument name: 'Controller Name', description: "The name of the controller"
}

def m
if (args[0]) {
    m = model(args[0])
} else {
    m = model('asynchronous.mail.AsynchronousMailController')
}

render template: "artifacts/AsynchronousMailController.groovy",
        destination: file("grails-app/controllers/$m.packagePath/${m.convention('Controller')}.groovy"),
        model: m

copy {
    from templates("scaffolding/*.gsp")
    into "grails-app/views/${m.propertyName}"
}

description("Creates a new Asynchronous Mail Controller for managing asynchronous messages.") {
    usage "grails create-asynchornous-mail-controller [CONTROLLER NAME]"
    argument name: 'Controller Name', description: "The name of the controller"
}

def model
if (args[0]) {
    model = model(args[0])
} else {
    model = model('asynchronous.mail.AsynchronousMailController')
}

render template: "artifacts/AsynchronousMailController.groovy",
        destination: file("grails-app/controllers/$model.packagePath/${model.convention('Controller')}.groovy"),
        model: model

copy {
    from templates("scaffolding/*.gsp")
    into "grails-app/views/${model.propertyName}"
}

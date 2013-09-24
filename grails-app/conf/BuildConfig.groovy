grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
    }

    plugins {
        runtime(":hibernate:$grailsVersion") {
            export = false
        }

        compile ':mail:1.0.1'
        compile ':quartz:1.0-RC9'

        build(':release:2.1.0', ':rest-client-builder:1.0.3') {
            export = false
        }
    }
}

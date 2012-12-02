grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
    }

    plugins {

        compile ':mail:1.0.1'
        compile ':quartz2:0.2.3'

        build(':release:2.1.0', ':rest-client-builder:1.0.2') {
            export = false
        }
    }
}

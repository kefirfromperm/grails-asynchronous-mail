grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
    }

    dependencies {
        compile "org.codehaus.gpars:gpars:1.0.0"
        compile "org.codehaus.jsr166-mirror:jsr166y:1.7.0"
    }

    plugins {
        runtime(":hibernate:2.0.0") {
            export = false
        }

        compile(':mail:1.0.1'){
            excludes 'spring-test'
        }
        compile ':quartz:1.0-RC10'

        build(':release:2.1.0', ':rest-client-builder:1.0.3') {
            export = false
        }
    }
}

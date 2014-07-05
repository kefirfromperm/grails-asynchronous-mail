grails.project.work.dir = 'target'
grails.project.source.level = 1.6
grails.project.dependency.resolver = "maven" // or ivy

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        grailsPlugins()
        grailsHome()

        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        mavenRepo 'http://snapshots.repository.codehaus.org'
        mavenRepo 'http://repository.codehaus.org'
        mavenRepo 'http://download.java.net/maven/2/'
        mavenRepo 'http://repository.jboss.com/maven2/'
    }

    dependencies {
        compile "org.codehaus.gpars:gpars:1.0.0"
        compile "org.codehaus.jsr166-mirror:jsr166y:1.7.0"
    }

    plugins {
        test(":hibernate:3.6.10.15") {
            export = false
        }

        compile(':mail:1.0.6'){
            excludes 'spring-test'
        }
        compile ':quartz:1.0.2'

        build(':release:3.0.1', ':rest-client-builder:1.0.3') {
            export = false
        }
    }
}

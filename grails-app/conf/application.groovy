grails {
    profile = 'web-plugin'
    codegen {
        defaultPackage = 'grails.plugin.asyncmail'
    }
}
info {
    app {
        name = '@info.app.name@'
        version = '@info.app.version@'
        grailsVersion = '@info.app.grailsVersion@'
    }
}
spring {
    groovy {
        template."check-template-location" = false
    }
}

environments {
    test {
        hibernate.dialect = 'org.hibernate.dialect.H2Dialect'
        dataSource {
            pooled = true
            lazy = false
            jmxExport = true
            driverClassName = 'org.h2.Driver'
            username = 'sa'
            password = ''
            dbCreate = 'create-drop'
            url = 'jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE'
        }
        quartz.jdbcStore = false
    }
}


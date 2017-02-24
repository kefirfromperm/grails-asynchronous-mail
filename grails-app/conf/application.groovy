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
        dataSource {
            pooled = true
            jmxExport = true
            driverClassName = 'org.h2.Driver'
            username = 'sa'
            password = ''
            dbCreate = 'update'
            url = 'jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE'
        }
        quartz.jdbcStore = false
    }
}


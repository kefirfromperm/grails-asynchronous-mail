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

asynchronous.mail.default.attempt.interval=300000l       // Five minutes
asynchronous.mail.default.max.attempts.count=1
asynchronous.mail.send.repeat.interval=60000l           // One minute
asynchronous.mail.expired.collector.repeat.interval=607000l
asynchronous.mail.messages.at.once=100
asynchronous.mail.send.immediately=true
asynchronous.mail.override=false
asynchronous.mail.clear.after.sent=false
asynchronous.mail.disable=false
asynchronous.mail.useFlushOnSave=true
asynchronous.mail.persistence.provider='hibernate4'      // Possible values are 'hibernate', 'hibernate4', 'mongodb'
asynchronous.mail.gparsPoolSize=1
asynchronous.mail.newSessionOnImmediateSend=false

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


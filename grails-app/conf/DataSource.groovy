// environment specific settings
environments {
    test {
        dataSource {
            pooled = true
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
        hibernate {
            cache.use_second_level_cache = false
            cache.use_query_cache = false
        }
    }
}

Running RunSQL plugin before FlywayDB
-------------------------------------
Kotlin version:

    task<RunSQL>("createEmptyDatabase") {
        config {
            // ... Connection properties ...
        }
    }
    
    task("initDb") {
        dependsOn("createEmptyDatabase")
        dependsOn("flywayMigrate").mustRunAfter("createDatabase")
        dependsOn("flywayInfo").mustRunAfter("flywayMigrate")
    }
    
By running:

    ./gradlew initdb
    
The following tasks will be executed in order:
- createEmptyDatabase
- flywayMigrate
- flywayInfo

This will allow creation of a new database and applying all migrations on it.

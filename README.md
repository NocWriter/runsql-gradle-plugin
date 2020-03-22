Run SQL Gradle plugin
=====================

Gradle plugin for running SQL scripts.

Why should I use it ? Any alternatives ? I have a question ...
--------------------------------------------------------------
There's a good [FAQ](docs/FAQ.md) - you may find some answers over there.

How to use this plugin?
-----------------------
Firstly, you'll need to include the plugin in your build script:

For Groovy-based DSL:

    plugins {
        id "com.nocwriter.runsql" version "1.0"
    }

For Kotlin-based DSL:

    plugins {
        id("com.nocwriter.runsql") version ("1.0")
    }
    
Next, you'll need to create a custom task:

For Groovy-based DSL:

    task createTable(type: RunSQL) {
        config {
            username = "..."
            password = "..."
            url = "..."
            driverClassName = "..."
            scriptFile = "/db/createTable.sql"
        }
    }

For Kotlin-based DSL:

    task<RunSQL>("createTable") {
        config {
                username = "..."
                password = "..."
                url = "..."
                driverClassName = "..."
                scriptFile = "/db/createTable.sql"
        }
    }
    
Then simple run:

    gradle :createTable

Scripts
----------------
You can provide either a single script file or multiple scripts via 'scriptFile' properties:

    task<RunSQL>("createTable") {
        config {
            username = "..."
            password = "..."
            url = "..."
            driverClassName = "..."
            scriptFile = arrayOf("/db/createTable.sql", "/db/populateDate.sql")
        }
    }
    
You can also provide a direct script, without an external files, e.g.:

    task<RunSQL>("createTable") {
        config {
            username = "..."
            password = "..."
            url = "..."
            driverClassName = "..."
            
            // Use of 'script' instead of 'scriptFile':
            script = "CREATE TABLE books (" +
                     "  name VARCHAR(100)," +
                     "  author VARCHAR(100)" +
                     ");
        }
    }

You cannot use both.

JDBC driver class name
----------------------
_driverClassName_ property is optional. The plugin will attempt to detect the matching driver class name.
As of now, it supports the following: PostgreSQL, MySQL, Oracle, DB2, Sybase, HSQLDB, H2, SQLite and Derby.

System requirements
-------------------
The plugin requires Java 11 and above and Gradle 6 or above.

License
-------
This plugin is licensed under [Apache License V2.0](LICENSE).

How to contact you
------------------
If you'd like to contribute, ask questions or request for new features, you
should open the
[New issues](https://github.com/NocWriter/runsql-gradle-plugin/issues/new)
page and leave a message over there (with E-mail to contact you back).

I don't publish my E-mail address directly, as bots tend to abuse it.
 
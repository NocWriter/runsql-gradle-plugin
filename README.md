Run SQL Gradle plugin
=====================

What does this plugin do ?
--------------------------
As it name states - it allows Gradle to run SQL scripts.

Why should I use it ? Any alternatives ? I have a question ...
--------------------------------------------------------------
There's a good [FAQ](FAQ.md) - you may find some answers over there.

How to use this plugin?
-----------------------
Firstly, you'll need to include the plugin in your build script:

For Groovy-based DSL:

    plugins {
        id "com.nocwriter.runsql" version "0.8"
    }

For Kotin-based DSL:

    plugins {
        id("com.nocwriter.runsql") version ("0.8")
    }
    
Next, you'll need to configure it:

    sqlProperties {
        username = "<username>"
        password = "<password>"
        url = "<jdbc url>"
        scriptFilename = "<reference_script_file>"
    }
    
For example:

    sqlProperties {
        username = "postgres"
        password = "password"
        url = "jdbc:postgresql://localhost/mydb"
        scriptFilename = "/db/create_database_schema.sql"
    }
    
License
-------
This plugin is licensed under [Apache License V2.0](LICENSE).

How to contact you
------------------
If you'd like to contribute, ask questions or request for new features, you
should open the
[New issues](https://github.com/NocWriter/runsql-gradle-plugin/issues/new)
page and leave a message over there (with E-mail to contact you back).

I publish my E-mail address directly, as bots tend to abuse it.
 
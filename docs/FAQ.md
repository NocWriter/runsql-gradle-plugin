What is this project all about ?
--------------------------------
This is a Gradle plugin for executing SQL scripts (from within Gradle,
of course ...)

Why is it needed ?
------------------
Well, no enterprise project can be complete without a database and every
database needs its own set of SQL scripts.

Whether to build a complete schema and setup production environment, whether
to bootstrap a sandbox environment or whether to setup a local development
environment - someone needs to run SQL scripts in order to build or check
the database. That's what this plugin does - it runs SQL scripts.

Wait, I have FlywayDB plugin, why should I use this plugin ?
------------------------------------------------------------
Flyway is an excellent _schema migration_ tool. It is one of the best tools I
know of, for managing database schema.

However, FlywayDB is all about migrations. If you'd like to run scripts that
are not related to migration - you need SQL execution tool, such as this plugin.

Any examples ?
--------------
Here are few example for I use this plugin for:

* In development environment (and sometimes in sandbox/test environment, when
things go south), when I want to quickly setup a new database environment, I'm
using FlywayDB to build the schema. However, before running FlywayDB migration
task on Gradle, I'm dropping the database and creating a clean new one using
a custom script.
* Whenever I want to run some sort of database tables defragmentation (e.g.:
rebuild indexes, run PostgreSQL `VACUUM` or MySQL `OPTIMIZE TABLE`) - I'm
using Gradle to do that.


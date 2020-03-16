package com.nocwriter.runsql.gradle;

import java.util.StringJoiner;

/**
 * Plugin extension to hold properties.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLExtension {

    /**
     * JDBC URL (e.g.: jdbc:postgresql://localhost:5432/sample_db).
     */
    public String url;

    /**
     * JDBC connection username.
     */
    public String username;

    /**
     * JDBC connection password.
     */
    public String password;

    /**
     * JDBC driver class name (e.g.: 'org.postgresql.Driver for PostgreSQL or 'com.mysql.jdbc.Driver' for MySQL).
     */
    public String driverClassName;

    /**
     * Script file to run. Relative to project root (e.g.: '/migrations/initDatabase.sql').
     */
    public String scriptFilename;

    @Override
    public String toString() {
        return new StringJoiner(", ", RunSQLExtension.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("driverClassName='" + driverClassName + "'")
                .add("scriptFilename='" + scriptFilename + "'")
                .toString();
    }
}

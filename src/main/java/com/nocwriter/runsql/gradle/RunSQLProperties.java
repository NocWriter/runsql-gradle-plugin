package com.nocwriter.runsql.gradle;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Plugin extension to hold properties.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLProperties {

    /**
     * JDBC URL (e.g.: jdbc:postgresql://localhost:5432/sample_db).
     */
    protected String url;

    /**
     * JDBC connection username.
     */
    protected String username;

    /**
     * JDBC connection password.
     */
    protected String password;

    /**
     * JDBC driver class name (e.g.: 'org.postgresql.Driver for PostgreSQL or 'com.mysql.jdbc.Driver' for MySQL).
     */
    protected String driverClassName;

    /**
     * Script to execute. Caller must not specify both {@code script} and {@link #scriptFile} altogether.
     */
    protected String[] script;

    /**
     * Script file to run. Relative to project root (e.g.: '/migrations/initDatabase.sql').
     * Caller must not specify both {@link #script} and {@code scriptFile} altogether.
     */
    protected String[] scriptFile;

    /**
     * Class constructor.
     */
    public RunSQLProperties() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Object getScript() {
        return script;
    }

    public void setScript(Object script) {
        this.script = toStringArray("script", script);
    }

    public Object getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(Object scriptFile) {
        this.scriptFile = toStringArray("scriptFile", scriptFile);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RunSQLProperties.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("driverClassName='" + driverClassName + "'")
                .add("scriptFile='" + Arrays.toString(scriptFile) + "'")
                .toString();
    }

    /**
     * Convert an un-typed object to string array (i.e.: {@code String[]}). The object must
     * either be {@code java.lang.String} or {@code java.lang.String[]}.
     *
     * @param propertyName Property name this value is assigned to (required in case of error).
     * @param object       Object to convert.
     * @return String array of value(s) stored in <i>object</i>.
     * @throws IllegalArgumentException If <i>object</i> is neither {@code java.lang.String} nor
     *                                  {@code java.lang.String[]}.
     */
    private String[] toStringArray(String propertyName, Object object) throws IllegalArgumentException {
        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();
        if (clazz == String.class) {
            return new String[]{(String) object};
        }

        if (clazz.isArray() && clazz.getComponentType() == String.class) {
            return (String[]) object;
        }

        String buf = "Invalid value type assigned to property '" +
                propertyName +
                "' -- '" +
                clazz.getName() +
                "' (allowed types: java.lang.String or java.lang.String[]).";
        throw new IllegalArgumentException(buf);
    }

}

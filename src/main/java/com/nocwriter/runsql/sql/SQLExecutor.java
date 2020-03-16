package com.nocwriter.runsql.sql;

import com.nocwriter.runsql.RunSQLException;
import com.nocwriter.runsql.jdbc.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SQLExecutor {

    /**
     * Indicates if the entire script should run in one transaction ({@code true}) or each script should run in its
     * own transaction ({@code false}).
     */
    public boolean atomicity = true;

    /**
     * For cases where a query statement spans more than 1 line, whether to keep it that way or restructure the query
     * to be a single-line query.<p>
     * For example, if set to {@code true}, the following query will be executed as written:
     * <pre>
     *     SELECT
     *          username,
     *          password,
     *          first_name,
     *          last_name
     *     FROM
     *          users;
     * </pre>
     * <p>
     * However, if set to {@code false}, the query will be executed as:
     * <pre>
     *     SELECT username, password, first_name, last_name FROM users;
     * </pre>
     */
    public boolean keepNewlines = true;

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SQLExecutor.class);

    /**
     * Class constructor.
     */
    public SQLExecutor() {
    }

    public void executeSQL(String url,
                           String driverClassName,
                           String username,
                           String password,
                           File scriptFile,
                           ClassLoader jdbcDriverClassLoader) throws SQLException {
        printAvailableJDBCDrivers();

        List<SQLScriptParser.SQLStatement> statements = readScriptFile(scriptFile);
        try (Connection connection = openJDBCConnection(username,
                password,
                url,
                driverClassName,
                jdbcDriverClassLoader)) {
            if (!this.atomicity) {
                connection.setAutoCommit(false);
            }

            Statement statement = connection.createStatement();
            statements.forEach(sql -> {
                try {
                    statement.execute(sql.sql);
                    if (!atomicity) {
                        connection.commit();
                    }
                } catch (SQLException ex) {
                    throw new SQLExecutionTaskException("SQL statement execution failed (line " + sql.lineNumber + ").",
                            ex);
                }
            });

            if (atomicity) {
                connection.commit();
            }
        }
    }

    /**
     * Print all detected JDBC drivers. Useful for troubleshooting.
     */
    protected static void printAvailableJDBCDrivers() {
        StringBuilder buf = new StringBuilder();
        buf.append("Detected JDBC drivers:\n");
        List<String> availableDrivers = new LinkedList<>();
        DriverManager.drivers().forEach(driver -> {
            String location = driver.getClass().getResource("/" + driver.getClass().getName().replace('.', '/') + ".class").toString();
            String[] locationParts = location.split("!");
            String[] pathParts = locationParts[0].split("/");
            String lastPart = pathParts[pathParts.length - 1];
            String jarName = lastPart.endsWith(".jar") ? lastPart : "n/a";

            buf.append("\t")
                    .append(driver.getClass().getName())
                    .append(" version ")
                    .append(driver.getMajorVersion())
                    .append(".")
                    .append(driver.getMinorVersion())
                    .append(" (jar: ")
                    .append(jarName)
                    .append(").\n");
        });

        logger.info(buf.toString());
    }

    /**
     * Read SQL script file and parse statements.
     *
     * @return List of SQL statements to execute.
     */
    protected List<SQLScriptParser.SQLStatement> readScriptFile(File scriptFile) {
        if (!scriptFile.exists()) {
            throw new IllegalStateException("Missing SQL file -- " + scriptFile.getAbsolutePath());
        }
        logger.info("SQL script file: {}", scriptFile.getAbsolutePath());

        List<String> script;
        try {
            script = Files.readAllLines(scriptFile.toPath());
        } catch (IOException ex) {
            logger.error("Failed to read SQL script file {}", scriptFile.getAbsolutePath());
            throw new SQLExecutionTaskException("Failed to read SQL file " + scriptFile.getAbsolutePath(), ex);
        }

        SQLScriptParser parser = new SQLScriptParser();
        parser.keepNewlines = keepNewlines;
        List<SQLScriptParser.SQLStatement> statements = parser.parseScript(script);
        logger.info("Successfully read script file (found {} SQL statements).", statements.size());

        return statements;
    }

    /**
     * Opens a new JDBC connection based on provided username, password, url and driver class name.
     *
     * @param username              Username.
     * @param password              Password.
     * @param url                   JDBC URL.
     * @param driverClassName       Driver class name.
     * @param jdbcDriverClassLoader JDBC drivers class loader.
     * @return New JDBC connection.
     * @throws SQLException If any SQL-related error occurs.
     */
    public static Connection openJDBCConnection(String username,
                                                String password,
                                                String url,
                                                String driverClassName,
                                                ClassLoader jdbcDriverClassLoader)
            throws SQLException {

        Driver driver = JdbcUtils.loadJdbcDriver(jdbcDriverClassLoader, driverClassName);
        logger.info("Registering JDBC driver: '{}' (version {}.{}).",
                driver.getClass().getName(),
                driver.getMajorVersion(),
                driver.getMinorVersion());

        DriverManager.registerDriver(driver);
        printAvailableJDBCDrivers();

        Connection connection = DriverManager.getConnection(url, username, password);

        String loadedDriverMessage =
                String.format("Successfully established JDBC connection (JDBC driver '%s' version %s).",
                        connection.getMetaData().getDriverName(),
                        connection.getMetaData().getDriverVersion());

        logger.info(loadedDriverMessage);

        return connection;
    }

    private void validateProperty(String name, String value) {
        if (value == null) {
            throw new IllegalStateException("Missing property '" + name + "'.");
        }
    }


}
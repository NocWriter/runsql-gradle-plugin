package com.nocwriter.runsql.script;

import com.nocwriter.runsql.gradle.ScriptObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Executes SQL scripts.
 *
 * @author Guy Raz Nir
 * @since 2020/03/21
 */
public class ScriptRunner {

    /**
     * Indicates if commit should occur after each statement ({@code true}) or an entire script should run in atomicity
     * ({@code false}).
     */
    public boolean autoCommit = true;

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
     * JDBC connection to execute scripts with.
     */
    private final Connection connection;

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ScriptRunner.class);

    /**
     * Class constructor.
     *
     * @param connection JDBC connection.
     */
    public ScriptRunner(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }
        this.connection = connection;
    }

    /**
     * Execute an SQL script.
     *
     * @param scriptObject Script object containing SQL statements.
     */
    public void executeSQL(ScriptObject scriptObject) {
        try {
            connection.setAutoCommit(autoCommit);

            Statement statement = connection.createStatement();
            scriptObject.statements.forEach(sql -> {
                try {
                    statement.execute(sql.sql);
                } catch (SQLException ex) {
                    String message = String.format("SQL statement execution failed (file: %s, line: %d).",
                            scriptObject.scriptFile != null ? scriptObject.scriptFile.toString() : "n/a",
                            sql.lineNumber);
                    throw new ScriptRunnerException(message, ex);
                }
            });

            if (!autoCommit) {
                connection.commit();
            }
        } catch (SQLException ex) {
            throw new ScriptRunnerException("Failed to execute script.", ex);
        }
    }
}
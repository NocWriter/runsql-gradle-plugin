package com.nocwriter.runsql.script;

import com.nocwriter.runsql.gradle.ScriptObject;
import com.nocwriter.runsql.script.ScriptParser;
import com.nocwriter.runsql.script.ScriptRunner;
import org.assertj.core.api.Assertions;
import org.hsqldb.server.ServerAcl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;


/**
 * Integration tests for {@link ScriptRunner}.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class ScriptRunnerTest {

    private Connection connection;

    /**
     * JDBC URL for H2 in-memory database.
     */
    private static final String URL = "jdbc:hsqldb:mem:test";

    /**
     * Database username.
     */
    private static final String USERNAME = "SA";

    /**
     * Database password.
     */
    private static final String PASSWORD = "";

    /**
     * Driver class name.
     */
    private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbcDriver";

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.close();
    }

    /**
     * Test that {@link ScriptRunner} reads SQL script file and execute it.
     */
    @Test
    @DisplayName("Test should create table and populate it with data")
    public void testShouldCreateTableAndPopulateWithData() throws SQLException {
        ScriptObject scriptObject = new ScriptObject();
        scriptObject.script = new String[]{"",
                "-- Create new table.",
                "CREATE TABLE books",
                "(",
                "    id     INT IDENTITY  PRIMARY KEY,",
                "    name   VARCHAR(100),",
                "    author VARCHAR(100)",
                ");",
                "",
                "-- Insert sample data to table.",
                "INSERT INTO books (name, author)",
                "VALUES ('The Hound of the Baskervilles', 'Arthur Conan Doyle');"
        };
        ScriptParser parser = new ScriptParser();
        scriptObject.statements = parser.parseScript(scriptObject.script);

        // Run SQL script.
        ScriptRunner executor = new ScriptRunner(connection);
        executor.autoCommit = true;
        executor.executeSQL(scriptObject);

        //
        // Read database to validate execution.
        //
        int numberOfRows;
        try (Statement statement = connection.createStatement()) {
            //noinspection SqlResolve
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM books;");
            rs.next();
            numberOfRows = rs.getInt(1);
        }

        // We expect that the number of rows available is '1'.
        Assertions.assertThat(numberOfRows).isEqualTo(1);
    }
}

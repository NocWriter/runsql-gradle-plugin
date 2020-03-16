package com.nocwriter.runsql.sql;

import org.assertj.core.api.Assertions;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Integration tests for {@link SQLExecutor}.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class SQLExecutorTest {

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

    /**
     * Test that {@link SQLExecutor} reads SQL script file and execute it.
     */
    @Test
    @DisplayName("Test should create table and populate it with data")
    public void testShouldCreateTableAndPopulateWithData() throws SQLException, URISyntaxException, IOException, ServerAcl.AclFormatException {
        // Start a new H2 in-memory server.
        HsqlProperties p = new HsqlProperties();
        p.setProperty("server.database.0","file:/opt/db/accounts");
        p.setProperty("server.dbname.0","an_alias");
        Server server = new Server();
        server.setProperties(p);
        server.setLogWriter(null); // can use custom writer
        server.setErrWriter(null); // can use custom writer
        server.start();

        // Reference to sample script file.
        File file = new File(getClass().getResource("/create_books_table.sql").toURI());

        // Run SQL script.
        SQLExecutor executor = new SQLExecutor();
        executor.executeSQL(URL, DRIVER_CLASS_NAME, USERNAME, PASSWORD, file, getClass().getClassLoader());

        //
        // Read database to validate execution.
        //
        int numberOfRows;
        try (Connection connection = SQLExecutor.openJDBCConnection(USERNAME, PASSWORD, URL, DRIVER_CLASS_NAME,  getClass().getClassLoader())) {
            try (Statement statement = connection.createStatement()) {
                //noinspection SqlResolve
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM books;");
                rs.next();
                numberOfRows = rs.getInt(1);
            }
        }
        server.stop();

        // We expect that the number of rows available is '1'.
        Assertions.assertThat(numberOfRows).isEqualTo(1);
    }
}

package com.nocwriter.runsql;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A Gradle integration test for executing a SQL script.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class ExecuteScriptITest extends GradleIntegrationTestBase {

    /**
     * Execute a Gradle build process (in another process) to execute a SQL script. The script is applied on
     * a temporary database, which is read after the task is complete.
     */
    @Test
    @DisplayName("Test should execute a custom SQL script")
    public void testShouldExecuteCustomSQLScript() throws IOException, SQLException {
        useBuildScript("/test_scripts/custom_sql_task_script");
        copy("/create_books_table.sql", "create_books_table.sql");

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments(":createTable", "--stacktrace", "--info")
                .withPluginClasspath()
                .withGradleVersion("5.6")
                .build();

        dumpTestOutput(result);

        assertThat(result.getTasks()).hasSize(1);
        assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);

        try (Connection connection = DriverManager.getConnection(getUrl(), USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                @SuppressWarnings("SqlResolve")
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM books;");

                assertThat(rs.next()).as("Should have a result from query.").isTrue();
                assertThat(rs.getInt(1)).as("Should have exactly one item in database.").isEqualTo(1);
            }
        }
    }

    /**
     * Execute a Gradle build process (in another process) to execute a SQL script. The script is applied on
     * a temporary database, which is read after the task is complete.
     */
    @Test
    @DisplayName("Test should execute direct SQL script string")
    public void testShouldExecuteDirectSQLString() throws IOException, SQLException {
        useBuildScript("/test_scripts/custom_sql_task_script");

        BuildResult result = GradleRunner.create()
                .withGradleVersion("5.6")
                .withProjectDir(testProjectDir)
                .withArguments(":createTableOnly", "--stacktrace", "--info")
                .withPluginClasspath()
                .build();

        dumpTestOutput(result);

        assertThat(result.getTasks()).hasSize(1);
        assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);

        try (Connection connection = DriverManager.getConnection(getUrl(), USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                @SuppressWarnings("SqlResolve")
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM books;");

                assertThat(rs.next()).as("Should have a result from query.").isTrue();
                assertThat(rs.getInt(1)).as("Should have exactly one item in database.").isEqualTo(0);
            }
        }
    }
}

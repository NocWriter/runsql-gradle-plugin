package com.nocwriter.runsql;

import org.gradle.testkit.runner.BuildResult;
import org.hsqldb.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Base class for integration tests. Provides fixture (creates temporary settings and build file) and provide various
 * helper utilities.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
@SuppressWarnings("SameParameterValue")
public class GradleIntegrationTestBase {

    /**
     * Temporary folder for generating Gradle script files per test.
     */
    @TempDir
    protected File testProjectDir;

    /**
     * settings.gradle.tks
     */
    protected File settingsFile;

    /**
     * run_sql_script
     */
    protected File buildFile;

    /**
     * DB credentials.
     */
    protected static final String USERNAME = "sa";

    /**
     * DB credentials.
     */
    protected static final String PASSWORD = "";

    /**
     * DB name.
     */
    protected static final String DATABASE_NAME = "test";

    /**
     * JDBC URL for accessing our test database.
     */
    protected static final String URL_TEMPLATE = "jdbc:hsqldb:file:%s/" + DATABASE_NAME + ";shutdown=true";

    /**
     * Test logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Embedded database.
     */
    private Server server;

    /**
     * Test fixtures (runs before every test):
     * <ul>
     *     <li>Creates basic <i>settings.gradle.kts'</i> file.</li>
     *     <li>Creates empty <i>build.gradle.kts</i> file.</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() throws IOException {
        logger.info("Project dir: {}", testProjectDir.toString());

        // References to Gradle settings and build files (Kotlin form).
        settingsFile = new File(testProjectDir, "settings.gradle.kts");
        buildFile = new File(testProjectDir, "build.gradle.kts");

        // Create default settings file.
        writeFile(settingsFile, "rootProject.name = \"run-sql-plugin-test\"");
        logger.info("New build environment created successfully.");
    }

    /**
     * Write textual contents to file.
     *
     * @param target   Target file to write to.
     * @param contents Variable-length array of strings to write to.
     * @throws IOException If any I/O error occurs.
     */
    protected void writeFile(File target, String... contents) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
            for (String block : contents) {
                writer.write(block.replace('\'', '\"'));
                writer.write(System.lineSeparator());
            }

        }
    }

    /**
     * Copies a resource located on the classpath to the temporary file-system test environment.
     *
     * @param classpathResource Resource on the local classpath.
     * @param localResource     Target file name, relative to temporary test directory.
     * @throws IOException If copy failed.
     */
    protected void copy(String classpathResource, String localResource) throws IOException {
        Path targetResource = Paths.get(testProjectDir.getAbsolutePath(), localResource);
        try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
            assert in != null;
            Files.copy(in, targetResource);
        }
    }

    /**
     * Use a local classpath resource as the build script. Useful for writing the script outside the test.
     *
     * @param classpathResource Classpath resource.
     * @throws IOException If any I/O error occurs during read/write.
     */
    protected void useBuildScript(String classpathResource) throws IOException {
        String contents;
        try (InputStream in = ExecuteScriptITest.class.getResourceAsStream(classpathResource)) {
            assert in != null;

            byte[] data = new byte[10 * 1024];
            int numberOfBytes = in.read(data);
            contents = new String(data, 0, numberOfBytes);
        }

        writeFile(buildFile, contents);
    }

    /**
     * Helper method for dumping Gradle's test execution output to console.
     *
     * @param result Build results.
     */
    protected void dumpTestOutput(BuildResult result) {
        System.out.println("---------- Test output ----------");
        System.out.println(result.getOutput());
        System.out.println("---------------------------------");
    }

    /**
     * Starts a new embedded database server.
     */
    protected void startDatabaseServer() throws IOException {
        Path tempDir = Files.createTempDirectory("run_sql_test_");
        String path = tempDir.toString();
        String databasePath = String.format("file:%s;user=%s;password=%s",
                tempDir,
                USERNAME,
                PASSWORD);

        logger.info("Starting new HSQLDB embedded server.");
        logger.debug("Location of database storage: {}", path);
        Server server = new Server();
        server.setDatabasePath(0, databasePath);
        server.setDatabaseName(0, DATABASE_NAME);
        server.setErrWriter(null);
        server.setLogWriter(null);
        server.setSilent(true);
        server.setTrace(false);
        server.start();

        logger.info("HSQLDB started successfully.");
    }

    /**
     * Stops embedded database, if it's currently running.
     */
    protected void stopDatabaseServer() {
        if (server != null) {
            logger.info("Shutting down HSQLDB embedded server.");
            server.stop();
            server = null;
        }
    }

    protected String getUrl() {
        return String.format(URL_TEMPLATE, testProjectDir.toString());
    }

}

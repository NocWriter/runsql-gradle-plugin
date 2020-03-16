package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.RunSQLException;
import com.nocwriter.runsql.sql.SQLExecutor;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gradle task to execute query.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLTask extends DefaultTask {

    @TaskAction
    public void execute() throws SQLException {
        // Locate and validate plugin's extension.
        RunSQLExtension extension = getProject().getExtensions().getByType(RunSQLExtension.class);
        ExtensionValidator.validateExtensionProperties(extension);

        ClassLoader jdbcClassLoader = createDriversClassLoader();

        // We need to resolve a local script file based on project's root directory.
        File scriptFile = getProject().file(extension.scriptFilename);

        // Execute script.
        SQLExecutor executor = new SQLExecutor();
        executor.executeSQL(extension.url,
                extension.driverClassName,
                extension.username,
                extension.password,
                scriptFile,
                jdbcClassLoader);
    }

    /**
     * Since Gradle itself may not load the build script classpath dependencies itself, we need to create a new
     * class loader to do it.<p>
     * This method collects all 'classpath' configuration dependencies and creates a class loader for them.
     *
     * @return JDBC drivers class loader.
     */
    protected ClassLoader createDriversClassLoader() {
        Configuration classpathConfiguration = getProject().getConfigurations().getByName("runtimeClasspath");
        ResolvedConfiguration resolvedConfiguration = classpathConfiguration.getResolvedConfiguration();

        URL[] artifactsURLs = resolvedConfiguration
                .getResolvedArtifacts()
                .stream()
                .map(this::toURL)
                .toArray(URL[]::new);

        return new URLClassLoader(
                artifactsURLs,
                getClass().getClassLoader());
    }

    /**
     * Resolve artifact to URL.
     *
     * @param artifact Artifact to resolve.
     * @return URL to artifact's file.
     */
    protected URL toURL(ResolvedArtifact artifact) {
        try {
            return artifact.getFile().toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RunSQLException("Unexpected error: Could not resolve artifact to URL: " + artifact.toString());
        }
    }
}

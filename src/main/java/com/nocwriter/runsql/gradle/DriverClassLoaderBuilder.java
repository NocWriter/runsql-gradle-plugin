package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.RunSQLException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Creates a new class loader from Project's runtime artifacts.
 *
 * @author Guy Raz Nir
 * @since 2020/03/21
 */
public class DriverClassLoaderBuilder {

    /**
     * Since Gradle itself may not load the build script classpath dependencies itself, we need to create a new
     * class loader to do it.<p>
     * This method collects all 'classpath' configuration dependencies and creates a class loader for them.
     *
     * @param project Project to use for resolving class loader.
     * @return JDBC drivers class loader.
     */
    public static ClassLoader createDriversClassLoader(Project project) {
        Configuration classpathConfiguration = project.getConfigurations().getByName("runtimeClasspath");
        ResolvedConfiguration resolvedConfiguration = classpathConfiguration.getResolvedConfiguration();

        URL[] artifactsURLs = resolvedConfiguration
                .getResolvedArtifacts()
                .stream()
                .map(DriverClassLoaderBuilder::toURL)
                .toArray(URL[]::new);

        return new URLClassLoader(
                artifactsURLs,
                DriverClassLoaderBuilder.class.getClassLoader());
    }

    /**
     * Resolve artifact to URL.
     *
     * @param artifact Artifact to resolve.
     * @return URL to artifact's file.
     */
    protected static URL toURL(ResolvedArtifact artifact) {
        try {
            return artifact.getFile().toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RunSQLException("Unexpected error: Could not resolve artifact to URL: " + artifact.toString());
        }
    }
}

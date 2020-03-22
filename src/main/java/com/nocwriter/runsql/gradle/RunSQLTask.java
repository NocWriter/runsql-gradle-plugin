package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.RunSQLException;
import com.nocwriter.runsql.jdbc.JdbcUtils;
import com.nocwriter.runsql.script.ScriptParser;
import com.nocwriter.runsql.script.ScriptRunner;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.tasks.TaskAction;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Gradle task to execute query.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLTask extends DefaultTask {

    private RunSQLProperties props = new RunSQLProperties();

    /**
     * Triggers an action for configuring {@link RunSQLProperties}.
     *
     * @param configAction Configuration action to execute.
     */
    public void config(Action<RunSQLProperties> configAction) {
        configAction.execute(this.props);
    }

    @TaskAction
    public void execute() throws SQLException {
        // Locate and validate plugin's extension.
        PropertiesValidator.validateExtensionProperties(this.props);

        // Build a new class loader that can load JDBC driver classes.
        ClassLoader jdbcClassLoader = DriverClassLoaderBuilder.createDriversClassLoader(getProject());
        JdbcUtils.registerDriver(jdbcClassLoader, this.props.driverClassName);

        List<ScriptObject> scriptObjects = ScriptsReader.fetchScripts(getProject(), this.props);
        ScriptParser parser = new ScriptParser();
        scriptObjects.forEach(script -> script.statements = parser.parseScript(script.script));

        try (Connection connection = JdbcUtils.openJDBCConnection(props.username, props.password, props.url)) {
            // Execute script.
            ScriptRunner executor = new ScriptRunner(connection);
            scriptObjects.forEach(executor::executeSQL);
        }
    }

}

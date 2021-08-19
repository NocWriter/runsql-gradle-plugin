package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.script.ScriptRunnerException;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ScriptsReader {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ScriptsReader.class);

    /**
     * Fetch scripts provided via extension. The scripts can either be read from external files or can be a single
     * direct script.
     *
     * @param project    Gradle project. Required to resolve files.
     * @param properties Properties to read scripts by.
     * @return List of scripts.
     */
    public static List<ScriptObject> fetchScripts(Project project, RunSQLProperties properties) {
        List<ScriptObject> scripts = new LinkedList<>();

        if (properties.script != null) {
            scripts.add(new ScriptObject(properties.script));
        } else {
            Arrays.stream(properties.scriptFile).forEach(scriptFile -> {
                File file = project.file(scriptFile);
                String[] contents = readScriptFile(file);
                scripts.add(new ScriptObject(file, contents));
            });
        }

        return scripts;
    }

    /**
     * Read SQL script file and parse statements.
     *
     * @return List of SQL statements to execute.
     */
    private static String[] readScriptFile(File scriptFile) {
        if (!scriptFile.exists()) {
            throw new IllegalStateException("Missing SQL file -- " + scriptFile.getAbsolutePath());
        }
        logger.info("SQL script file: {}", scriptFile.getAbsolutePath());

        List<String> script;
        try {
            script = Files.readAllLines(scriptFile.toPath());
        } catch (IOException ex) {
            logger.error("Failed to read SQL script file {}", scriptFile.getAbsolutePath());
            throw new ScriptRunnerException("Failed to read SQL file " + scriptFile.getAbsolutePath(), ex);
        }

        return script.toArray(new String[0]);
    }


}

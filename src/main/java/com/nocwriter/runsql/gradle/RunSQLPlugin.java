package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.jdbc.DriverManagerLogPrinter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin's main entry point.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(RunSQLPlugin.class);

    @Override
    public void apply(Project project) {
        logger.info("Plugin {} created successfully for project {}", getClass().getSimpleName(), project.getName());

        project.getTasks().register("RunSQLHelp", RunSQLHelpTask.class);
    }
}

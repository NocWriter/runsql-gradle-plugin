package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.jdbc.DriverManagerLogPrinter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Plugin's main entry point.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        DriverManagerLogPrinter.apply();

        project.getExtensions().create("sqlProperties", RunSQLExtension.class);

        // Register new task.
        project.getTasks().create("runSQL", RunSQLTask.class);
    }
}

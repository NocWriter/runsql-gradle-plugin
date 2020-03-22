package com.nocwriter.runsql.gradle;

import org.gradle.api.DefaultTask;

import java.io.IOException;
import java.io.InputStream;

public class RunSQLHelpTask extends DefaultTask {

    public void printHelp() {
        try (InputStream in = RunSQLHelpTask.class.getResourceAsStream("/help.txt")) {
            byte[] data = new byte[in.available()];
            //noinspection ResultOfMethodCallIgnored
            in.read(data);
            System.out.println(new String(data));
        } catch (IOException ex) {
            System.out.println("Could not print help message.");
        }
    }
}

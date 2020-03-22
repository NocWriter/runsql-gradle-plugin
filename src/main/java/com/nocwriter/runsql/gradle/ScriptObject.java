package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.script.SQLStatement;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a script to be executed. Includes optional script file (if availble), the raw form of the script itself
 * and parsed statements.
 *
 * @author Guy Raz Nir
 * @since 2020/03/21
 */
public class ScriptObject {

    /**
     * Script file, if the contents where read from file.
     */
    public File scriptFile;

    /**
     * Script to execute.
     */
    public String[] script;

    /**
     * Collection of translated statements.
     */
    public List<SQLStatement> statements = new LinkedList<>();

    /**
     * Class constructor.
     */
    public ScriptObject() {
    }

    /**
     * Class constructor.
     *
     * @param script Script contents.
     */
    public ScriptObject(String[] script) {
        this.script = script;
    }

    /**
     * Class constructor.
     *
     * @param scriptFile File the script was read from.
     * @param script     Script contents.
     */
    public ScriptObject(File scriptFile, String[] script) {
        this.scriptFile = scriptFile;
        this.script = script;
    }

    /**
     * Simple factory to create a new script object.
     *
     * @param script Script to use.
     * @return New script object.
     */
    public ScriptObject newScriptObject(String... script) {
        return new ScriptObject(script);
    }

}

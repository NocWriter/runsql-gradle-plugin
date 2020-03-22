package com.nocwriter.runsql.script;

/**
 * Top-level exception for plugin.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class ScriptRunnerException extends RuntimeException {

    public ScriptRunnerException() {
    }

    public ScriptRunnerException(String message) {
        super(message);
    }

    public ScriptRunnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptRunnerException(Throwable cause) {
        super(cause);
    }

    public ScriptRunnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

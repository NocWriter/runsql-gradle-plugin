package com.nocwriter.runsql.sql;

/**
 * Top-level exception for plugin.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class SQLExecutionTaskException extends RuntimeException {

    public SQLExecutionTaskException() {
    }

    public SQLExecutionTaskException(String message) {
        super(message);
    }

    public SQLExecutionTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLExecutionTaskException(Throwable cause) {
        super(cause);
    }

    public SQLExecutionTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

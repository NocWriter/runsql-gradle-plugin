package com.nocwriter.runsql;

/**
 * Base exception for all plugin's exception.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class RunSQLException extends RuntimeException {

    public RunSQLException() {
    }

    public RunSQLException(String message) {
        super(message);
    }

    public RunSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunSQLException(Throwable cause) {
        super(cause);
    }

    public RunSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.RunSQLException;

/**
 * This exception indicates that one of the mandatory properties (such as URL, username, password, ....) are missing.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class InvalidOrMissingPropertyException extends RunSQLException {

    public InvalidOrMissingPropertyException(String message) {
        super(message);
    }

}

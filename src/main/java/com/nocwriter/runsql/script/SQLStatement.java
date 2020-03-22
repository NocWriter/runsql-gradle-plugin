package com.nocwriter.runsql.script;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents an SQL statement and its first line number within the script.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class SQLStatement {

    /**
     * Script's line number.
     */
    final int lineNumber;

    /**
     * SQL statement.
     */
    final String sql;

    /**
     * Class constructor.
     *
     * @param lineNumber Line number.
     * @param sql        SQL statement.
     */
    public SQLStatement(int lineNumber, String sql) {
        this.lineNumber = lineNumber;
        this.sql = sql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLStatement that = (SQLStatement) o;
        return lineNumber == that.lineNumber &&
                Objects.equals(sql, that.sql);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, sql);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SQLStatement.class.getSimpleName() + "[", "]")
                .add("lineNumber=" + lineNumber)
                .add("sql='" + sql + "'")
                .toString();
    }
}

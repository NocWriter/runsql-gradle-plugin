package com.nocwriter.runsql.jdbc;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DriverManager;

/**
 * A special writer for redirecting JDBC {@code DriverManager} output to console.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class DriverManagerLogPrinter extends Writer {

    /**
     * Class constructor.
     */
    public DriverManagerLogPrinter() {
        super(System.out);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void write(char[] buf, int offset, int count) {
        System.out.println("DriverManager: " + new String(buf, offset, count));
    }

    @Override
    public void flush() {
        System.out.flush();
    }

    @Override
    public void close() {
        // Do nothing.
    }

    /**
     * Re-assign {@code DriverManager}'s output to STDOUT.
     */
    public static void apply() {
        DriverManager.setLogWriter(new PrintWriter(new DriverManagerLogPrinter()));
    }

}

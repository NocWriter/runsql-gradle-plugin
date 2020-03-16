package com.nocwriter.runsql.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverDelegator implements Driver {

    private final Driver driver;

    public DriverDelegator(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Connection connect(String s, Properties properties) throws SQLException {
        return driver.connect(s, properties);
    }

    @Override
    public boolean acceptsURL(String s) throws SQLException {
        return driver.acceptsURL(s);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
        return driver.getPropertyInfo(s, properties);
    }

    @Override
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
    }
}

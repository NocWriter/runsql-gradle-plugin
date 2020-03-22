package com.nocwriter.runsql.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Internal delegator for JDBC drivers. Required due to limitation imposed by Java's {@code DriverManager}.<p>
 * <p>
 * By default, a driver loaded via a custom class loader which differs from the current class loader, cannot be
 * registered by {@code DriverManager}. To overcome this problem, we create this delegator and register it on behalf
 * of the real driver.
 *
 * @author Guy Raz Nir
 * @since 2020/03/18
 */
class DriverDelegator implements Driver {

    /**
     * The actual driver we delegate the work to.
     */
    private final Driver driver;

    /**
     * Class constructor.
     *
     * @param driver The actual driver we delegate the work to.
     */
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

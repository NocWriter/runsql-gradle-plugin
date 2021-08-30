package com.nocwriter.runsql.jdbc;

import com.nocwriter.runsql.RunSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Collection of JDBC utilities.
 *
 * @author Guy Raz Nir
 * @since 2020/03/18
 */
public class JdbcUtils {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * Load JDBC driver from a given URL.
     *
     * @param jarUrl          URL to JDBC driver, typically a .jar file.
     * @param driverClassName Driver class name.
     * @return Driver class ready for use.
     */
    public static Driver loadJdbcDriver(URL jarUrl, String driverClassName) {
        return loadJdbcDriver(new URL[]{jarUrl}, driverClassName);
    }

    /**
     * Load JDBC driver from a given URL.
     *
     * @param jarsUrls        Collection of URL to try and load from, typically a .jar files.
     * @param driverClassName Driver class name.
     * @return Driver class ready for use.
     */
    public static Driver loadJdbcDriver(URL[] jarsUrls, String driverClassName) {
        URLClassLoader classLoader = new URLClassLoader(jarsUrls);
        return loadJdbcDriver(classLoader, driverClassName);
    }

    /**
     * Loads a JDBC driver using a given class loader.
     *
     * @param cl              Class loader.
     * @param driverClassName Driver class name.
     * @return Driver class ready for use.
     */
    public static Driver loadJdbcDriver(ClassLoader cl, String driverClassName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Driver> driverClass =
                    (Class<? extends Driver>) Class.forName(driverClassName, true, cl);

            Driver driver = driverClass.getDeclaredConstructor().newInstance();
            return new DriverDelegator(driver);
        } catch (ClassNotFoundException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException ex) {
            throw new RunSQLException("Could not load JDBC driver " + driverClassName, ex);
        }
    }

    /**
     * Load JDBC driver and register it with JDBC's driver manager.
     *
     * @param classLoader     Class loader to use for loading the driver class name.
     * @param driverClassName Driver class name.
     */
    public static void registerDriver(ClassLoader classLoader, String driverClassName) {
        Driver driver = loadJdbcDriver(classLoader, driverClassName);
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException ex) {
            throw new RunSQLException("Failed to register JDBC driver class '" + driverClassName + "'.", ex);
        }
    }

    /**
     * Opens a new JDBC connection based on provided username, password, url and driver class name.
     *
     * @param username Username.
     * @param password Password.
     * @param url      JDBC URL.
     * @return New JDBC connection.
     * @throws SQLException If any SQL-related error occurs.
     */
    public static Connection openJDBCConnection(String username,
                                                String password,
                                                String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);

        String loadedDriverMessage =
                String.format("Successfully established JDBC connection (JDBC driver '%s' version %s).",
                        connection.getMetaData().getDriverName(),
                        connection.getMetaData().getDriverVersion());

        logger.info(loadedDriverMessage);

        return connection;
    }

    /**
     * Print all detected JDBC drivers. Useful for troubleshooting.
     */
    @SuppressWarnings("ConstantConditions")
    protected static void printAvailableJDBCDrivers() {
        StringBuilder buf = new StringBuilder();
        buf.append("Detected JDBC drivers:\n");
        Collections.list(DriverManager.getDrivers()).forEach(driver -> {
            String driverClassPath = "/" + driver.getClass().getName().replace('.', '/') + ".class";
            String location = driver.getClass().getResource(driverClassPath).toString();
            String[] locationParts = location.split("!");
            String[] pathParts = locationParts[0].split("/");
            String lastPart = pathParts[pathParts.length - 1];
            String jarName = lastPart.endsWith(".jar") ? lastPart : "n/a";

            buf.append("\t")
                    .append(driver.getClass().getName())
                    .append(" version ")
                    .append(driver.getMajorVersion())
                    .append(".")
                    .append(driver.getMinorVersion())
                    .append(" (jar: ")
                    .append(jarName)
                    .append(").\n");
        });

        logger.info(buf.toString());
    }


}

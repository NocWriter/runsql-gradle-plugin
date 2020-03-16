package com.nocwriter.runsql.jdbc;

import com.nocwriter.runsql.RunSQLException;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

public class JdbcUtils {

    public static Driver loadJdbcDriver(URL jarUrl, String driverClassName) {
        return loadJdbcDriver(new URL[]{jarUrl}, driverClassName);
    }

    public static Driver loadJdbcDriver(URL[] jarsUrls, String driverClassName) {
        URLClassLoader classLoader = new URLClassLoader(jarsUrls);
        return loadJdbcDriver(classLoader, driverClassName);
    }

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
}

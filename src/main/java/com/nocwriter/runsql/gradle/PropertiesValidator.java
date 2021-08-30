package com.nocwriter.runsql.gradle;

import com.nocwriter.runsql.RunSQLException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Some helper function to verify and manage plugin extension.
 *
 * @author Guy Raz Nir
 * @since 2020/03/21
 */
public class PropertiesValidator {

    /**
     * Holds mapping between JDBC sub-protocol (e.g.: mysql) and driver class name (e.g.: 'com.mysql.jdbc.Driver').
     */
    public static final Map<String, String> jdbcDriverClassNamesLookup = loadJdbcClassNames();

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertiesValidator.class);

    /**
     * Name of classpath resource holding driver class names.
     */
    private static final String JDBC_DRIVER_CLASS_NAME_LOOKUP_FILE = "/jdbcDriverClassNames.properties";

    /**
     * Validate plugin's properties.
     *
     * @param extension Extension holding all properties.
     */
    public static void validateExtensionProperties(RunSQLProperties extension) {
        if (extension.username == null) {
            throw new InvalidOrMissingPropertyException("Missing property 'username'.");
        }

        if (extension.password == null) {
            throw new InvalidOrMissingPropertyException("Missing property 'password'.");
        }

        if (StringUtils.isBlank(extension.url)) {
            throw new InvalidOrMissingPropertyException("Missing property 'url'.");
        }

        extension.username = extension.username.trim();
        extension.password = extension.password.trim();
        extension.url = extension.url.trim();

        // Verify the format of URL property.
        String[] urlParts = extension.url.split(":");
        if (urlParts.length < 3 || !"jdbc".equals(urlParts[0])) {
            throw new InvalidOrMissingPropertyException("Invalid/non-JDBC url: " + extension.url);
        }

        if (StringUtils.isBlank(extension.driverClassName)) {
            String detectedDriverClassName = jdbcDriverClassNamesLookup.get(urlParts[1]);
            if (detectedDriverClassName == null) {
                throw new InvalidOrMissingPropertyException(
                        "Missing property 'driverClassName' (could not auto-detect from JDBC URL string).");
            }

            extension.driverClassName = detectedDriverClassName;
        }

        if (extension.scriptFile != null && extension.script != null) {
            throw new InvalidOrMissingPropertyException("You cannot specify both 'scriptFile' and 'script' properties.");
        }

        if (extension.script == null && extension.scriptFile == null) {
            throw new InvalidOrMissingPropertyException("You must specify either 'scriptFile' or 'script'.");
        }
    }

    /**
     * Loads default JDBC sub-protocol / driver class name mapping.
     *
     * @return Map between JDBC sub-protocol and driver class name.
     */
    private static Map<String, String> loadJdbcClassNames() {
        try (InputStream in = RunSQLPlugin.class.getResourceAsStream(JDBC_DRIVER_CLASS_NAME_LOOKUP_FILE)) {
            // Use Java's properties implementation to load key/value pairs.
            Properties props = new Properties();
            props.load(in);

            // Translate to simple string-to-string map.
            Map<String, String> map = new HashMap<>(props.size());
            props.forEach((key, value) -> map.put((String) key, (String) value));
            return map;
        } catch (Exception ex) {
            throw new RunSQLException(
                    String.format("Internal error: Failed to load internal resource '%s'.",
                            JDBC_DRIVER_CLASS_NAME_LOOKUP_FILE),
                    ex);
        }
    }

}

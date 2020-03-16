package com.nocwriter.runsql;

import static org.assertj.core.api.Assertions.*;

import com.nocwriter.runsql.gradle.InvalidOrMissingPropertyException;
import com.nocwriter.runsql.gradle.RunSQLExtension;
import com.nocwriter.runsql.gradle.ExtensionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExtensionValidatorTest {

    /**
     * Plugin's extension holding properties.
     */
    private RunSQLExtension extension;

    /**
     * Text fixture -- creates new plugin and extension before each test.
     */
    @BeforeEach
    public void setUp() {
        extension = new RunSQLExtension();
    }

    /**
     * Test should fail on missing properties, such as username, password, url and driverClassName.
     */
    @Test
    @DisplayName("Test should fai on missing extension data")
    public void testShouldFailOnMissingExtensionData() {
        assertThatThrownBy(() -> ExtensionValidator.validateExtensionProperties(extension))
                .isInstanceOf(InvalidOrMissingPropertyException.class);
    }

    @Test
    @DisplayName("Test should fail on invalid JDBC url")
    public void testShouldFailOnInvalidJdbcUrl() {
        extension.username = "username";
        extension.password = "password";
        extension.url = "xxx:mysql://localhost:3306/";
        assertThatThrownBy(() -> ExtensionValidator.validateExtensionProperties(extension))
                .isInstanceOf(InvalidOrMissingPropertyException.class)
                .hasMessageContaining("Invalid")
                .hasMessageContaining("non-JDBC");
    }

    @Test
    @DisplayName("Test should fail on missing driver class name")
    public void testShouldFailOnMissingDriverClassName() {
        extension.username = "username";
        extension.password = "password";
        extension.url = "jdbc:unknowndb://localhost:3306/";
        assertThatThrownBy(() -> ExtensionValidator.validateExtensionProperties(extension))
                .isInstanceOf(InvalidOrMissingPropertyException.class)
                .hasMessageContaining("Missing property")
                .hasMessageContaining("driverClassName");
    }

    @Test
    @DisplayName("Test should auto-detect JDBC driver class name")
    public void testShouldAutoDetectJdbcDriverClassName() {
        extension.username = "username";
        extension.password = "password";
        extension.url = "jdbc:mysql://localhost:3306/";
        ExtensionValidator.validateExtensionProperties(extension);

        assertThat(extension.driverClassName).isEqualTo("com.mysql.cj.jdbc.Driver");
    }
}

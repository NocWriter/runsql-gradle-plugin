package com.nocwriter.runsql.gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link PropertiesValidator}.
 *
 * @author Guy Raz Nir
 * @since 2020/03/11
 */
public class PropertiesValidatorTest {

    /**
     * Plugin's extension holding properties.
     */
    private RunSQLProperties properties;

    /**
     * Text fixture -- creates new plugin and extension before each test.
     */
    @BeforeEach
    public void setUp() {
        properties = new RunSQLProperties();
    }

    /**
     * Test should fail on missing properties, such as username, password, url and driverClassName.
     */
    @Test
    @DisplayName("Test should fai on missing extension data")
    public void testShouldFailOnMissingExtensionData() {
        assertThatThrownBy(() -> PropertiesValidator.validateExtensionProperties(properties))
                .isInstanceOf(InvalidOrMissingPropertyException.class);
    }

    @Test
    @DisplayName("Test should fail on invalid JDBC url")
    public void testShouldFailOnInvalidJdbcUrl() {
        properties.setUsername("username");
        properties.setPassword("password");
        properties.setUrl("xxx:mysql://localhost:3306/");
        assertThatThrownBy(() -> PropertiesValidator.validateExtensionProperties(properties))
                .isInstanceOf(InvalidOrMissingPropertyException.class)
                .hasMessageContaining("Invalid")
                .hasMessageContaining("non-JDBC");
    }

    @Test
    @DisplayName("Test should fail on missing driver class name")
    public void testShouldFailOnMissingDriverClassName() {
        properties.setUsername("username");
        properties.setPassword("password");
        properties.setUrl("jdbc:unknowndb://localhost:3306/");
        assertThatThrownBy(() -> PropertiesValidator.validateExtensionProperties(properties))
                .isInstanceOf(InvalidOrMissingPropertyException.class)
                .hasMessageContaining("Missing property")
                .hasMessageContaining("driverClassName");
    }

    @Test
    @DisplayName("Test should auto-detect JDBC driver class name")
    public void testShouldAutoDetectJdbcDriverClassName() {
        properties.setUsername("username");
        properties.setPassword("password");
        properties.setUrl("jdbc:mysql://localhost:3306/");
        properties.setScript("SELECT COUNT(*) FROM books;");
        PropertiesValidator.validateExtensionProperties(properties);

        assertThat(properties.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
    }
}

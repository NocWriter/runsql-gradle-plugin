package com.nocwriter.runsql.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Provide test cases for {@link ScriptParser}.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class ScriptParserTest {

    /**
     * Instance of parser to test.
     */
    private ScriptParser parser;

    /**
     * Creates a new parser before each text execution, in case the parsed was altered during previous test.
     */
    @BeforeEach
    public void setUp() {
        parser = new ScriptParser();
    }

    /**
     * Test should parse a 1-line script with a single SQL statement.
     */
    @Test
    public void testShouldParseSimpleStatement() {
        // Test query with ending character ";".
        String queryStatement = "SELECT * FROM users;";
        List<SQLStatement> statements = parser.parseScript(queryStatement);

        assertThat(statements).hasSize(1);
        assertThat(statements).contains(new SQLStatement(1, queryStatement));

        queryStatement = "SELECT * FROM users";
        statements = parser.parseScript(queryStatement);

        // Test query without ending character ";".
        assertThat(statements).hasSize(1);
        assertThat(statements).contains(new SQLStatement(1, queryStatement));
    }

    /**
     * Test parser parses two statements from the same line.
     */
    @Test
    public void testShouldParseTwoStatementsOneLine() {
        String queryStatement1 = "SELECT * FROM users;";
        String queryStatement2 = "SELECT * FROM projects;";

        List<SQLStatement> statements = parser.parseScript(queryStatement1 +
                "   \t\t" +
                queryStatement2);

        assertThat(statements)
                .hasSize(2)
                .contains(new SQLStatement(1, queryStatement1))
                .contains(new SQLStatement(1, queryStatement2));
    }

    /**
     * Test parser skips comment lines (starting with '--') and empty lines (lines with no characters or only-spaces).
     */
    @Test
    public void testShouldSkipCommentLines() {
        String queryStatement1 = "SELECT * FROM users;";
        String queryStatement2 = "SELECT * FROM projects;";
        String[] queryScript = {
                "",
                "-- This is a comment line",
                "",
                "             ",
                queryStatement1,
                "\t\t  \t",
                "       -- Another comment line, but indented one ...",
                "",
                queryStatement2
        };

        List<SQLStatement> statements = parser.parseScript(queryScript);

        // Assert that the parser extracted 2 SQL statements on line 5 and 9.
        assertThat(statements)
                .hasSize(2)
                .contains(new SQLStatement(5, queryStatement1))
                .contains(new SQLStatement(9, queryStatement2));

    }

    /**
     * Test that parsed ignored special characters, such as comment prefix (--) or query termination character (;).
     * It also tests that a quote of one type, is ignored inside another type, for example:
     * {@code SELECT *, "Author's name:" AS text1 FROM books;}.<p>
     * <p>
     * In the above case, since the ' resides insize the " .... ", it is not regarded as a quoting character.
     */
    @Test
    public void testShouldIgnoreCommentsAndSemicolonInsideQuotes() {
        String queryStatement1 = "SELECT *, 'ignored: ; -- \"' AS text1, \"and ignored: ; -- ' \" AS text2 FROM users;";
        String queryStatement2 = "SELECT * FROM projects";

        String queryScript = queryStatement1 + queryStatement2;
        List<SQLStatement> statements = parser.parseScript(queryScript);

        // Assert that the parser extracted 2 SQL statements on line 5 and 9.
        assertThat(statements)
                .hasSize(2)
                .contains(new SQLStatement(1, queryStatement1))
                .contains(new SQLStatement(1, queryStatement2));


    }
}

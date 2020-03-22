package com.nocwriter.runsql.script;

import java.util.*;

/**
 * Parses an SQL script and extract SQL statements. Lines are that are either empty or comment lines are discarded.
 *
 * @author Guy Raz Nir
 * @since 2020/03/14
 */
public class ScriptParser {

    /**
     * Indicates if newlines should be kept on the generated SQL statements or not. When a newline is kept, a statement
     * such as:
     * <pre>
     *     SELECT * FROM
     *     users;
     * </pre>
     * Will generate the statement:
     * <pre>
     *     SELECT * FROM
     *     users;
     * </pre>
     * <p>
     * If this flag is set to {@code false}, the generate SQL statement of the above snippet will be:
     * <pre>
     *     SELECT * FROM users;
     * </pre>
     */
    public boolean keepNewlines = true;

    /**
     * Class constructor.
     */
    public ScriptParser() {
    }

    /**
     * Class constructor.
     *
     * @param keepNewlines {@code true} to keep new-lines when parsing script, {@code false} to replace them with
     *                     white space.
     */
    public ScriptParser(boolean keepNewlines) {
        this.keepNewlines = keepNewlines;
    }

    public List<SQLStatement> parseScript(String... sqlScript) {
        return parseScript(Arrays.asList(sqlScript));
    }

    /**
     * Parses SQL script and return SQL statements for execution.
     *
     * @param sqlScript SQL script to parse.
     * @return SQL statements.
     */
    public List<SQLStatement> parseScript(List<String> sqlScript) {
        int lineNumber = 0;
        int statementLineNumber = 0;
        StringBuilder buf = new StringBuilder();
        List<SQLStatement> statements = new LinkedList<>();

        // Iterate over the lines of the script.
        for (String line : sqlScript) {
            // Current line number.
            lineNumber++;

            // Skip empty or comment lines.
            if (line.isBlank() || line.trim().startsWith("--")) {
                continue;
            }

            // If we've reached this point, we are currently processing an SQL statement we want to extract.
            // If the internal buffer is empty, it means that this is the beginning of the statement.
            // Otherwise, it's a part of the previous line.
            if (buf.length() == 0) {
                statementLineNumber = lineNumber;
            } else {
                buf.append(keepNewlines ? "\n" : " ");
            }

            // Iterate over the current line's character by character.
            // We handle quoting blocks limited by either ' or ".
            char[] characters = line.toCharArray();
            Character quotingChar = null;
            for (int index = 0; index < characters.length; index++) {
                if (quotingChar == null &&
                        (index + 1 < characters.length) &&
                        characters[index] == '-' &&
                        characters[index + 1] == '-') {
                    break;
                }

                buf.append(characters[index]);

                // Check if we need to start a new quoted block or end one.
                if (characters[index] == '\'' || characters[index] == '"') {
                    if (quotingChar == null) {
                        quotingChar = characters[index];
                    } else if (quotingChar == characters[index]) {
                        quotingChar = null;
                    }
                }

                // If encounter "end-of-statement" separator (;) which is not within a quoted block (e.g.: inside '...'
                // or "..."), we need to flush current statement and start processing a new statement.
                if (quotingChar == null && characters[index] == ';') {
                    statements.add(new SQLStatement(statementLineNumber, buf.toString()));

                    buf.setLength(0);

                    // If we completed flushing the last SQL statement, skip remaining spaces till the end of the line.
                    while (index + 1 < characters.length && Character.isWhitespace(characters[index + 1])) {
                        index++;
                    }
                }

            }
        }

        // If there's anything left in our internal buffer, we treat it as a statement.
        if (buf.length() > 0) {
            statements.add(new SQLStatement(statementLineNumber, buf.toString()));
        }

        return statements;
    }


}

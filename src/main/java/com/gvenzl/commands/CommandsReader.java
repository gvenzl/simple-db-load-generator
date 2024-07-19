/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: CommandsReader.java
 * Description: The commands reader.
 *
 * Copyright 2012 Gerald Venzl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gvenzl.commands;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gvenzl.parameters.Parameter;
import com.gvenzl.parameters.Parameters;
import com.gvenzl.logger.Logger;

/**
 * The CommandsReader class parses a file for commands like SQL statements or Key and Values
 * @author gvenzl
 *
 */
public class CommandsReader
{
    private final Path commandsFilePath;
    private final ArrayList<Command> commandsList;

    /***************************** MYSQL specific variables *************************/
    private static final String MYSQL_PATTERN = "((\\d{6} \\d{2}:\\d{2}:\\d{2}\\t)|(\\t{2}))\\s*\\d* (\\w+)\\t?(.+$)";
    private static final String MYSQL_SQL_COMMENT_PATTERN = "(/\\* .* \\*/)(.*)";
    private static final String[] MYSQL_SUPPORTED_COMMAND_TYPES = { "QUERY", "DELAYED INSERT", "PREPARE", "EXECUTE", "CLOSE STMT", "RESET STMT", "FETCH" };
    private static final String[] MYSQL_SUPPORTED_COMMANDS =
            { "SELECT ", "UPDATE ", "INSERT ", "DELETE ", "COMMIT", "ROLLBACK", "LOAD DATA INFILE ", "LOAD XML ", "START TRANSACTION",
                    "REPLACE ", "CALL", "SAVEPOINT", "ROLLBACK WORK TO", "ROLLBACK TO", "RELEASE SAVEPOINT", "LOCK TABLES", "UNLOCK TABLES",
                    "PREPARE", "DEALLOCATE PREPARE", "DROP PREPARE", "EXECUTE"
            };
    private boolean isMySqlFile = false;
    private boolean isMySqlSupportedCommand = false;

    /**************************** ORACLE specific variables *******************************/
    //TODO: Oracle trace file parsing
    //private static final String ORACLEPATTERN = "";
    boolean bOracleFile = false;

    /**************************** TextFile specific variables *****************************/
    private static final String TEXT_FILE_PATTERN = "(.+)(;$)";
    private static final String INLINE_COMMENT_MARKER = "--";

    /**
     * Creates a new CommandsReader instance
     * @throws FileIsDirectoryException Exception thrown if the path to the file is actually a directory
     * @throws FileNotReadable Exception thrown if the file is not readable
     * @throws NoFilePassedException Exception thrown if no file has been provided
     */
    public CommandsReader() throws FileIsDirectoryException, FileNotReadable, NoFilePassedException {

        String path = Parameters.getInstance().getParameters().getProperty(Parameter.INPUT_FILE.toString());

        if (null == path) {
            throw new NoFilePassedException("No input file has been provided!");
        }

        this.commandsFilePath = FileSystems.getDefault().getPath(path);

        // Multiple files are not supported yet, therefore no directory can be specified!
        if (Files.isDirectory(commandsFilePath)) {
            throw new FileIsDirectoryException("The passed file " + commandsFilePath.toAbsolutePath().toString() + " is a directory!");
        }
        // File not readable
        else if (!Files.isReadable(commandsFilePath)) {
            throw new FileNotReadable("File " + commandsFilePath.toAbsolutePath().toString() + " is not readable!");
        }

        this.commandsList = new ArrayList<>();
    }

    /**
     * Parses a file for executable SQLs.
     * It distinguishes between plain text SQLs, MySql General Log and Oracle trace files via RegEx patterns
     * @return a ArrayList containing all SQls of the file
     */
    public ArrayList<Command> parseCommandsFile() {
        Logger.log("Parsing commands file");

        try {
            // Read the entire file into memory
            String fileContent = new String(Files.readAllBytes(commandsFilePath));

            // Default: Unix line feed
            String separator = "\n";

            // Find line delimiter
            if (fileContent.indexOf("\r\n") > 0) { // Windows
                separator = "\r\n";
            }
            else if (fileContent.indexOf("\r") > 0) { // Mac OS X
                separator = "\r";
            }

            // Create RegEx pattern

            // MySql RegEx pattern
            Pattern mySqlPattern = Pattern.compile(MYSQL_PATTERN);

            // Oracle RDBMS RegEx pattern
            //TODO: Oracle trace file parsing
            // Pattern oraclePattern = Pattern.compile(ORACLEPATTERN);

            // Text file pattern
            Pattern textFilePattern = Pattern.compile(TEXT_FILE_PATTERN);

            // Loop over array - use for loop rather than for-each for error line tracking!
            StringBuilder multiLineTextFileCommand = new StringBuilder();
            String[] lines = fileContent.trim().split(separator);
            for (String line : lines) {

                // Create matchers for all input RegEx for current line
                Matcher mySqlMatcher = mySqlPattern.matcher(line);

                //TODO: Oracle Trace file parsing
                // Matcher oracleMatcher = oraclePattern.matcher(lines[iLineNumber]);

                Matcher textFileMatcher = textFilePattern.matcher(line);

                // Ignore comments
                if (!line.startsWith(INLINE_COMMENT_MARKER)) {
                    // MySql line matches
                    if (mySqlMatcher.matches()) {
                        isMySqlFile = true;
                        String commandType = mySqlMatcher.replaceAll("$4");
                        String command = mySqlMatcher.replaceAll("$5");
                        if (isSupportedMySqlCommand(commandType, command)) {
                            isMySqlSupportedCommand = true;
                            commandsList.add(new Command(command.replaceAll("`", "")));
                        } else {
                            isMySqlSupportedCommand = false;
                        }

                    }
                    //TODO: Oracle trace file parsing
                    // Oracle trace file line matches
                    /*else if (oracleMatcher.matches())
                    {
                        bOracleFile = true;
                    }*/
                    // Found ";" at the end of a line
                    else if (textFileMatcher.matches()) {
                        // Add potential multi lines to SQL command (default empty string)
                        multiLineTextFileCommand.append(removeInlineComment(textFileMatcher.replaceAll("$1")));
                        // Add complete sql command to list
                        commandsList.add(new Command(multiLineTextFileCommand.toString()));
                        // Reset multi line variable
                        multiLineTextFileCommand = new StringBuilder();
                    }
                    // No termination found yet, continue
                    else {
                        // Ticket 7: Multi-Line support
                        if (isMySqlFile && isMySqlSupportedCommand) {
                            // Get index of latest added command
                            int index = (commandsList.size() - 1);
                            // Get last SQL command
                            String sql = commandsList.get(index).getCommand();
                            // Append current line
                            sql = sql + " " + line;
                            // Override last added command
                            commandsList.set(index, new Command(sql));
                        }
                        // Text file could be Multi-line, save current line until command end
                        else {
                            multiLineTextFileCommand.append(removeInlineComment(line)).append(" ");
                        }
                    }
                }
            }

            Logger.log("Lines parsed: " + lines.length);
            Logger.log("Number of valid SQL statements parsed: " + commandsList.size());
        }
        catch (IOException e) {
            Logger.log("Error while reading from SQL statements file: " + commandsFilePath);
            Logger.log(e.getMessage());
        }

        return commandsList;
    }

    /**
     * Removes in-line comments.
     * @param line the line to remove the comment from.
     * @return the line without the comment.
     */
    private String removeInlineComment(String line) {
        if (line.contains(INLINE_COMMENT_MARKER)) {
            return line.substring(0, line.indexOf(INLINE_COMMENT_MARKER));
        }
        else {
            return line;
        }
    }

    /**
     * Checks whether a MySql command type and command is supported by SimpleDBLoadGenerator
     * @param commandType MySql command type (check README.md for command type reference)
     * @param command MySql command (check README.md for command reference)
     * @return A boolean indicating whether the command is supported (true) or not (false)
     */
    private boolean isSupportedMySqlCommand(String commandType, String command) {
        // Only if command type is supported
        if (Arrays.asList(MYSQL_SUPPORTED_COMMAND_TYPES).contains(commandType.toUpperCase())) {
            // If actual SQL command is supported
            // Iterate over all supported commands
            // TODO: Check for better way to do this
            for (String supportedCommand : MYSQL_SUPPORTED_COMMANDS) {
                // RegEx trim any comment in SQL (/* ...... */ but not /*+ ..... */ as those are Oracle Optimizer hints)
                // Convert command to upper case for supported commands pattern match
                // Check whether the SQL command starts with one of the supported commands
                if (command.replaceFirst(MYSQL_SQL_COMMENT_PATTERN, "$2").toUpperCase().startsWith(supportedCommand)) {
                    return true;
                }
            }
        }

        return false;
    }
}

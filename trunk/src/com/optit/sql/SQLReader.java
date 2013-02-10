package com.optit.sql;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.optit.logger.Logger;

/**
 * The SQLReader class parses a file for SQL statements
 * @author gvenzl
 *
 */
public class SQLReader
{
	private final Path sqlFilePath;
	private ArrayList<String> sqlList;
	
	/***************************** MYSQL specific variables *************************/
	private static final String MYSQLPATTERN = "((\\d{6} \\d{2}:\\d{2}:\\d{2}\\t)|(\\t{2}))\\s*\\d* (\\w+)\\t?(.+$)";
	private static final String MYSQLSQLCOMMENTPATTERN = "(/\\* .* \\*/)(.*)";
	public static final String[] MYSQLSUPPORTEDCOMMANDTYPES = { "QUERY", "DELAYED INSERT", "PREPARE", "EXECUTE", "CLOSE STMT", "RESET STMT", "FETCH" };
	public static final String[] MYSQLSUPPORTEDCOMMANDS =
									{ "SELECT ", "UPDATE ", "INSERT ", "DELETE ", "COMMIT", "ROLLBACK", "LOAD DATA INFILE ", "LOAD XML ", "START TRANSACTION",
										"REPLACE ", "CALL", "SAVEPOINT", "ROLLBACK WORK TO", "ROLLBACK TO", "RELEASE SAVEPOINT", "LOCK TABLES", "UNLOCK TABLES",
										"PREPARE", "DEALLOCATE PREPARE", "DROP PREPARE", "EXECUTE"
									};
	boolean bMySqlFile = false;
	boolean bMySqlSupportedCommand = false;

	/**************************** ORACLE specific variables *************************/
	//TODO: Oracle trace file parsing
	//private static final String ORACLEPATTERN = "";
	boolean bOracleFile = false;

	/**************************** TextFile specific variables *************************/
	private static String TEXTFILEPATTERN= "(.+)(;$)";
	
	/**
	 * Creates a new SQLReader instance
	 * @param filePath Path to the file that should be parsed
	 */
	public SQLReader (String filePath)
		throws Exception
	{
		this.sqlFilePath = FileSystems.getDefault().getPath(filePath);
		
		// Multiple files are not supported yet, therefore no directory can be specified!
		if (Files.isDirectory(sqlFilePath, new LinkOption[] {}))
		{
			throw new Exception("The passed file " + sqlFilePath.toAbsolutePath().toString() + " is a directory!" +
									"Executing multiple files is not supported yet but planned for the future!");
		}
		// File not readable
		else if (!Files.isReadable(sqlFilePath))
		{
			throw new Exception("File " + sqlFilePath.toAbsolutePath().toString() + " is not readable!");
		}
		this.sqlList = new ArrayList<String>();
	}
	
	/**
	 * Parses a file for executable SQLs.
	 * It distinguishes between plain text SQLs, MySql General Log and Oracle trace files via RegEx patterns
	 * @return a ArrayList containing all SQls of the file
	 */
	public ArrayList<String> parseSqlFile ()
	{
		Logger.log("Parsing sql file");

		try
		{
			// Read file (using new Java 7 NIO)
			String fileContent = new String(Files.readAllBytes(sqlFilePath));
						
			// Default: Unix line feed
			String separator = "\n";
			
			// Find line delimiter
			if (fileContent.indexOf("\r\n") > 0) // Windows
			{
				separator = "\r\n";
			}
			else if (fileContent.indexOf("\r") > 0) // Mac OS X
			{
				separator = "\r";
			}
			
			// Create RegEx pattern
			
			// MySql RegEx pattern
			Pattern mySqlPattern = Pattern.compile(MYSQLPATTERN);
			
			// Oracle RegEx pattern
			//TODO: Oracle trace file parsing
			// Pattern oraclePattern = Pattern.compile(ORACLEPATTERN);
			
			// Text file pattern
			Pattern textFilePattern = Pattern.compile(TEXTFILEPATTERN);
			
			// Loop over array - use for loop rather than for-each for error line tracking!
			String multiLineTextFileCommand = "";
			String[] lines = fileContent.trim().split(separator);
			for (int iLineNumber=0; iLineNumber<lines.length; iLineNumber++)
			{
				
				// Create matchers for both MySql RegEx patterns for current line
				Matcher mySqlMatcher = mySqlPattern.matcher(lines[iLineNumber]);
				
				//TODO: Oracle Trace file parsing
				// Matcher oracleMatcher = oraclePattern.matcher(lines[iLineNumber]);
				
				Matcher textFileMatcher = textFilePattern.matcher(lines[iLineNumber]);
			
				// MySqlline matches
				if (mySqlMatcher.matches())
				{
					bMySqlFile = true;
					String commandType = mySqlMatcher.replaceAll("$4");
					String command = mySqlMatcher.replaceAll("$5");
					if (isSupportedMySqlCommand(commandType, command))
					{
						bMySqlSupportedCommand = true;
						sqlList.add(command.replaceAll("`", ""));
					}
					else
					{
						bMySqlSupportedCommand = false;
					}

				}
				//TODO: Oracle trace file parsing
				// Oracle trace file line matches
				/*else if (oracleMatcher.matches())
				{
					bOracleFile = true;
				}*/
				else if (textFileMatcher.matches())
				{
					// Add potential multi lines to SQL command (default empty string)
					multiLineTextFileCommand = multiLineTextFileCommand + textFileMatcher.replaceAll("$1");
					// Add complete sql command to list
					sqlList.add(multiLineTextFileCommand);
					// Reset multi line variable
					multiLineTextFileCommand = "";
				}
				else
				{
					// Ticket 7: Multi-Line support
					if (bMySqlFile && bMySqlSupportedCommand)
					{
						// Get index of latest added command
						int index = (sqlList.size()-1);
						// Get last SQL command
						String sql = sqlList.get(index);
						// Append current line
						sql = sql + " " + lines[iLineNumber];
						// Override last added command
						sqlList.set(index, sql);
					}
					// Text file could be Multi-line, save current line until command end
					else
					{
						multiLineTextFileCommand = multiLineTextFileCommand +lines[iLineNumber] + " ";
					}
				}
			}

			Logger.log("Lines parsed: " + lines.length);
			Logger.log("Amount of valid SQLs parsed: " + sqlList.size());
		}
		catch (IOException e)
		{
			Logger.log("Error while reading from SQL file: " + sqlFilePath);
			Logger.log(e.getMessage());
		}
		
		return sqlList;
	}
	
	/**
	 * Checks whether a MySql command type and command is supported by SimpleLoadGenerator
	 * @param commandType MySql command type (check ReadMe.txt for command type reference)
	 * @param command MySql command (check ReadMe.txt for command reference)
	 * @return A boolean indicating whether the command is supported (true) or not (false) 
	 */
	private boolean isSupportedMySqlCommand(String commandType, String command)
	{
		// Only if command type is supported
		if (Arrays.asList(MYSQLSUPPORTEDCOMMANDTYPES).contains(commandType.toUpperCase()))
		{
			// If actual SQL command is supported
			List<String> supportedCommands = Arrays.asList(MYSQLSUPPORTEDCOMMANDS);
			Iterator<String> supportedCommandsIterator = supportedCommands.iterator();
			// Iterate over all supported commands
			// TODO: Check for better way to do this
			while (supportedCommandsIterator.hasNext())
			{
				// RegEx trim any comment in SQL (/* ...... */ but not /*+ ..... */ as those are Oracle Optimizer hints)
				// Convert command to upper case for supported commands pattern match
				// Check whether the SQL command starts with one of the supported commands
				if (command.replaceFirst(MYSQLSQLCOMMENTPATTERN, "$2").toUpperCase().startsWith(supportedCommandsIterator.next()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}

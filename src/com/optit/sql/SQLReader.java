package com.optit.sql;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;

import com.optit.logger.Logger;

/**
 * The SQLReader class parses a file for SQL statements
 * @author gvenzl
 *
 */
public class SQLReader
{
	private static final String separator = ";";
	private final String sqlFilePath;
	
	/**
	 * Creates a new SQLReader instance
	 * @param sqlFilePath Path to the sql file that should be parsed
	 */
	public SQLReader (String sqlFilePath)
	{
		this.sqlFilePath = sqlFilePath;
	}
	
	/**
	 * Parses a file for SQLs. Each SQL needs to be delimited by \";\" The parser does NOT ignore non SQLs nor comments! 
	 * @return a LinkedList containing all SQls of the file
	 */
	public LinkedList<String> parseSqlFile ()
	{
		Logger.log("Parsing sql file");
		LinkedList<String> returnList = new LinkedList<String>();
				
		try
		{
			// Read file (using new Java 7 NIO)
			String fileContent = new String(Files.readAllBytes(FileSystems.getDefault().getPath(sqlFilePath)));
			
			// Create list entries
			returnList.addAll(Arrays.asList(fileContent.trim().split(separator)));
			Logger.log("Amount of SQLs parsed: " + returnList.size());
		}
		catch (IOException e)
		{
			Logger.log("Error while reading from SQL file: " + sqlFilePath);
			Logger.log(e.getMessage());
		}
		
		return returnList;
	}
}

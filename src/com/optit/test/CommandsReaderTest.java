package com.optit.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.optit.commands.Command;
import com.optit.commands.CommandsReader;

/**
 * @author gvenzl
 *
 */
public class CommandsReaderTest extends TestCase
{
	@Test
	public void test_SqlReader() throws Exception
	{
		new CommandsReader("allSqls.sql");
	}
	
	@Test
	public void test_parseTextSqlFile()
		throws Exception
	{
		// Amount of SQLs in test file
		final int expectedParsedSQLs = 50;
		ArrayList<Command> sqls = new CommandsReader("allSqls.sql").parseCommandsFile();
		
		assertEquals(expectedParsedSQLs, sqls.size());
	}
	
	@Test
	public void test_parseMySqlGeneralLogFile()
		throws Exception
	{
		// Amount of valid/supported SQLs in test file
		final int expectedValidSQLs = 91;
		ArrayList<Command> sqls = new CommandsReader("mysql.general.log").parseCommandsFile();
		
		assertEquals(expectedValidSQLs, sqls.size());
	}
	
	public void test_negative_SqlReader()
	{
		try { new CommandsReader(""); } catch (Exception e) {};
	}
}

package com.optit.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.optit.Parameters;
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
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "allSqls.sql");
		new CommandsReader();
	}
	
	@Test
	public void test_parseTextSqlFile()
		throws Exception
	{
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "allSqls.sql");
		// Amount of SQLs in test file
		final int expectedParsedSQLs = 50;
		ArrayList<Command> sqls = new CommandsReader().parseCommandsFile();
		
		assertEquals(expectedParsedSQLs, sqls.size());
	}
	
	@Test
	public void test_parseMySqlGeneralLogFile()
		throws Exception
	{
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "mysql.general.log");
		// Amount of valid/supported SQLs in test file
		final int expectedValidSQLs = 91;
		ArrayList<Command> sqls = new CommandsReader().parseCommandsFile();
		
		assertEquals(expectedValidSQLs, sqls.size());
	}
	
	@Test
	public void test_parseKVFile() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "kvdata.log");
		// Amount of KVs in test file
		final int expectedKVs = 11;
		ArrayList<Command> kvs = new CommandsReader().parseCommandsFile();
		
		assertEquals(expectedKVs, kvs.size());
	}
	
	@Test
	public void test_negative_SqlReader()
	{
		try { new CommandsReader(); } catch (Exception e) {};
	}
}

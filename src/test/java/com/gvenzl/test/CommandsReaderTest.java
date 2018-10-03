package com.gvenzl.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.gvenzl.Parameters;
import com.gvenzl.commands.Command;
import com.gvenzl.commands.CommandsReader;

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
		
		// Iterate over all objects and check for not null
		for (Command cmd : sqls)
		{
			assertNotNull(cmd.getCommand());
		}
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
		
		// Iterate over all objects and check for not null
		for (Command cmd : sqls)
		{
			assertNotNull(cmd.getCommand());
		}
	}
	
	@Test
	public void test_parseKVFile() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "kvdata.log");
		// Amount of KVs in test file
		final int expectedKVs = 11;
		ArrayList<Command> kvs = new CommandsReader().parseCommandsFile();
		
		assertEquals(expectedKVs, kvs.size());
		
		// Iterate over all objects and check for not null
		for (Command cmd : kvs)
		{
			assertNotNull(cmd.getKey());
			assertFalse(cmd.getValue().toString().equals("<Value format:NONE bytes:>"));
		}
	}
	
	@Test
	public void test_negative_SqlReader()
	{
		try { new CommandsReader(); } catch (Exception e) {};
	}
}
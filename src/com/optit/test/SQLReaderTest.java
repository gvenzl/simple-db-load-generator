package com.optit.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.optit.sql.SQLReader;

/**
 * @author gvenzl
 *
 */
public class SQLReaderTest extends TestCase
{
	@Test
	public void test_SqlReader() throws Exception
	{
		new SQLReader("allSqls.sql");
	}
	
	@Test
	public void test_parseTextSqlFile()
		throws Exception
	{
		// Amount of SQLs in test file
		final int expectedParsedSQLs = 50;
		ArrayList<String> sqls =new SQLReader("allSqls.sql").parseSqlFile();
		
		assertEquals(expectedParsedSQLs, sqls.size());
	}
	
	@Test
	public void test_parseMySqlGeneralLogFile()
		throws Exception
	{
		// Amount of valid/supported SQLs in test file
		final int expectedValidSQLs = 91;
		ArrayList<String> sqls = new SQLReader("mysql.general.log").parseSqlFile();
		
		assertEquals(expectedValidSQLs, sqls.size());
	}
	
	public void test_negative_SqlReader()
	{
		try { new SQLReader(""); } catch (Exception e) {};
	}
}

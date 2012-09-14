package com.optit.test;

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
		new SQLReader("");
	}
	
	@Test
	public void test_parseSqlFile() throws Exception
	{
		new SQLReader("allSqls.sql").parseSqlFile();
	}
	
	@Test
	public void test_negative_parseSqlFile() throws Exception
	{
		new SQLReader("").parseSqlFile();
	}
}

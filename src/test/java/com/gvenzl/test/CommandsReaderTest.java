/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: CommandsReaderTest.java
 * Description:
 *
 * Copyright 2018 Gerald Venzl
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
	public void test_SqlReader() {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/allSqls.sql");
		new CommandsReader();
	}
	
	@Test
	public void test_parseTextSqlFile() {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/allSqls.sql");
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
	public void test_parseMySqlGeneralLogFile() {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/mysql.general.log");
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
	public void test_parseKVFile() {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/kvdata.log");
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
		try { new CommandsReader(); } catch (Exception e) {}
	}
}

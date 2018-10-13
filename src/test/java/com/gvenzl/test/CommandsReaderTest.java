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

import java.io.File;
import java.util.ArrayList;

import com.gvenzl.commands.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.gvenzl.Parameters;

public class CommandsReaderTest {

	@Test
	public void test_SqlReader() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/allSqls.sql");
		new CommandsReader();
	}
	
	@Test
	public void test_parseTextSqlFile() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/allSqls.sql");
		// Amount of SQLs in test file
		final int expectedParsedSQLs = 50;
		ArrayList<Command> sqls = new CommandsReader().parseCommandsFile();
		
		Assert.assertEquals(expectedParsedSQLs, sqls.size());
		
		// Iterate over all objects and check for not null
		for (Command cmd : sqls)
		{
			Assert.assertNotNull(cmd.getCommand());
		}
	}
	
	@Test
	public void test_parseMySqlGeneralLogFile() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/mysql.general.log");
		// Amount of valid/supported SQLs in test file
		final int expectedValidSQLs = 91;
		ArrayList<Command> sqls = new CommandsReader().parseCommandsFile();
		
		Assert.assertEquals(expectedValidSQLs, sqls.size());
		
		// Iterate over all objects and check for not null
		for (Command cmd : sqls)
		{
			Assert.assertNotNull(cmd.getCommand());
		}
	}
	
	@Test
	public void test_parseKVFile() throws Exception {
		Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources/kvdata.log");
		// Amount of KVs in test file
		final int expectedKVs = 11;
		ArrayList<Command> kvs = new CommandsReader().parseCommandsFile();
		
		Assert.assertEquals(expectedKVs, kvs.size());
		
		// Iterate over all objects and check for not null
		for (Command cmd : kvs)
		{
			Assert.assertNotNull(cmd.getKey());
			Assert.assertNotEquals("<Value format:NONE bytes:>", cmd.getValue().toString());
		}
	}
	
	@Test (expected = NoFilePassedException.class)
	public void test_negative_CommandReader_no_file_provided() throws Exception	{

		new CommandsReader();
	}

	@Test (expected = FileIsDirectoryException.class)
    public void test_negative_CommandReader_file_is_directory() throws Exception {

        Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, "src/test/resources");
        new CommandsReader();
    }

    @Test
    public void test_negative_CommandReader_file_is_readonly() throws Exception {

	    String fileName = "src/test/resources/readonly.test";
        File testFile = new File(fileName);
        if (! testFile.createNewFile())
        	throw new Exception("Test execution error: Test file cannot be created!");

        if (! testFile.setReadable(false)) {
            throw new Exception("Test execution error: Test file cannot be modified!");
        }

        Parameters.getInstance().getParameters().setProperty(Parameters.inputFile, fileName);
        try {
            new CommandsReader();
        } catch (FileNotReadable e) {
            if (! testFile.delete()) {
                throw new Exception("Test execution error: Test file cannot be cleaned up!");
            }
        }
    }

    @After
    public void tearDown() {
	    Parameters.getInstance().tearDown();
    }

}

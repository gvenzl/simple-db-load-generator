/*
 * Since: October, 2013
 * Author: gvenzl
 * Name: CommandTest.java
 * Description: Tests the Command class.
 *
 * Copyright 2013 Gerald Venzl
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

import org.junit.Assert;
import org.junit.Test;

import com.gvenzl.commands.Command;

public class CommandTest {

	private final String command = "select test from dual";

	@Test
	public void test_ConstructorString() {
		Command cmd = new Command(command);
		Assert.assertEquals(Command.TYPE.SQL, cmd.getType());
		Assert.assertEquals(command, cmd.getCommand());
	}
	
	@Test
	public void test_getCommand() {
		Assert.assertEquals(command, new Command(command).getCommand());
	}
}

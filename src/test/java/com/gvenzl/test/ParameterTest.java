/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: ParameterTest.java
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

import junit.framework.TestCase;

import org.junit.Test;

import com.gvenzl.Parameters;

/**
 * @author gvenzl
 *
 */
public class ParameterTest extends TestCase
{
	private class MyParams extends Parameters
	{
		
	}
	
	@Test
	public void test_instantiate()
	{
		System.out.println("Test Parameters instanziation");
		new MyParams();
	}
	
	@Test
	public void test_values()
	{
		assertEquals("dbType", MyParams.dbType);
		assertEquals("verbose", Parameters.verbose);
		assertEquals("host", Parameters.host);
		assertEquals("password", Parameters.password);
		assertEquals("port", Parameters.port);
		assertEquals("sessions", Parameters.sessions);
		assertEquals("dbName", Parameters.dbName);
		assertEquals("inputFile", Parameters.inputFile);
		assertEquals("user", Parameters.user);
	}
}

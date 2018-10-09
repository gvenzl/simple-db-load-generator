/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: AllTest.java
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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author gvenzl
 *
 */
public class AllTest extends TestSuite
{
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for all JUnit tests");
		suite.addTestSuite(SimpleLoadGeneratorTest.class);
		suite.addTestSuite(LoggerTest.class);
		suite.addTestSuite(ParameterTest.class);
		suite.addTestSuite(CommandsReaderTest.class);
		suite.addTestSuite(RandomIteratorTest.class);
		suite.addTestSuite(CommandTest.class);
		suite.addTestSuite(LoaderDataSourceTest.class);
		
		return suite;
	}
}

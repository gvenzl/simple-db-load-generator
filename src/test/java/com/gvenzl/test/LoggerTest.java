/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: LoggerTest.java
 * Description: Tests the Logger class.
 *
 * Copyright 2012 Gerald Venzl
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

import org.junit.Test;

import com.gvenzl.logger.Logger;

public class LoggerTest {

	@Test
	public void test_logEmpty() {
		System.out.println("Test logger new line");
		Logger.log();
	}
	
	@Test
	public void test_logNotEmpty() {
		System.out.println("Test logger output");
		Logger.log("Testline test test test");
	}
	
	@Test
	public void test_logErr() {
		System.out.println("Test logger to error output");
		Logger.logErr("Testline error error error");
	}
	
	@Test
	public void test_logTimed() {
		System.out.println("Test logger with timing output");
		Logger.logTimed("Testline test test test");
		Logger.logTimed("Second test line test test test");
	}
	
	@Test
	public void test_setDebug() {
		Logger.setVerbose(true);
	}
	
	@Test
	public void test_logDebug() {
		Logger.setVerbose(true);
		Logger.logVerbose("This is a DEBUG OUTPUT!");
	}
}

package com.gvenzl.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.gvenzl.logger.Logger;

/**
 * @author gvenzl
 *
 */
public class LoggerTest extends TestCase
{
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

package com.gvenzl.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.gvenzl.SimpleLoadGenerator;

/**
 * @author gvenzl
 *
 */
public class SimpleLoadGeneratorTest extends TestCase
{
	@Test
	public void test_printHelp() {
		System.out.println("Test online help");
		SimpleLoadGenerator.printHelp();
	}
	
	@Test
	public void test_handleException() {
		System.out.println("Test exception handling of non-caught exceptions");
		SimpleLoadGenerator.handleExceptions(new RuntimeException());
	}
}

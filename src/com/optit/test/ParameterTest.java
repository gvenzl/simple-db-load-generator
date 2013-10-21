package com.optit.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.optit.Parameters;

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
		assertEquals("databaseType", MyParams.databaseType);
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

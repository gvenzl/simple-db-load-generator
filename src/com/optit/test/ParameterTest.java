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
		assertEquals("debug", Parameters.debug);
		assertEquals("host", Parameters.host);
		assertEquals("password", Parameters.password);
		assertEquals("port", Parameters.port);
		assertEquals("sessions", Parameters.sessions);
		assertEquals("sid", Parameters.sid);
		assertEquals("sqlfile", Parameters.sqlfile);
		assertEquals("user", Parameters.user);
	}
}

package com.optit.test;

import junit.framework.TestCase;
import oracle.kv.Key;
import oracle.kv.Value;

import org.junit.Test;

import com.optit.commands.Command;

public class CommandTest extends TestCase
{
	private final String command = "select test from dual";
	private final Key key = Key.createKey("MajorKey", "minorKey");
	private final Value value = Value.createValue("This is my cool test value".getBytes());
	
	@Test
	public void test_ConstrutorString() {		
		Command cmd = new Command(command);
		assertEquals(Command.TYPE.SQL, cmd.getType());
		assertEquals(command, cmd.getCommand());
	}
	
	public void test_ConstructorKeyValue() {
		Command cmd = new Command(key, value);
		assertEquals(Command.TYPE.KV, cmd.getType());
		assertEquals(key, cmd.getKey());
		assertEquals(value, cmd.getValue());
	}
	
	public void test_getCommand() {
		assertEquals(command, new Command(command).getCommand());
	}
	
	public void test_getKey() {
		assertEquals(key, new Command(key, null).getKey());
	}
	
	public void test_getValue() {
		assertEquals(value, new Command(null, value).getValue());
	}
	
}

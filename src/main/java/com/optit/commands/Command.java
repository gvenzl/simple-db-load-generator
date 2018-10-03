package com.optit.commands;

import oracle.kv.Key;
import oracle.kv.Value;

//TODO: Check whether an interface can be used for put and get operations

/**
 * Holds a command to execute (either SQL or KV)
 * @author gvenzl
 *
 */
public class Command
{
	private String commandString;
	private Key key;
	private Value value;
	private TYPE type;
	
	public enum TYPE { SQL, KV };
	
	public Command (String command)	{
		commandString = command;
		type = TYPE.SQL;
	}
	
	public Command (Key key, Value val) {
		this.key = key;
		this.value = val;
		type = TYPE.KV;
	}
	
	public TYPE getType() {
		return type;
	}

	public String getCommand () {
		return commandString;
	}
	
	public Key getKey() {
		return key;
	}
	
	public Value getValue() {
		return value;
	}
	
}

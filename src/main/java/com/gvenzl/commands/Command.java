/*
 * Since: October, 2013
 * Author: gvenzl
 * Name: Command.java
 * Description: The call resembling a command.
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

package com.gvenzl.commands;

//TODO: Check whether an interface can be used for put and get operations

/**
 * Holds a command to execute (either SQL)
 * @author gvenzl
 *
 */
public class Command
{
	private String commandString;
	private final TYPE type;
	
	public enum TYPE { SQL }

    public Command (String command)	{
		commandString = command;
		type = TYPE.SQL;
	}

	
	public TYPE getType() {
		return type;
	}

	public String getCommand () {
		return commandString;
	}
	
}

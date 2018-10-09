/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: ExecutorThread.java
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

package com.gvenzl.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import oracle.kv.KVStore;

import com.gvenzl.Parameters;
import com.gvenzl.commands.Command;
import com.gvenzl.connection.DbType;
import com.gvenzl.logger.Logger;
import com.gvenzl.util.RandomIterator;

/**
 * The ExecutorThread is a thread having one connection to the database
 * It will generate load against the database until the volatile stop flag is set via the stop() method
 * @author gvenzl
 *
 */
class ExecutorThread extends Thread
{
	private static boolean ignoreErrors = false;
	private Connection conn;
	private KVStore kvStoreConn;
	private final List<Command> commands;
	
	private boolean stop = false;
	
	/**
	 * Creates a new ExecutorThread object
	 * @param conn A connection to the database
	 * @param commands A List of all commands that should be executed
	 */
	public ExecutorThread(Connection conn, List<Command> commands)
	{
		this.conn = conn;
		this.commands = commands;
		ignoreErrors = Boolean.valueOf(Parameters.getInstance().getParameters().getProperty(Parameters.ignoreErrors));
		
		try {
			this.conn.setAutoCommit(false);
		}
		catch (SQLException e) {
			Logger.log("Could not set AutoCommit to false: " + e.getMessage());
			Logger.log("Test will continue with default AutoCommit value!");
		}
	}
	
	/**
	 * Creates a new ExecutorThread object
	 * @param kvStore A connection to the KV store
	 * @param commands A List of all commands that should be executed
	 */
	public ExecutorThread(KVStore kvStore, List<Command> commands)
	{
		this.kvStoreConn = kvStore;
		this.commands = commands;
		ignoreErrors = Boolean.valueOf(Parameters.getInstance().getParameters().getProperty(Parameters.ignoreErrors));
	}
	
	/**
	 * Returns the fully qualified thread name (name + id)
	 * @return The fully qualified thread name
	 */
	private String getFullName()
	{
		return Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
	}
	
	/**
	 * Stop routine for thread
	 */
	public void stopThread()
	{
		stop = true;
	}
	
	@Override
	public void run()
	{
		// New Random generator for random wait
		Random random = new Random();
		
		// Database type
		DbType dbType = DbType.getType(Parameters.getInstance().getParameters().getProperty(Parameters.dbType));
		
		// Run as long as the Thread doesn't get interrupted (via Ctrl+C)
		while (!stop) {
			// Iterate over all SQLs
			RandomIterator<Command> iterator = new RandomIterator<>(commands);
			
			// Endless loop for commands until thread has to stop - loop will be broken by NoSuchElementException of the iterator
			while (!stop) {
				Command cmd;

				try	{ cmd = iterator.next(); }
				catch (NoSuchElementException e) {
					// No more elements in the list - break inner loop
					Logger.log(this.getFullName() + ": All commands executed, re-executing...");
					// Break loop
					break;
				}
				
				long startTime;
				long endTime;
				
				switch (dbType)
				{
					case MYSQL:
					case ORACLE:
					{
						String sqlCommand = cmd.getCommand().trim();
						try (PreparedStatement stmt = conn.prepareStatement(sqlCommand))
						{
							Logger.log(this.getFullName() + ": Executing SQL...");
							Logger.logVerbose(this.getFullName() + ": SQL text: " + sqlCommand);
							
							int rows=0;
							// executeQuery for selects
							if (sqlCommand.toLowerCase().startsWith("select")) 
							{
								startTime = System.currentTimeMillis();
								ResultSet rslt = stmt.executeQuery();
							
								// Fetching all results - in order to produce I/O
								while (rslt.next())	{
									rows++;
								}
								endTime = System.currentTimeMillis();
							}
							else {
								startTime = System.currentTimeMillis();
								stmt.execute();
								endTime = System.currentTimeMillis();
							}
							Logger.log(this.getFullName() + ": " + rows + " rows in set - (" + (endTime - startTime) + "ms)");					
						}
						catch (SQLException e)
						{
							Logger.log(this.getFullName() + ": SQL error detected!");
							Logger.log(this.getFullName() + ": " + e.getMessage());
								
							// Only if errors should not be ignored stop thread
							if (!ignoreErrors) {
								Logger.log(this.getFullName() + ": Stopping thread due to SQL error!");
								stop = true;
							}
						}
						break;
					}
					case NOSQL:
					{
						try {
    						startTime = System.currentTimeMillis();
    						kvStoreConn.get(cmd.getKey());
    						kvStoreConn.put(cmd.getKey(), cmd.getValue());
    						endTime = System.currentTimeMillis();
						
    						Logger.log(this.getFullName() + ": Key '" + cmd.getKey().toString() + "' read and written (" + (endTime - startTime) + "ms)");
						} catch (Exception e) {
							Logger.log(this.getFullName() + ": Error detected!");
							Logger.log(this.getFullName() + ": " + e.getMessage());
								
							// Only if errors should not be ignored stop thread
							if (!ignoreErrors) {
								Logger.log(this.getFullName() + ": Stopping thread due to error!");
								stop = true;
							}
						}
						break;
					}
				}
				
				try {
					// Wait random amount of milliseconds between 0 and 1000 (1 second max) before executing next SQL
					int sleep = random.nextInt(1000);
					Logger.log(this.getFullName() + ": Sleeping for " + sleep + "ms");
					Thread.sleep(sleep);
				}
				catch (InterruptedException ie) {
					Logger.log(this.getFullName() + ": Interrupt (Ctrl+C) detected, stopping thread!");
					stop = true;
				}
			}
		}

		Logger.log(this.getFullName() + ": Closing Db connection...");

		switch (dbType)
		{
			case MYSQL:
			case ORACLE: {
				try {
					// Close database connection
					conn.rollback(); 
					conn.close();
				}
				catch (SQLException e) {
					// Ignore exception while closing, program is about to stop
					Logger.logVerbose(this.getFullName() + ": Error closing Db connection: " + e.getMessage());
				}
			}
			case NOSQL: {
				kvStoreConn.close();
				break;
			}
			
		}

		Logger.log(this.getFullName() + ": Stopping...");
	}
}

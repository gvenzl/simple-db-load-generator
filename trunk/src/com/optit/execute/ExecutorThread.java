package com.optit.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import oracle.kv.KVStore;

import com.optit.Parameters;
import com.optit.commands.Command;
import com.optit.logger.Logger;
import com.optit.util.RandomIterator;

/**
 * The ExecutorThread is a thread having one connection to the database
 * It will generate load against the database until the volatile stop flag is set via the stop() method
 * @author gvenzl
 *
 */
public class ExecutorThread extends Thread
{
	private static boolean ignoreErrors = false;
	private Connection conn;
	private KVStore kvStoreConn;
	private List<Command> commands;
	
	private enum TYPE { RDBMS, KV };
	private final TYPE type;
	
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
		ignoreErrors = Boolean.valueOf(Parameters.getInstance().getParameters().getProperty(Parameters.ignoreErrors)).booleanValue();
		type = TYPE.RDBMS;
		
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
		ignoreErrors = Boolean.valueOf(Parameters.getInstance().getParameters().getProperty(Parameters.ignoreErrors)).booleanValue();
		type = TYPE.KV;
	}
	
	/**
	 * Returns the fully qualified thread name (name + id)
	 * @return The fully qualified thread name
	 */
	public String getFullName()
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
		
		// Run as long as the Thread doesn't get interrupted (via Ctrl+C)
		while (!stop)
		{
			// Iterate over all SQLs
			RandomIterator<Command> iterator = new RandomIterator<Command>(commands);
			
			// Endless loop for commands until thread has to stop - loop will be broken by NoSuchElementException of the iterator
			while (!stop)
			{
				Command cmd;

				try	{ cmd = iterator.next(); }
				catch (NoSuchElementException e) {
					// No more elements in the list - break inner loop
					Logger.log(this.getFullName() + ": All commands executed, re-executing...");
					// Break loop
					break;
				}
				
				long startTime = 0;
				long endTime = 0;
				
				switch (type)
				{
					case RDBMS:
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
							if (!ignoreErrors)
							{
								Logger.log(this.getFullName() + ": Stopping thread due to SQL error!");
								stop = true;
							}
						}
						break;
					}
					case KV:
					{
						startTime = System.currentTimeMillis();
						kvStoreConn.get(cmd.getKey());
						kvStoreConn.put(cmd.getKey(), cmd.getValue());
						endTime = System.currentTimeMillis();
						
						Logger.log(this.getFullName() + ": Key '" + cmd.getKey().toString() + "' read and written (" + (endTime - startTime) + "ms)");
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

		switch (type)
		{
			case KV:
			{
				kvStoreConn.close();
				break;
			}
			case RDBMS:
			{
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
			
		}

		Logger.log(this.getFullName() + ": Stopping...");
	}
}

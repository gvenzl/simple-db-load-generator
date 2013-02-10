package com.optit.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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
	private List<String> sqls;
	
	private boolean stop = false;
	
	/**
	 * Creates a new ExecutorThread object
	 * @param conn A connection to the database
	 * @param sqls A List of all sqls that should be executed
	 * @param ignoreErrs Flag that defines if errors caused by SQLs should force the thread to stop or not
	 */
	public ExecutorThread(Connection conn, List<String> sqls, boolean ignoreErrs)
	{
		this.sqls = sqls;
		this.conn = conn;
		ignoreErrors = ignoreErrs;
		
		try
		{
			this.conn.setAutoCommit(false);
		}
		catch (SQLException e)
		{
			Logger.log("Could not set AutoCommit to false: " + e.getMessage());
			Logger.log("Test will continue with default AutoCommit value!");
		}
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
			RandomIterator<String> iterator = new RandomIterator<String>(sqls);
			
			// Endless loop for SQLs until thread has to stop - loop will be broken by NoSuchElementException of the iterator
			while (!stop)
			{
				String sql;

				try
				{
					sql = iterator.next().trim();
				}
				catch (NoSuchElementException e)
				{
					// No more elements in the list - break inner loop
					Logger.log(this.getFullName() + ": All SQLs executed, re-executing...");
					break;
				}

				// Run next statement
				try (PreparedStatement stmt = conn.prepareStatement(sql))
				{
					Logger.log(this.getFullName() + ": Executing SQL...");
					Logger.logVerbose(this.getFullName() + ": SQL text: " + sql);
					
					long startTime = 0;
					int rows=0;
					// executeQuery for selects
					if (sql.toLowerCase().startsWith("select"))
					{
						startTime = System.currentTimeMillis();
						ResultSet rslt = stmt.executeQuery();
					
						// Fetching all results - in order to produce I/O
						while (rslt.next())
						{
							rows++;
						}
					}
					else
					{
						startTime = System.currentTimeMillis();
						stmt.execute();
					}
					Logger.log(this.getFullName() + ": " + rows + " rows in set - (" + (System.currentTimeMillis()-startTime) + "ms)");					

					// Wait random amount of milliseconds between 0 and 1000 (1 second max) before executing next SQL
					int sleep = random.nextInt(1000);
					Logger.log(this.getFullName() + ": Sleeping for " + sleep + "ms");
					Thread.sleep(sleep);
				}
				catch (InterruptedException | SQLException e)
				{
					if (e instanceof SQLException)
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
					else
					{
						Logger.log(this.getFullName() + ": Interrupt (Ctrl+C) detected, stopping thread!");
						stop = true;
					}
				}
			}
		}
		
		try
		{
			// Close database connection
			Logger.log(this.getFullName() + ": Closing Db connection...");
			conn.rollback();
			conn.close();
		}
		catch (SQLException e)
		{
			// Ignore exception while closing, program is about to stop
			Logger.logVerbose(this.getFullName() + ": Error closing Db connection: " + e.getMessage());
		}
		Logger.log(this.getFullName() + ": Stopping...");
	}
}

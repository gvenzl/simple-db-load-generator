package com.optit.execute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.optit.Parameters;
import com.optit.logger.Logger;
import com.optit.sql.SQLReader;

/**
 * The executor class is responsible to establish the data source to the database, run the SQLReader to parse the sql file 
 * and start/stop the test execution threads.
 * @author gvenzl
 */
public class Executor
{
	private final Properties parameters;
	
	/**
	 * Inner class for ShutDown hook. Needed in order to stop all threads gracefully
	 * @author gvenzl
	 *
	 */
	private class ShutDown extends Thread
	{
		ExecutorThread[] threadsToStop;
		
		/**
		 * Constructs a new ShutDown class
		 * @param threads Started threads which need to be stopped again
		 */
		public ShutDown(ExecutorThread[] threads)
		{
			this.threadsToStop = threads;
		}
		
		@Override
		public void run()
		{
			// Set stop flag for each thread
			for (int i=0;i<threadsToStop.length;i++)
			{
				threadsToStop[i].stopThread();
			}
			
			// Wait until each thread has finished
			for (int i=0;i<threadsToStop.length;i++)
			{
				try
				{
					threadsToStop[i].join();
				}
				catch (InterruptedException e)
				{
					// Ignore exception
				}
			}
		}
	}
	
	/**
	 * This constructor creates a new Executor instance
	 * @param parameters A instance of the Parameter object that contains the runtime parameters
	 */
	public Executor (Properties parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 * This method initialises a new data source to the database
	 * @return A new DataSource instance to the database. NULL if the DataSource couldn't be established.
	 * @throws SQLException Any SQL/DB exception during database connection establishment
	 */
	private DataSource initializeDataSource()
		throws SQLException
	{
		Logger.log("Initialise the DataSource...");
		Logger.log("Database type: " + parameters.getProperty(Parameters.databaseType));
	
		DataSource ds = null;
		String url = "";
		switch (parameters.getProperty(Parameters.databaseType))
		{
			case "oracle":
			{
				ds = new OracleDataSource();
				url = "jdbc:oracle:thin:" + parameters.getProperty(Parameters.user) + "/" + parameters.getProperty(Parameters.password) + "@//" +
						parameters.getProperty(Parameters.host) + ":" + parameters.getProperty(Parameters.port) + "/" + parameters.getProperty(Parameters.sid);
				((OracleDataSource) ds).setURL(url);
				((OracleDataSource) ds).setImplicitCachingEnabled(true);
				break;
			}
			case "mysql":
			{
				ds = new MysqlDataSource();
				url = "jdbc:mysql://" + parameters.getProperty(Parameters.host) + ":" + parameters.getProperty(Parameters.port) + "/" + parameters.getProperty(Parameters.sid) +
							"?user=" + parameters.getProperty(Parameters.user) + "&password=" + parameters.getProperty(Parameters.password);
				((MysqlDataSource) ds).setURL(url);
				break;
			}
			default:
			{
				Logger.log("Wrong database type specified! Cannot establish database connection!");
			}
		}
		Logger.logDebug("URL used to connect: " + url);
		
		return ds; 
	}

	/**
	 * Starts the test execution
	 */
	public void runTests()
	{
		try
		{
			// Initialise the Data Source
			DataSource dbDataSource = initializeDataSource();
			if (dbDataSource != null)
			{
	
				// Parse all SQLs into a ArrayList 
				ArrayList<String> sqls = new SQLReader(parameters.getProperty(Parameters.sqlfile)).parseSqlFile();
				
				// Only execute tests if LinkedList is filled (not filled if parse error occurred)
				if (!sqls.isEmpty())
				{
					// Start and execute load threads
					int sessions = Integer.valueOf(parameters.getProperty(Parameters.sessions)).intValue();
					Logger.log("Starting concurrent sessions: " + sessions);
					
					ExecutorThread[] threads = new ExecutorThread[sessions];
					for (int iSession=0;iSession<sessions;iSession++)
					{
						threads[iSession] = new ExecutorThread(dbDataSource.getConnection(), sqls, Boolean.valueOf(parameters.getProperty(Parameters.ignoreErrors)).booleanValue());
						threads[iSession].setName("SQL-Loader");
						threads[iSession].start();
					}
					
					// Add shutdown hook for Ctrl+C capture
					Runtime.getRuntime().addShutdownHook(new ShutDown(threads));
					
					// Wait for all threads to finish
					for (int iThread=0;iThread<threads.length;iThread++)
					{
						try
						{
							threads[iThread].join();
						}
						catch (InterruptedException e)
						{
							// Ignore thread interrupt!
						}
					}
				}
			}
			
		}
		catch (SQLException e)
		{
			Logger.log("Error during database connection establishment: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

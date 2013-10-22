package com.optit.execute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.kv.FaultException;
import oracle.kv.KVStoreConfig;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.optit.Parameters;
import com.optit.commands.Command;
import com.optit.commands.CommandsReader;
import com.optit.connection.LoaderDataSource;
import com.optit.logger.Logger;

/**
 * The executor class is responsible to establish the data source to the database, run the CommandsReader to parse the sql file 
 * and start/stop the test execution threads.
 * @author gvenzl
 */
public class Executor
{
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
		public ShutDown(ExecutorThread[] threads) {
			this.threadsToStop = threads;
		}
		
		@Override
		public void run() {
			// Set stop flag for each thread
			for (int i=0;i<threadsToStop.length;i++) {
				threadsToStop[i].stopThread();
			}
			
			// Wait until each thread has finished
			for (int i=0;i<threadsToStop.length;i++) {
				try {
					threadsToStop[i].join();
				}
				catch (InterruptedException e) { /* Ignore exception */ }
			}
		}
	}
	
	/**
	 * This method initializes a new data source to the database
	 * @return A new DataSource instance to the database. NULL if the DataSource couldn't be established.
	 * @throws SQLException Any SQL/DB exception during database connection establishment
	 */
	private LoaderDataSource initializeDataSource()
		throws SQLException, FaultException
	{
		Properties parameters = Parameters.getInstance().getParameters();
		String host = parameters.getProperty(Parameters.host);
		String port = parameters.getProperty(Parameters.port);
		String dbName = parameters.getProperty(Parameters.dbName);
		String user = parameters.getProperty(Parameters.user);
		String password = parameters.getProperty(Parameters.password);
		
		Logger.log("Initialise the DataSource...");
		Logger.log("Database type: " + Parameters.getInstance().getParameters().getProperty(Parameters.databaseType));
	
		LoaderDataSource dataSource = null;
		String url = "";
		
		switch (parameters.getProperty(Parameters.databaseType))
		{
			case "oracle":
			{
				OracleDataSource ods = new OracleDataSource();
				url = "jdbc:oracle:thin:" + user + "/" + password + "@//" + host + ":" + port + "/" + dbName;
				ods.setURL(url);
				ods.setImplicitCachingEnabled(true);
				
				// Test connection establishment
				ods.getConnection();
				dataSource = new LoaderDataSource(ods);
				break;
			}
			case "mysql":
			{
				MysqlDataSource mysqlds = new MysqlDataSource();
				url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?user=" + user + "&password=" + password;
				mysqlds.setURL(url);
				
				// Test connection establishment
				mysqlds.getConnection();
				dataSource = new LoaderDataSource(mysqlds);
				break;
			}
			case "kvstore":
			{
				dataSource = new LoaderDataSource(new KVStoreConfig(dbName, host + ":" + port));
				break;
			}
			default:
			{
				Logger.log("Wrong database type specified! Cannot establish database connection!");
				throw new RuntimeException("Wrong database type specified! " + parameters.getProperty(Parameters.databaseType) + " is not supported!");
			}
		}

		Logger.logVerbose("URL used to connect: " + url);
		
		return dataSource; 
	}

	/**
	 * Starts the test execution
	 */
	public void runTests()
	{
		try
		{
			// Initialize the Data Source
			LoaderDataSource dataSource = initializeDataSource();
			// Parse all SQLs into a ArrayList 
			try	{
				ArrayList<Command> commands = new CommandsReader().parseCommandsFile();
				
				// Only execute tests if LinkedList is filled (not filled if parse error occurred)
				if (!commands.isEmpty())
				{
					// Start and execute load threads
					int sessions = Integer.valueOf(Parameters.getInstance().getParameters().getProperty(Parameters.sessions)).intValue();
					Logger.log("Starting concurrent sessions: " + sessions);
					
					ExecutorThread[] threads = new ExecutorThread[sessions];
					for (int iSession=0;iSession<sessions;iSession++)
					{
						switch ((String)Parameters.getInstance().getParameters().get(Parameters.databaseType)) {
							case "oracle":
							case "mysql": {
								threads[iSession] = new ExecutorThread(dataSource.getDBConnection(), commands);
								break;
							}
							case "kvstore": {
								threads[iSession] = new ExecutorThread(dataSource.getKVConnection(), commands);
								break;
							}
						}
						
						threads[iSession].setName("Loader");
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
			catch (Exception e)
			{
				Logger.log("Error during input file parsing: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		catch (Exception e)
		{
			Logger.log("Error during database connection establishment: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

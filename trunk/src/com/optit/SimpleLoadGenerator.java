package com.optit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.optit.execute.Executor;
import com.optit.logger.Logger;

/**
 * The SimpleLoadGenerator class is the main class and entry point for the generator tool.
 * It gets the required parameters to test and prints the online help.
 * @author gvenzl
 */
public class SimpleLoadGenerator
{
	/**
	 * Print the online help into stdout
	 */
	public static void printHelp()
	{
		Logger.log("Usage: java -jar SimpleLoadGenerator.jar|com.optit.SimpleLoadGenerator -user [username] -password [password] -host [host] -port [port] -sid [sid] -databaseType [databaseType] -sessions [Amount of sessions] -sqlfile [sqlfile] -ignoreErrors -debug -help|-h|--help|-?");
        Logger.log("");
        Logger.log("[-user]			The database username");
        Logger.log("[-password]		The password of the database user");
        Logger.log("[-host]			Database machine host name");
        Logger.log("[-port]			Listener port of the database listener");
        Logger.log("[-sid]			Database SID/name");
        Logger.log("[-databaseType]		Database type: oracle|mysql");
        Logger.log("[-sessions]		Amount of sessions that should execute the queries");
        Logger.log("[-sqlfile]		Path to file containing the SQL statements to execute");
        Logger.log("[-ignoreErrors]		Ignore errors caused by SQL statements and carry on executing");
        Logger.log("[-debug]		Enables debug output");
        Logger.log("[-help|--help|-h|-?]	Display this help");
        Logger.log();
        Logger.log("If a properties file (SimpleLoadGenerator.properties) exists, the system will load the parameters from there!");
        Logger.log("You will not need to specify any additional parameter. However, the properties file will only be read when you do not pass any parameters on!");
        Logger.log("Each SQL statement in the sql file has to be delimited by \";\".");
        Logger.log("The application does not handle escaped \";\" and will treat them as delimiter!");
        Logger.log("The application does not do a implicit commit! If you want to execute DML statements you will have to include a COMMIT statement!");
	}

	/**
	 * The main method and entry point into the program.
	 * @param args The parameters passed for executed the tests.
	 */
	public static void main(String[] args)
	{
		// Initialise application parameter properties
		Properties parameters = new Properties();

		// Set default parameters
		parameters.setProperty(Parameters.sessions, "1");
		parameters.setProperty(Parameters.debug, "false");
		parameters.setProperty(Parameters.ignoreErrors, "false");
		
		// No parameters passed, read parameters from properties file
		// Do not attempt to read the properties file if any parameter has been passed via the CLI
		if (args.length == 0)
		{
			String propertiesFileName = SimpleLoadGenerator.class.getSimpleName() + ".properties";
			
			try(FileInputStream fis = new FileInputStream(propertiesFileName))
			{
				parameters.load(fis);
			}
			catch (IOException ioEx)
			{
				// Properties file cannot be found!
				if (ioEx instanceof FileNotFoundException)
				{
					Logger.log("Properties file \"" + propertiesFileName + "\" was not found in current working directory (" + System.getProperties().getProperty("user.dir") + ")");
					Logger.log("Please use command line parameters or define a poperties file");
					Logger.log();

					printHelp();
					System.exit(0);
				}
				// Other IO exception - print out error and exit
				else
				{
					handleExceptions(ioEx);
					System.exit(0);
				}
			}
			catch (IllegalArgumentException illArgEx)
			{
				Logger.log("The properties file contains illegal characters!");
				handleExceptions(illArgEx);
				System.exit(0);
			}
		}
		
		// Parameters were passed on, properties file ignored -> read passed on parameters
		
		for (int i=0;i<args.length;i++)
		{
			if (args[i].equals("-" + Parameters.user))
			{
				parameters.setProperty(Parameters.user, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.password))
			{
				parameters.setProperty(Parameters.password, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.host))
			{
				parameters.setProperty(Parameters.host, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.port))
			{
				parameters.setProperty(Parameters.port, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.sid))
			{
				parameters.setProperty(Parameters.sid, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.databaseType))
			{
				parameters.setProperty(Parameters.databaseType, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.sessions))
			{
				parameters.setProperty(Parameters.sessions, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.sqlfile))
			{
				parameters.setProperty(Parameters.sqlfile, args[++i]);
			}
			else if (args[i].equals("-" + Parameters.debug))
			{
				parameters.setProperty(Parameters.debug, "true");
			}
			else if (args[i].equals("-" + Parameters.ignoreErrors))
			{
				parameters.setProperty(Parameters.ignoreErrors, "true");
			}
			else if (args[i].equals("-help") || args[i].equals("--help") || args[i].equals("-h") || args[i].equals("-?"))
			{
				printHelp();
				System.exit(0);
			}
			else
			{
				Logger.log("Unknown parameter: " + args[i]);
				Logger.log();
				printHelp();
				System.exit(0);
			}
		}
		
		// Set debug flag based on parameter
		Logger.setDebug(parameters.getProperty(Parameters.debug).equalsIgnoreCase("true"));
		
		// main try/catch block to avoid exception being thrown out not handled
		try
		{
			// Create executor, pass on parameters and run tests
			Executor executor = new Executor(parameters);
			executor.runTests();
		}
		catch (Exception e)
		{
			handleExceptions(e);
		}
	}
	
	/**
	 * This function sits on the top level and catches all exceptions and prints out proper error messages 
	 * @param e The exception that comes from somewhere within the code
	 */
	public static void handleExceptions(Exception e)
	{
		Logger.log("Application error: " + e.getMessage());
		if (e.getCause() != null)
		{
			Logger.log("Caused by: " + e.getCause().toString());
		}
		e.printStackTrace(System.err);
	}
}
package com.optit;

import java.util.Properties;

/**
 * This class holds the values for the parameters defined.
 * Whenever a value changes like "user" to "UserName" it has just to be changed here once
 * @author gvenzl
 *
 */
public class Parameters
{
	public static final String user = "user";
	public static final String password = "password";
	public static final String host = "host";
	public static final String port = "port";
	public static final String dbName = "dbName";
	public static final String dbType = "dbType";
	public static final String sessions = "sessions";
	public static final String inputFile = "inputFile";
	public static final String verbose = "verbose";
	public static final String ignoreErrors = "ignoreErrors";
	
	private Properties parameters;
	
	private static Parameters _instance = new Parameters();
	
	public Parameters() {		
		// Initialize application parameter properties
		parameters = new Properties();

		// Set default parameters
		parameters.setProperty(Parameters.sessions, "1");
		parameters.setProperty(Parameters.verbose, "false");
		parameters.setProperty(Parameters.ignoreErrors, "false");
		
	}
	
	public static Parameters getInstance() {
		return _instance;
	}
	
	public Properties getParameters() {
		return parameters;
	}
}

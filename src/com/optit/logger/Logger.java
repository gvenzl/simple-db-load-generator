package com.optit.logger;

/**
 * The Logger is used as an interface for logging information
 * @author Gerald Venzl
 *
 */
public class Logger
{
	private static long milliSecs = 0;
	private static boolean debugFlag = false;
	
	/**
	 * En- or disables debugging output
	 * @param debug True=enable debug; False=disable debug
	 */
	public static void setDebug(boolean debug)
	{
		debugFlag = debug;
	}
	
	/**
	 * Logs one line into the standard output
	 * @param line The line to log
	 */
	public static void log(String line)
	{
		synchronized (System.out)
		{
			System.out.println(line);
		}
	}
	
	/**
	 * Logs one line into the standard output only if debug is enabled
	 * @param line The line to log
	 */
	public static void logDebug(String line)
	{
		if (debugFlag)
		{
			synchronized (System.out)
			{
				System.out.println("DEBUG: " + line);
			}
		}
	}
	
	
	/**
	 * Logs a new line into the stanard output
	 */
	public static void log()
	{
		synchronized (System.out)
		{
			System.out.println();
		}
	}

	/**
	 * Logs a line into the standard output with timing set
	 * @param line The line to log
	 */
	public static void logTimed(String line)
	{
		if (milliSecs == 0)
		{
			milliSecs = System.currentTimeMillis();
			synchronized (System.out)
			{
				System.out.println(line);
			}
		}
		else
		{
			long duration = System.currentTimeMillis()-milliSecs;
			milliSecs = 0;
			synchronized (System.out)
			{
				System.out.println("Duration: " + duration + "ms - " + line);
			}
		}
	}
	
	/**
	 * Logs an error into the error output
	 * @param line The line to log
	 */
	public static void logErr(String line)
	{
		synchronized (System.err)
		{
			System.err.println(line);
		}
	}
}

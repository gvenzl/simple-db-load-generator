/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: Logger.java
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

package com.gvenzl.logger;

/**
 * The Logger is used as an interface for logging information
 * @author gvenzl
 *
 */
public class Logger
{
	private static long milliSecs = 0;
	private static boolean verboseFlag = false;
	
	/**
	 * En- or disables verbose output
	 * @param verbose Enable or disable verbose output
	 */
	public static void setVerbose(boolean verbose)
	{
		verboseFlag = verbose;
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
	 * Logs one line into the standard output only if verbose is enabled
	 * @param line The line to log
	 */
	public static void logVerbose(String line)
	{
		if (verboseFlag)
		{
			synchronized (System.out)
			{
				System.out.println("VERBOSE: " + line);
			}
		}
	}
	
	
	/**
	 * Logs a new line into the standard output
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

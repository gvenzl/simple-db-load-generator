/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: Parameters.java
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

package com.gvenzl;

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
	
	private final Properties parameters;
	
	private static Parameters _instance;
	
	protected Parameters() {
		// Initialize application parameter properties
		parameters = new Properties();

		// Set default parameters
		parameters.setProperty(Parameters.sessions, "1");
		parameters.setProperty(Parameters.verbose, "false");
		parameters.setProperty(Parameters.ignoreErrors, "false");
		
	}
	
	public static Parameters getInstance() {

	    if (null == _instance) {
	        _instance = new Parameters();
        }

		return _instance;
	}
	
	public Properties getParameters() {
		return parameters;
	}

	public void tearDown() {
	    _instance = null;
    }
}

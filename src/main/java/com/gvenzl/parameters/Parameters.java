/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: Parameters.java
 * Description: The runtime parameters passed on.
 *
 * Copyright 2012 Gerald Venzl
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

package com.gvenzl.parameters;

import java.util.Properties;

/**
 * This class holds the values for the parameters defined.
 * Whenever a value changes like "user" to "UserName" it has just to be changed here once
 * @author gvenzl
 *
 */
public class Parameters
{
    private final Properties parameters;

    private static Parameters _instance;

    protected Parameters() {
        // Initialize application parameter properties
        parameters = new Properties();

        // Set default parameters
        parameters.setProperty(Parameter.SESSIONS.toString(), "1");
        parameters.setProperty(Parameter.VERBOSE.toString(), "false");
        parameters.setProperty(Parameter.IGNORE_ERRORS.toString(), "false");

    }

    public static Parameters getInstance() {

        if (null == _instance) {
            _instance = new Parameters();
        }

        return _instance;
    }

    /**
     * Get the parameter names and values
     * @return {@link Properties} containing the parameter names and values
     */
    public Properties getParameters() {
        return parameters;
    }

    /**
     * Returns a parameter value.
     * @param key the parameter key to look for
     * @return the parameter value
     */
    public static String getParameter(String key) {
        return getInstance().getParameters().getProperty(key);
    }

    public void tearDown() {
        _instance = null;
    }
}

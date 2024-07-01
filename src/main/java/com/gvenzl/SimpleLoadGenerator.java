/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: SimpleLoadGenerator.java
 * Description: The main class
 *
 * Copyright 2024 Gerald Venzl
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.gvenzl.execute.Executor;
import com.gvenzl.logger.Logger;
import com.gvenzl.parameters.Parameter;
import com.gvenzl.parameters.Parameters;

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
        Logger.log("Usage: java -jar SimpleLoadGenerator.jar|com.gvenzl.SimpleLoadGenerator -user [username] -password [password] -host [host] -port [port] -dbName [dbName] -dbType [dbType] -sessions [number of sessions] -inputFile [inputFile] -ignoreErrors -verbose -help|-h|--help|-?");
        Logger.log("");
        Logger.log("[-user]			The database username");
        Logger.log("[-password]		The password of the database user");
        Logger.log("[-host]			Database machine host name");
        Logger.log("[-port]			Listener port of the database listener");
        Logger.log("[-dbName]		Database name/service name");
        Logger.log("[-dbType]		Database type: oracle|mysql");
        Logger.log("[-sessions]		Number of sessions that should execute the queries");
        Logger.log("[-inputFile]		Path to file containing the commands (e.g. SQL statements) to execute");
        Logger.log("[-ignoreErrors]		Ignore errors caused by SQL statements and continue executing");
        Logger.log("[-verbose]		Enables verbose output");
        Logger.log("[-help|--help|-h|-?]	Display this help");
        Logger.log();
        Logger.log("By default, if not parameters have been provided, SimpleLoadGenerator will look for a properties file called 'SimpleLoadGenerator.properties'.");
        Logger.log("Each SQL statement in the plain text sql file has to be delimited by \";\\n\".");
        Logger.log("SimpleLoadGenerator does not execute an implicit commit. If you want to execute DML statements you will have to include a COMMIT statement.");
    }

    /**
     * Parses the command line arguments
     * @param args a {@link String} array of command line parameters
     */
    private static void parseArgs(String[] args) {

        Properties params = Parameters.getInstance().getParameters();

        for (int i=0;i<args.length;i++) {

            if (args[i].equalsIgnoreCase("-" + Parameter.USER.toString())) {
                params.setProperty(Parameter.USER.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.PASSWORD.toString())) {
                params.setProperty(Parameter.PASSWORD.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.HOST.toString())) {
                params.setProperty(Parameter.HOST.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.PORT.toString())) {
                params.setProperty(Parameter.PORT.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.DB_NAME.toString())) {
                params.setProperty(Parameter.DB_NAME.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.DB_TYPE.toString())) {
                params.setProperty(Parameter.DB_TYPE.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.SESSIONS.toString())) {
                params.setProperty(Parameter.SESSIONS.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.INPUT_FILE.toString())) {
                params.setProperty(Parameter.INPUT_FILE.toString(), args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.VERBOSE.toString())) {
                params.setProperty(Parameter.VERBOSE.toString(), "true");
            }
            else if (args[i].equalsIgnoreCase("-" + Parameter.IGNORE_ERRORS.toString())) {
                params.setProperty(Parameter.IGNORE_ERRORS.toString(), "true");
            }
            else if (args[i].equalsIgnoreCase("-help") || args[i].equals("--help") || args[i].equals("-h") || args[i].equals("-?")) {
                printHelp();
                System.exit(0);
            }
            else {
                Logger.log("Unknown parameter: " + args[i]);
                Logger.log();
                printHelp();
                System.exit(0);
            }
        }
    }

    /**
     * The main method and entry point into the program.
     * @param args The parameters passed for executed the tests.
     */
    public static void main(String[] args) {
        // No parameters passed, read parameters from properties file
        // Do not attempt to read the properties file if any parameter has been passed via the CLI
        if (args.length == 0) {
            String propertiesFileName = SimpleLoadGenerator.class.getSimpleName() + ".properties";

            try(FileInputStream fis = new FileInputStream(propertiesFileName)) {
                Parameters.getInstance().getParameters().load(fis);
            }
            catch (IOException ioEx) {
                // Properties file cannot be found!
                if (ioEx instanceof FileNotFoundException) {
                    Logger.log("Properties file \"" + propertiesFileName + "\" was not found in current working directory (" + System.getProperties().getProperty("user.dir") + ")");
                    Logger.log("Please use command line parameters or define a properties file.");
                    Logger.log();

                    printHelp();
                    System.exit(0);
                }
                // Other IO exception - print out error and exit
                else {
                    handleExceptions(ioEx);
                    System.exit(0);
                }
            }
            catch (IllegalArgumentException illArgEx){
                Logger.log("The properties file contains illegal characters!");
                handleExceptions(illArgEx);
                System.exit(0);
            }
        }
        else {
            parseArgs(args);
        }

        // Set debug flag based on parameter
        Logger.setVerbose(Parameters.getInstance().getParameters().getProperty(Parameter.VERBOSE.toString()).equalsIgnoreCase("true"));

        // main try/catch block to avoid exception being thrown out not handled
        try
        {
            // Create executor and run tests
            new Executor().runTests();
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
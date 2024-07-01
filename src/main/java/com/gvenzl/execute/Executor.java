/*
 * Since: February, 2012
 * Author: gvenzl
 * Name: Executor.java
 * Description: The executor program to execute the tests.
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

package com.gvenzl.execute;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gvenzl.parameters.Parameter;
import com.mysql.cj.jdbc.MysqlDataSource;
import oracle.jdbc.pool.OracleDataSource;

import com.gvenzl.parameters.Parameters;
import com.gvenzl.commands.Command;
import com.gvenzl.commands.CommandsReader;
import com.gvenzl.connection.DbType;
import com.gvenzl.connection.LoaderDataSource;
import com.gvenzl.logger.Logger;

/**
 * The executor class is responsible to establish the data source to the database, run the CommandsReader to parse the sql file 
 * and start/stop the test execution threads.
 * @author gvenzl
 */
public class Executor
{
    private LoaderDataSource dataSource;

    /**
     * Constructs a new Executor instance
     * @throws SQLException Any SQL/DB exception during database connection establishment
     */
    public Executor() throws SQLException {
        try {
            initializeDataSource();
        }
        catch (SQLException e) {
            Logger.log("Error during database connection establishment: " + e.getMessage());
            e.printStackTrace(System.err);
            throw e;
        }
    }
    /**
     * Inner class for ShutDown hook. Needed to stop all threads gracefully
     * @author gvenzl
     *
     */
    private class ShutDown extends Thread
    {
        final ExecutorThread[] threadsToStop;

        /**
         * Constructs a new ShutDown class
         * @param threads Started threads which need to be stopped again
         */
        ShutDown(ExecutorThread[] threads) {
            this.threadsToStop = threads;
        }

        @Override
        public void run() {
            // Set stop flag for each thread
            for (ExecutorThread aThreadsToStop : threadsToStop) {
                aThreadsToStop.stopThread();
            }

            // Wait until each thread has finished
            for (ExecutorThread aThreadsToStop : threadsToStop) {
                try {
                    aThreadsToStop.join();
                }
                catch (InterruptedException e) { /* Ignore exception */ }
            }
        }
    }

    /**
     * This method initializes a new data source to the database
     * @throws SQLException Any SQL/DB exception during database connection establishment
     */
    private void initializeDataSource()
            throws SQLException
    {
        String host = Parameters.getParameter(Parameter.HOST.toString());
        String port = Parameters.getParameter(Parameter.PORT.toString());

        String user = Parameters.getParameter(Parameter.USER.toString());
        String password = Parameters.getParameter(Parameter.PASSWORD.toString());

        DbType dbType = DbType.getType(Parameters.getParameter(Parameter.DB_TYPE.toString()));
        String dbName = Parameters.getParameter(Parameter.DB_NAME.toString());

        Logger.log("Initialize the DataSource...");
        Logger.log("Database type: " + dbType);

        LoaderDataSource dataSource;
        String url = "";

        // Creating the datasource to the database. Extend this if you need to add additional support!
        switch (dbType)
        {
            case ORACLE:
            {
                OracleDataSource ods = new OracleDataSource();
                url = String.format("jdbc:oracle:thin:%s/%s@//%s:%s/%s", user, password, host, port, dbName);
                ods.setURL(url);
                ods.setImplicitCachingEnabled(true);

                // Test connection establishment
                Connection conn = ods.getConnection();
                conn.close();
                dataSource = new LoaderDataSource(ods);
                break;
            }
            case MYSQL:
            {
                MysqlDataSource mysqlds = new MysqlDataSource();
                url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", host, port, dbName, user, password);
                mysqlds.setURL(url);

                // Test connection establishment
                Connection conn = mysqlds.getConnection();
                conn.close();
                dataSource = new LoaderDataSource(mysqlds);
                break;
            }
            default:
            {
                Logger.log("Wrong database type specified, cannot establish database connection!");
                throw new RuntimeException("Wrong database type specified! " + Parameters.getParameter(Parameter.DB_TYPE.toString()) + " is not supported!");
            }
        }

        Logger.logVerbose("URL used to connect: " + url);

        this.dataSource = dataSource;
    }

    /**
     * Starts the test execution
     */
    public void runTests() throws Exception
    {
        // Parse all SQLs into a ArrayList
        ArrayList<Command> commands = new CommandsReader().parseCommandsFile();

        // Only execute tests if LinkedList is filled (not filled if parse error occurred)
        if (!commands.isEmpty())
        {
            // Start and execute load threads
            int sessions = Integer.parseInt(Parameters.getInstance().getParameters().getProperty(Parameter.SESSIONS.toString()));
            Logger.log("Starting concurrent sessions: " + sessions);

            ExecutorThread[] threads = new ExecutorThread[sessions];
            try {
                for (int iSession=0; iSession < sessions; iSession++)
                {
                    switch (DbType.getType(Parameters.getInstance().getParameters().getProperty(Parameter.DB_TYPE.toString()))) {
                        case ORACLE:
                        case MYSQL: {
                            threads[iSession] = new ExecutorThread(dataSource.getDBConnection(), commands);
                            break;
                        }
                    }

                    threads[iSession].setName("Loader");
                    threads[iSession].start();
                }
            }
            catch (SQLException e) {
                Logger.log("Error during database connection establishment: " + e.getMessage());
                e.printStackTrace(System.err);
            }

            // Add shutdown hook for Ctrl+C capture
            Runtime.getRuntime().addShutdownHook(new ShutDown(threads));

            // Wait for all threads to finish
            for (ExecutorThread thread : threads) {
                try {
                    thread.join();
                }
                catch (InterruptedException e) {
                    // Ignore thread interrupt!
                }
            }
        }
    }
}

# SimpleLoadGenerator
Copyright 2018 Gerald Venzl

## Content

1.  Purpose  
2.  Concept  
2.1 Plain text file re-execution  
2.2 MySQL general log file re-execution  
2.3 Oracle trace file re-execution  
3.  Build  
4.  Running SimpleLoadGenerator  

## 1. Purpose

Purpose of this application is to enable the user to generate/simulate some user load.
It uses a user defined set of SQL statements which can executed in a single or multiple database sessions

## 2. Concept

SimpleLoadGenerator was born out of the need to re-execute some production system SQL statements against a test database with the possibility to scale up the execution workload.
The SQL statements were provided via a database trace file which then got trimmed down to the actual SQL statements.
The idea spawn further to provide a generic load tool where someone could quickly generate either some random load against a database or execute quickly a couple of statements concurrently.

The original design was based on reading a plain text file which contains SQL statements.
SimpleLoadGenerator recognizes every byte in the file as part of a SQL statement until a ";" followed by a line end, is encountered which delimits the current SQL statement from the next one.
This means that when you run a plain text file, you can make your SQL statements as long and as many lines as you want, but you have to put a ";" at the end of a line to tell SimpleLoadGenerator
that the SQL command is finished. Previously SimpleLoadGenerator had the limitation that no ";" could appear within a SQL command, this limitation is now gone!

With version 1.1.0 came the improvement to read MySQL general log and Oracle trace files. This improvement was intended to make it
easier for the user to re-execute a SQL load without having to extract the SQLs into a plain file first.
However, by re-executing trace files there is a danger of re-executing some internal or recursive SQL commands that should not or must not be executed.
Because of that danger not all SQLs will be plainly re-executed as it is the case with plain text files! For the various restrictions see the sections below!

The application parses the entire SQL file and keeps all the SQL statements in memory which then get executed by one or more database sessions (configurable).
The SQL statements are executed in RANDOM order and the execution of the next statement is paused for a random generated number between 0 and 1000 milliseconds.
This is done to simulate a more realistic SQL load. Also, SimpleLoadGenerator detects SELECT statements and automatically fetches all rows of the SELECT being executed.
This too is done to simulate a more realistic SQL load.
Once all the statements are executed the application starts over again, ultimately producing an infinite loop.
Only a kill signal ([Ctrl]+[C]) or an error (in case the -ignoreErrors flag is not set - see below) will cause the application to stop gracefully
by stopping the execution after the current statement has been finished and closing the connection.

### 2.1 Plain text file re-execution

SimpleLoadGenerator will execute whatever is in the text file. This makes SimpleLoadGenerator very flexible and does not constrain the SQLs to SELECT statements only.
In case that the text file contains INSERT/UPDATE/DELETE statements that will start transactions, SimpleLoadGenerator WILL NOT execute a commit automatically.
If a commit needs to be executed, it will have to be in the text file as "COMMIT;" at the appropriate place!
The application will also not execute a rollback at the end of each test cycle which could cause the UNDO tablespace on an Oracle database to grow significantly.
It is believed that if DML loads are simulated, the user also wants to commit them, e.g. batch load simulation

### 2.2 MySQL general log file re-execution

The MySQL general log file is a plain text of binary file containing SQL command executions. SimpleLoadGenerator supports the plain text mode only!
The general log file has two different formats of lines. Whenever the time between the last and current execution changes for a second or more,
the date and time will be printed first, then followed by the thread id, command type and SQL command text.

The format looks like this ([\t] representing a tabulator character):  
`[Date][Space][Time][\t][ThreadId][Space][CommandType][\t][Sql Text][\n]`  
`[\t][\t][ThreadId][Space][CommandType][\t][Sql Text][\n]`

A regular expression is used to parse those two lines accordingly.
The used expression is:  
`((\d{6} \d{2}:\d{2}:\d{2}\t)|(\t{2}))\s*\d* (\w+)\t?(.+$)"`

MySQL lists following command types:

    "Sleep"
    "Quit"
    "Init DB"
    "Query"
    "Field List"
    "Create DB"
    "Drop DB"
    "Refresh"
    "Shutdown"
    "Statistics"
    "Processlist"
    "Connect"
    "Kill"
    "Debug"
    "Ping"
    "Time"
    "Delayed insert"
    "Change user"
    "Binlog Dump"
    "Table Dump"
    "Connect Out"
    "Register Slave"
    "Prepare"
    "Execute"
    "Long Data"
    "Close stmt"
    "Reset stmt"
    "Set option"
    "Fetch"
    "Daemon"
    "Error"

Out of that list SimpleLoadGenerator supports only following of those SQL command types:  

    "Query"
    "Delayed insert"
    "Prepare"
    "Execute"
    "Close stmt"
    "Reset stmt"
    "Fetch"

#### Supported commands

Query commands:

    CALL
    DELETE
    INSERT 
    LOAD DATA INFILE 
    LOAD XML 
    REPLACE 
    SELECT 
    UPDATE

Transaction Controlling statements:

    START TRANSACTION
    COMMIT
    ROLLBACK
    SAVEPOINT
    ROLLBACK [WORK] TO
    RELEASE SAVEPOINT
    LOCK TABLES
    UNLOCK TABLES

Prepare commands:

    PREPARE
    DEALLOCATE PREPARE
    DROP PREPARE
 
Execute commands:

    EXECUTE

#### Non-supported commands

Query commands:

    DO
    HANDLER 
    SET AUTOCOMMIT
    SET TRANSACTION

Controlling Master Servers Statements:

    PURGE BINARY LOGS
    RESET MASTER
    SET sql_log_bin

Controlling Slave Servers Statements:

    CHANGE MASTER TO
    MASTER_POS_WAIT()
    RESET SLAVE
    SET GLOBAL sql_slave_skip_counter
    START SLAVE
    STOP SLAVE

Compound-Statement Syntax:

    BEGIN ... END
    DECLARE

Other Non-supported commands:

    ALTER USER
    CREATE USER
    DROP USER
    GRANT
    RENAME USER
    REVOKE
    SET PASSWORD
    ANALYZE TABLE
    CHECK TABLE
    CHECKSUM TABLE
    OPTIMIZE TABLE
    REPAIR TABLE
    CREATE FUNCTION
    DROP FUNCTION
    INSTALL PLUGIN
    UNINSTALL PLUGIN
    SET
    SHOW
    BINLOG
    CACHE INDEX
    FLUSH
    KILL
    LOAD INDEX INTO CACHE
    RESET
    DESCRIBE
    EXPLAIN
    HELP
    USE

### 2.3 Oracle trace file re-execution
//TODO: Add Oracle trace file support

## 3. Build

An Ant build file is provided in the "build" directory. To build the program you have to
invoke the default target "build". Also you have to have the Oracle JDBC driver (11.2)
and MySql Connector/J (5.1) libraries within the classpath.

## 4. Execute a load run

### 4.1 Prerequisites

SimpleLoadGenerator requires either the Oracle 11g JDBC driver in the "ojdbc6.jar" library or the MySql Connector/J in the "mysql-connector-java-5.1.NN-bin.jar" library
in the classpath/working directory, depending on which database the load is executed against.
The Oracle JDBC driver can be found in $ORACLE_HOME/jdbc/lib
The MySql Connector/J can be downloaded for free at http://www.mysql.com/downloads/connector/j/  

### 4.2. Set correct Java version

SimpleLoadGenerator runs with Java 1.7+ and is successfully tested with Update 13.

    export JAVA_HOME=/usr/jdk1.7.0_06
    export PATH=$JAVA_HOME/bin:$PATH
    java -version
      java version "1.7.0_13"
      Java(TM) SE Runtime Environment (build 1.7.0_13-b20)
      Java HotSpot(TM) 64-Bit Server VM (build 23.7-b01, mixed mode)

### 4.3 JVM parameters

No special JVM parameters are needed. However, depending on how big the SQL file is, the default max heap size could be insufficient.
This can be controlled by the -Xmx JVM parameter:

    -Xmx256m  --> Maximum heap memory size of 256MB
    -Xmx512m  --> Maximum heap memory size of 512MB
    -Xmx1024m --> Maximum heap memory size of 1024MB

Example:

    java -Xmx512m -jar SimpleLoadGenerator.jar

### 4.4. Running SimpleLoadGenerator

The program comes with a default run script (run.sh for Unix, run.bat for Windows)

    ./run.sh

SimpleLoadGenerator provides two ways how to specify parameters. This can be done by either a properties file (SimpleLoadGenerator.properties) or via command line.
The command line can get quite long with the database connection details and usually you only specify them once and then you just adjust the SQL statements in the file or the amount of database sessions.
Therefore it's a good practice to use the properties file rather than the command line parameters.

SimpleLoadGenerator will ignore any command line parameters if a properties file is found in the working directory!

Following parameter need to be set:

    -user [username]:                         The username of the database user  
    -password [password]:                     The password of the user  
    -host [hostname]:                         Database machine host name or IP address  
    -port [port]:                             Listener port of the database listener  
    -sid [sid]:                               Database SID/name  
    -databaseType [oracle|mysql]:             Specify whether the load is against an Oracle or MySql database  
    -sqlfile [path to SQL statements file]:   The absolute or relative path to the SQL statements file to load  
    -sessions [amount of sessions]:           The amount of sessions that should execute the SQLs against the database (default: 1)  
    -ignoreErrors:                            Specifies whether the application should ignore failing SQL statements and continue with the load  
    -debug:                                   Enables debugging output, mostly useful to see which SQL statements got executed when  
    -help:                                    Shows the online help only  

Example:

     java -jar SimpleLoadGenerator.jar -user scott -password tiger \
     -host localhost -port 1521 -sid MYDB -databaseType oracle \
     -ignoreErrors -sqlfile ./SQLs.txt -sessions 10 -ignoreErrors

# License
    Copyright 2018 Gerald Venzl

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

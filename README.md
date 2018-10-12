# SimpleLoadGenerator

## Content

1.  Purpose  
2.  Concept  
2.1 Plain text file re-execution  
2.2 MySQL general log file re-execution  
3.  Build  
4.  Running SimpleLoadGenerator  

## 1. Purpose

Purpose of this application is to enable users to generate or simulate some work load on a database.
SimpleLoadGenerator uses a predefined set of SQL statements which can be executed in a single or in multiple database sessions, the latter for scaling the workload.

## 2. Concept

SimpleLoadGenerator was born out of the need to re-execute some production system SQL statements against a test database with the possibility to scale up the execution workload.
The SQL statements to re-execute were provided via a database trace file which got trimmed down to the actual SQL statements.
The idea spawn further to provide a generic load tool where someone could quickly generate either some random load against a database or execute a couple of statements concurrently.

The original design was based on reading a plain text file which contains SQL statements.
SimpleLoadGenerator recognizes every byte in the file as part of a SQL statement until a ";" followed by a line end/carriage return (`EOL`) is encountered which delimits the current SQL statement from the next one.
Because of that SQL statements can be many lines long, as long as the end is clearly marked by ";" + `EOL`.

With version 1.1.0 came the improvement to read MySQL general log files. This improvement was intended to make it
easier for the user to re-execute a SQL load without having to extract the SQLs into a plain file first.
However, by re-executing trace files there is a danger of re-executing some internal or recursive SQL commands that should not or must not be executed.
Because of that not all SQLs are executed from a trace file. For the various restrictions on trace file execution see the sections below!

The application parses the entire SQL file and keeps all the SQL statements in memory for the duration of the program runtime.
The SQL statements are then executed by one or more database sessions (configurable).
Furthermore, the SQL statements are executed in **RANDOM** order. The pause between the execution of two statements is a random generated number between 0 and 1000 milliseconds.
This is done to simulate a more realistic SQL load. Also, SimpleLoadGenerator detects `SELECT` statements and automatically fetches all rows of the `SELECT` being executed.
This too is done to simulate a more realistic SQL load, making sure that all data of a `SELECT` has been fetched over the network.
Once all the statements are executed, the application starts over again ultimately producing an infinite loop.
Only a kill signal ([Ctrl]+[C]) or an error (in case the `-ignoreErrors` flag is not set - see below) will cause the application to stop gracefully.

### 2.1 Plain text file re-execution

SimpleLoadGenerator will execute whatever is in the text file. This makes SimpleLoadGenerator very flexible and does not constrain the SQLs to `SELECT` statements only.
In case that the text file contains `INSERT`/`UPDATE`/`DELETE` statements that will start transactions, SimpleLoadGenerator WILL NOT execute a commit automatically.
If a commit needs to be executed, it will have to be in the text file as "COMMIT;"!
The application will not execute a rollback at the end of each test cycle.
It is believed that if DML loads are simulated, the user also wants to commit them, e.g. batch load simulation.
Otherwise, the user will have to explicitly add "ROLLBACK;", if desired.

### 2.2 MySQL general log file re-execution

The MySQL general log file comes in plain text or binary format, containing SQL command executions.
SimpleLoadGenerator supports the plain text format only!
The general log file has two different line formats.
Whenever the time between the last and current execution changes for a second or more,
the date and time will be printed first, followed by the thread id, command type and SQL command text.
In case the time hasn't changed, i.e. the SQL has been executed the very same second as the previous one, the date and time will be omitted.

The format looks like this (`[\t]` representing a tabulator character):  
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

## 3. Build

SimpleLoadGenerator is a Maven project. The Maven `pom.xml` file has following dependencies declared:

    <dependency>
        <groupId>com.oracle</groupId>
        <artifactId>ojdbc8</artifactId>
        <version>12.2.0.1</version>
    </dependency>
    <dependency>
       <groupId>com.oracle</groupId>
       <artifactId>kvclient</artifactId>
        <version>18.1.16</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.12</version>
    </dependency>
    
Both, the Oracle JDBC driver 12.2.0.1 and the Oracle NoSQL Client 18.1.16 
need to be installed into the local Maven repository first. This can be done via:

    mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
        -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=compile

You can build SimpleLoadGenerator by simply running:

    mvn clean package
    
This produces a packaged jar under `simpleloadgenerator/target/SimpleLoadGenerator<version>.jar`

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

SimpleLoadGenerator provides two ways on how to specify the input parameters.
This can be done by either a properties file (SimpleLoadGenerator.properties) or via command line.
The command line can get quite long with the database connection details and usually remains static while users adjust SQL statements in the file or the amount of database sessions more often.
It is therefore a good practice to use the properties file rather than the command line parameters.

**Note:** SimpleLoadGenerator will ignore any command line parameters if a properties file is found in the working directory!

Following parameter need to be set:

    -user           [username]             The username of the database user  
    -password       [password]             The password of the user  
    -host           [hostname]             Database machine host name or IP address  
    -port           [port]                 Listener port of the database listener  
    -sid            [sid]                  Database SID/name  
    -databaseType   [oracle|mysql]         Specify whether the load is against an Oracle or MySql database  
    -sqlfile        [path to SQLs file]    The absolute or relative path to the SQL statements file to load  
    -sessions       [amount of sessions]   The amount of sessions that should execute the SQLs against the database (default: 1)  
    -ignoreErrors                          Specifies whether the application should ignore failing SQL statements and continue with the load  
    -debug                                 Enables debugging output, mostly useful to see which SQL statements got executed when  
    -help                                  Shows the online help only  

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

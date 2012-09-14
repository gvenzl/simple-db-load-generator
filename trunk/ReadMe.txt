SimpleLoadGenerator - (c) Gerald Venzl

Content:

1. Purpose
2. Concept
3. Build
4. Running SimpleLoadGenerator

1. Purpose:

Purpose of this application is to enable the user to generate/simulate some user load.
It uses a user defined set of SQL statements which can executed in a single or multiple database sessions

2. Concept:

SimpleLoadGenerator was born out of the need to replay some production system SQL statements against a test database.
The SQL statements were provided via a database trace file which then got trimmed down to the actual SQL statements.

The design is therefore based on reading a plain text file which contains SQL statements.
It recognizes every byte in the file as part of a SQL statement until a ";" is encountered which delimits the current SQL statement from the next one.
SimpleLoadGenerator does not handle escaped ";" right now which means that your SQL statement must not contain a ";" anywhere inside it.

The application parses the entire SQL file and keeps all the SQL statements in memory which then get executed by one or more database sessions (configurable).
The SQL statements are executed in RANDOM order and the execution of the next statement is paused for a random generated number between 0 and 1000 milliseconds.
This is done to simulate a more realistic user load. Once all the statements are executed the application starts over again, ultimately producing an infinite loop.
Only a kill signal ([Ctrl]+[C]) or an error (in case the -ignoreErrors flag isn't set - see below) will cause the application to stop gracefully
by stopping the execution after the current statement has been finished and closing the connection.

SimpleLoadGenerator will execute whatever is in the text file. This means that it is also very flexible and not only constrained to SELECT statements.
If the text file contains INSERT/UPDATE/DELETE statements the application WILL NOT execute a commit automatically.
If you want your load to be committed, you will have to put "COMMIT;" at the appropriate place within the file!
The application will also not execute a rollback at the end of each test cycle which could cause the UNDO tablespace on an Oracle database to grow significantly.
It is believed that if WRITE loads are simulated, the user also wants to commit them, e.g. batch load simulation

SimpleLoadGenerator detects SELECT statements and automatically fetches all rows after one has been executed. This is also done to produce a more realistic load.

3. Build

An Ant build file is provided in the "build" directory. To build the program you have to
invoke the default target "build". Also you have to have the Oracle JDBC driver (11.2)
and MySql Connector/J (5.1) libraries within the classpath.

4. Execute a load run

4.0 Prerequisites

SimpleLoadGenerator requires either the Oracle 11g JDBC driver in the "ojdbc6.jar" library or the MySql Connector/J in the "mysql-connector-java-5.1.NN-bin.jar" library
depending whether the load is executed against an Oracle or MySql database.
The Oracle JDBC driver can be found in $ORACLE_HOME/jdbc/lib
The MySql Connector/J can be downloaded for free at http://www.mysql.com/downloads/connector/j/  

4.2. Set correct Java version

SimpleLoadGenerator runs with Java 1.7+ and is successfully tested with Update 6.

  export JAVA_HOME=/usr/jdk1.7.0_06
  export PATH=$JAVA_HOME/bin:$PATH
  java -version
    java version "1.7.0_06"
    Java(TM) SE Runtime Environment (build 1.7.0_06-b22)
    Java HotSpot(TM) 64-Bit Server VM (build 23.0-b21, mixed mode)

4.3 JVM parameters

No special JVM parameters are needed. However, depending on how big the SQL file is, the default max heap size could be insufficient.
This can be controlled by the -Xmx JVM parameter:

  -Xmx256m  --> Maximum heap memory size of 256MB
  -Xmx512m  --> Maximum heap memory size of 512MB
  -Xmx1024m --> Maximum heap memory size of 1024MB

Example:

  java -Xmx512m -jar SimpleLoadGenerator.jar

4.4. Running SimpleLoadGenerator

The program comes with a default run script (run.sh for Unix, run.bat for Windows)

  ./run.sh

SimpleLoadGenerator provides two ways how to specify parameters. This can be done by either a properties file (SimpleLoadGenerator.properties) or via command line.
The command line can get quite long with the database connection details and usually you only specify them once and then you just adjust the SQL statements in the file or the amount of database sessions.
Therefore it's a good practice to use the properties file rather than the command line parameters.

SimpleLoadGenerator will ignore any command line parameters if a properties file is found in the working directory!

Following parameter needs to be set:

-user [username]: The username of the database user
-password [password]: The password of the user
-host [hostname]: Database machine host name or IP address
-port [port]: Listener port of the database listener
-sid [sid]: Database SID/name
-databaseType [oracle|mysql]: Specify whether the load is against an Oracle or MySql database
-sqlfile [path to SQL statements file]: The absolute or relative path to the SQL statements file to load
-sessions [amount of sessions]: The amount of sessions that should execute the SQLs against the database (default: 1)
-ignoreErrors: Specifies whether the application should ignore failing SQL statements and continue with the load
-debug: Enables debugging output, most useful to see which SQL statements got executed when
-help: Shows the online help only

Example: java -jar SimpleLoadGenerator.jar -user scott -password tiger -host localhost -port 1521 -sid MYDB -databaseType oracle -ignoreErrors -sqlfile ./SQLs.txt
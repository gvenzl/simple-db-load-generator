# SimpleDBLoadGenerator - Changelog - (c) Gerald Venzl

## v3.0.0

* Provide default values for command line parameters
* Support comments (`--`) in SQL file
* Remove Oracle NoSQL Database support
* Migration to Java 17

## v2.0.2

### Changes

* Differentiate between SQL and DML operations when printing execution log.

### Bugfixes

* Don't throw NullPointerException on [Ctrl]+[C]

## v2.0.0

### Changes

* Migration to GitHub
* Migration to Java 8

## v1.1.0

### Changes

* Include ChangeLog
* Capability of parsing MySql general log files
* Capability of parsing Multi-Line text files
* Removed parse error count - Previously the program stopped after 1000 parse errors in order to prevent useless parallel invalid SQL execution. This changed for TextFile Multi line support where a line in between a SQL command can be anything.It is assumed that:  
  * MySql general log format won't change
  * The user verified the SQLs before he added them into the plain text files!

### Bugfixes

* MySql general log file: Replace "`" character in SQL strings - MySql masks all columns with "`" in the output file but those don't parse as prepared statements and have to be trimmed. 
* Corrected online help; session parameter was missing
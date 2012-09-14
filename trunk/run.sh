#!/bin/bash

JAVA_HOME=/usr/java/latest
export JAVA_HOME

PATH=$JAVA_HOME/bin:$PATH
export PATH

java -jar ./SimpleLoadGenerator.jar

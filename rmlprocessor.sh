#!/bin/bash
#This runs the RML processor
mvn exec:java -Dexec.args="$1 $2"

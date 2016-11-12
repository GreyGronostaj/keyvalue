#!/usr/bin/env bash

outputDir=output
timestamp=$(date +"%s_%N")
scriptName=$(basename "$0")

stdoutFile=$outputDir/${scriptName}_out_$timestamp.log
stderrFile=$outputDir/${scriptName}_err_$timestamp.log

mkdir $outputDir > /dev/null 2>&1
>&2 echo Running server with params $* [timestamp $timestamp]
java -jar jars/server.jar $* > $stdoutFile 2> $stderrFile &
echo $!

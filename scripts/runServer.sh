#!/usr/bin/env bash

>&2 echo Running server with params: \"$*\".
java -jar jars/server.jar $* > /dev/null 2>&1 &
echo $!

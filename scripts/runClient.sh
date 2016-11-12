#!/usr/bin/env bash

>&2 echo Running client with params: \"$*\".
java -jar jars/client.jar $*

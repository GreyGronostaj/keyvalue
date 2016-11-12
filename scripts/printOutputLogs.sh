#!/usr/bin/env bash

echo Output logs:

for logFile in $(ls output); do
    if [ "$TRAVIS" == true ]; then
        echo -n 'travis_fold:start:' $logFile '\r'
    fi

    echo $logFile
    echo ====================
    cat output/$logFile
    echo

    if [ "$TRAVIS" == true ]; then
        echo -n 'travis_fold:end:' $logFile '\r'
    fi
done

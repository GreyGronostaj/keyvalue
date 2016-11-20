#!/usr/bin/env bash

rm -r logs

PIDs=()

for i in {0..9}
do
    PIDs+=($(./scripts/runServer.sh 127.0.0.1:1000$i))
    sleep 2
done

fails=0

for i in {0..9}
do
    eval "./scripts/runClient.sh 127.0.0.1:1000$i put n $i &"
    PIDs+=($!)
done

sleep 10

fails=0

serversWithDifferentValue=-1

expectedValue=$i

lastReceivedValue=none

for i in {0..9}
do
    receivedValue=$(./scripts/runClient.sh 127.0.0.1:1000$i get n)
    if [ $receivedValue != $expectedValue ]
    then
        echo Expected: $expectedValue, received: $receivedValue.
        ((fails++))

        if [ $receivedValue != $lastReceivedValue ]
        then
            echo Received: $receivedValue, last time received: $lastReceivedValue.
            ((serversWithDifferentValue++))
            lastReceivedValue=$receivedValue
        fi
    fi
done

kill ${PIDs[@]}

if [ $fails == 0 ]
then
    echo "Test passed."
    exit 0
elif [ $serversWithDifferentValue == 0 ]
then
    echo "Test passed, but value is different from the one that should be set as last one. Expected: $expectedValue, received: $receivedValue."
    exit 0
fi

echo "Test failed."
echo $fails clients failed to receive expected answer.
exit 1

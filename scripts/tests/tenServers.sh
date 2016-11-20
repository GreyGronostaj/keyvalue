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
    for j in {0..9}
    do
        ./scripts/runClient.sh 127.0.0.1:1000$j put $i$j $i$j

        sleep 2

        ((n=9-j))
        ReceivedValue=$(./scripts/runClient.sh 127.0.0.1:1000$n get $i$j)

        ExpectedValue=$i$j

        if [ $ReceivedValue != $ExpectedValue ]
        then
            echo Expected: $ExpectedValue, received: $ReceivedValue.
            ((fails++))
            echo Failed.
        fi
    done
done

kill ${servers[@]}

rm serversPIDs

if [ $fails == 0 ]
then
    echo "Test passed."
    exit 0
fi

echo "Test failed."
echo $fails clients failed to receive correct answer.
exit 1

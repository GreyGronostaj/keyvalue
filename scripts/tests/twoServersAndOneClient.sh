#!/usr/bin/env bash

rm -r logs

FirstServerPID=$(./scripts/runServer.sh 127.0.0.1:10000)
echo PID: $FirstServerPID.

SecondServerPID=$(./scripts/runServer.sh 127.0.0.1:10001 127.0.0.1:10000)
echo PID: $SecondServerPID.

sleep 4

./scripts/runClient.sh 127.0.0.1:10000 put A 1

ExpectedValue='1'

sleep 1

ReceivedValue=$(./scripts/runClient.sh 127.0.0.1:10001 get A)

kill $FirstServerPID $SecondServerPID

echo Expected: $ExpectedValue, received: $ReceivedValue.

if [ $ReceivedValue == $ExpectedValue ]
then
    echo "Test passed."
    exit 0
fi

echo "Test failed."
exit 1

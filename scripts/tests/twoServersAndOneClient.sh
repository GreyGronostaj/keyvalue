#!/usr/bin/env bash

rm -r logs

FirstServerPID=$(./scripts/runServer.sh :10000)
echo PID: $FirstServerPID.

SecondServerPID=$(./scripts/runServer.sh :10001 :10000)
echo PID: $SecondServerPID.

./scripts/runClient.sh :10000 put A 1

ExpectedValue='1'

ReceivedValue=$(./scripts/runClient.sh :10001 get A)

kill $FirstServerPID $SecondServerPID

echo Expected: $ExpectedValue, received: $ReceivedValue.

if [ $ReceivedValue == $ExpectedValue ]
then
    echo "Test passed."
    exit 0
fi

echo "Test failed."
exit 1

#!/bin/bash

mkdir ~/processor/work
mkdir ~/processor/cache
chmod +x ~/processor/deploy/app/cpp/timeout/timeout

java jar elasticmq-server-0.8.3.jar &

# create local sqs queues
java -cp ~/app/lib/jars/* com.topcoder.arena.util.sqs.LocalSqsSetup 'http://localhost:9324' dev

# localhost:9324 = elasticmq (local sqs)
export JAVA_OPTS="-Darena.sqs-endpoint=http://localhost:9324 -Darena.env-prefix=dev -D"

cd ~/app/scripts

# start jboss
echo Starting JBoss...
./jboss.sh

echo Waiting for startup...
sleep 120

echo Starting main listener...
./runMainListener.sh

echo Waiting for listener to initialize...
sleep 240

echo Starting admin listener...
./runAdminListener.sh

echo Starting mpsqas listener...
./mpsqasListener.sh start 5016

echo Starting web socket listener...
./runWebSocketListener.sh

echo Startup complete
exit


#!/bin/bash

if [ -e ~/boot-data/boot-env ]
then
  echo "found boot-env"
  . ~/boot-data/boot-env
  export JAVA_OPTS
  export JBOSS_HOME
  export EC2_INSTANCE_ID
else
  echo "no boot-env found"
fi

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
./mpsqasListener.sh start 5037

echo Starting web socket listener...
./runWebSocketListener.sh

echo Startup complete
exit

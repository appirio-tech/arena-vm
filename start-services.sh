#!/bin/bash

source ~/env/env.sh
cp ~/env/security.keystore.cloud ~/app/scripts/
cp ~/env/TC.cloud.ldap.keystore ~/app/scripts/

echo Creating SQS queues...
sleep 2
java -cp "/home/apps/app/lib/jars/*" -Darena.sqs-endpoint="$SQS_URL" com.topcoder.arena.util.sqs.LocalSqsSetup "$SQS_URL" devArenaCode- compile admin-test listener-results proc-results mm-test practice srm-test

# also set dummy aws credentials
SQS_AWS_OPTS="-Darena.sqs-endpoint=$SQS_URL -Darena.env-prefix=dev -Daws.accessKeyId=x -Daws.secretKey=x"
export JAVA_OPTS="$SQS_AWS_OPTS -DconfigurationProvider.class=com.topcoder.farm.controller.configuration.XMLConfigurationProvider -Dconfiguration.xml.url=file:///home/apps/app/controller/config.xml"
export JBOSS_JAVA_OPTS="$JBOSS_JAVA_OPTS -Djavax.net.ssl.trustStore=TC.cloud.ldap.keystore"

export JAVA_VER=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)

if [ "$JAVA_VER" -ge 55 ] ; then
  echo "Detected JDK >= 11, setup corba jars"
  export CP=$CP:~/app/lib/corba/*
fi

cd ~/app/scripts

# start jboss
echo Starting JBoss...
./jboss.sh

echo Waiting for startup...
sleep 30

echo Starting main listener...
./runMainListener.sh

echo Waiting for listener to initialize...
sleep 30

echo Starting admin listener...
./runAdminListener.sh

echo Starting mpsqas listener...
./mpsqasListener.sh start 5037

echo Starting web socket listener...
./runWebSocketListener.sh

echo Starting Farm Processor...
rm -rf ~/processor/work
mkdir ~/processor/work
rm -rf ~/processor/cache
mkdir ~/processor/cache
chmod +x ~/processor/deploy/app/cpp/timeout/timeout

export JAVA_OPTS="-DcppArguments=-std=c++17 $SQS_AWS_OPTS"

cd ~/processor/deploy/bin
./processor.sh PR-LX

echo Startup complete

sleep infinity


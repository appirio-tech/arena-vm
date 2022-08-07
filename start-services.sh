#!/bin/bash

source ~/env/arena-env.sh

export JAVA_VER=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)

if [ "$JAVA_VER" -ge 55 ] ; then
  echo "Detected JDK >= 11, setup corba jars"
  export CP=$CP:~/app/lib/corba/*
fi

CMD=usage
if [[ $1 != "" ]] ; then
	CMD=$1
	shift
fi

if [ "$CMD" = "app" ] ; then
	export JAVA_OPTS="$SQS_AWS_OPTS -DconfigurationProvider.class=com.topcoder.farm.controller.configuration.XMLConfigurationProvider -Dconfiguration.xml.url=file:///home/apps/app/controller/config.xml"
	export JBOSS_JAVA_OPTS="$JBOSS_JAVA_OPTS -Djavax.net.ssl.trustStore=TC.cloud.ldap.keystore"

	cd ~/app/scripts

	echo Starting JBoss...
	./jboss.sh

	echo Waiting $JBOSS_WAIT_TIME seconds for JBoss startup...
	sleep $JBOSS_WAIT_TIME

	echo Starting Main Listener...
	./runMainListener.sh

	echo Waiting $MAIN_LISTENRER_WAIT_TIME seconds for Main Listener startup...
	sleep $MAIN_LISTENRER_WAIT_TIME

	echo Starting Admin Listener...
	./runAdminListener.sh

	echo Waiting for Admin Listener startup...
	sleep 5

	echo Starting MPSQAS Listener...
	./mpsqasListener.sh start 5037

	echo Waiting for MPSQAS Listener startup...
	sleep 5

	echo Starting WebSocket Listener...
	./runWebSocketListener.sh

	echo Waiting for WebSocket Listener startup...
	sleep 5

	echo Arena app startup complete
elif [ "$CMD" = "processor" ] ; then
    echo Starting Farm Processor...
	rm -rf ~/processor/work
	mkdir ~/processor/work
	rm -rf ~/processor/cache
	mkdir ~/processor/cache
	chmod +x ~/processor/deploy/app/cpp/timeout/timeout

	export JAVA_OPTS="-DcppArguments=-std=c++17 $SQS_AWS_OPTS $PROCESSOR_OPTS"

	cd ~/processor/deploy/bin
	./processor.sh PR-LX

	echo Waiting for Farm Processor startup...
	sleep 5

	echo Arena processor startup complete
else
    echo "Usage:"
    echo "start-services.sh (app|processor)"
    echo "	app       - start JBoss and Listeners in the background"
    echo "	processor - start Farm Processor in the background"
fi

sleep infinity


#!/bin/bash

export JAVA_VER=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)

if [ "$JAVA_VER" -ge 55 ] ; then
  echo "Detected JDK >= 11, setup corba jars"
  export CP=$CP:~/app/lib/corba/*
fi

CMD=usage
if [ -z $APPLICATIONTYPE ];
then
	if [[ $1 != "" ]] ; then
		CMD=$1
		shift
	fi
else
	CMD=$APPLICATIONTYPE
fi

if [ "$CMD" = "app" ] ; then
	cd ~/app/scripts

	echo Starting JBoss...
	./jboss.sh

	if [[ -z "${JBOSS_STARTUP_WAIT_TIME}" ]] ; then
		JBOSS_STARTUP_WAIT_TIME=120
	fi
	echo Waiting $JBOSS_STARTUP_WAIT_TIME seconds for JBoss startup...
	sleep $JBOSS_STARTUP_WAIT_TIME

	echo Starting Main Listener...
	./runMainListener.sh

	if [[ -z "${MAIN_LISTENRER_STARTUP_WAIT_TIME}" ]] ; then
		MAIN_LISTENRER_STARTUP_WAIT_TIME=120
	fi
	echo Waiting $MAIN_LISTENRER_STARTUP_WAIT_TIME seconds for Main Listener startup...
	sleep $MAIN_LISTENRER_STARTUP_WAIT_TIME

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

	echo "Arena JBoss & Listeners startup complete"
elif [ "$CMD" = "controller" ] ; then
    echo Starting Farm Controller...

	cd ~/controller/deploy
	./controller.sh

	echo Waiting for Farm Controller startup...
	sleep 5

	echo Arena Farm Controller startup complete
elif [ "$CMD" = "processor" ] ; then
    echo Starting Farm Processor...
	rm -rf ~/processor/work
	mkdir ~/processor/work
	rm -rf ~/processor/cache
	mkdir ~/processor/cache

	cd ~/processor/deploy
	./processor.sh

	echo Waiting for Farm Processor startup...
	sleep 5

	echo Arena Farm Processor startup complete
else
    echo "Usage:"
    echo "start-services.sh (app|processor)"
    echo "	app       - start JBoss and Listeners in the background"
    echo "	processor - start Farm Processor in the background"
fi

sleep infinity

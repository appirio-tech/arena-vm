#!/bin/bash
JAVACMD=java
BASE=..
MAIN=com.topcoder.server.listener.ListenerMain
PORT=5001
PROCESSOR=DefaultProcessor
CMD=usage
LOGFILE=server-`date +%Y-%m-%d-%H-%M-%S`.log
LIBS=$BASE/lib/jars
CP=$CP:$BASE/resources
CP=$CP:$LIBS/@filename.classes@
CP=$CP:$LIBS/@filename.arena-shared@
CP=$CP:$LIBS/@filename.client-common@
CP=$CP:$LIBS/@filename.client-socket@
CP=$CP:$LIBS/@filename.compeng-common@
CP=$CP:$LIBS/@filename.shared@
CP=$CP:$LIBS/@filename.concurrent@
CP=$CP:$LIBS/@filename.custom-serialization@
CP=$CP:$LIBS/@filename.encoder@
CP=$CP:$LIBS/@filename.farm-client@
CP=$CP:$LIBS/@filename.farm-deployer@
CP=$CP:$LIBS/@filename.farm-shared@
CP=$CP:$LIBS/@filename.http-tunnel-client@
CP=$CP:$LIBS/@filename.http-tunnel-server@
CP=$CP:$LIBS/@filename.logging@
CP=$CP:$LIBS/@filename.nbio-listener@
CP=$CP:$LIBS/@filename.security@
CP=$CP:$LIBS/@filename.log4j@
CP=$CP:$LIBS/@filename.commons-digester@
CP=$CP:$LIBS/@filename.activemq-core@
CP=$CP:$LIBS/@filename.activeio-core@
CP=$CP:$LIBS/@filename.backport-util-concurrent@
CP=$CP:$LIBS/@filename.jms@
CP=$CP:$LIBS/@filename.xpp3_min@
CP=$CP:$LIBS/@filename.commons-beanutils@
CP=$CP:$LIBS/@filename.commons-collections@
CP=$CP:$LIBS/@filename.commons-logging@
CP=$CP:$LIBS/@filename.jbossall-client@
CP=$CP:$LIBS/@filename.base_exception@
CP=$CP:$LIBS/@filename.basic_type_serialization@
CP=$CP:$CLASSPATH

if [[ $1 != "" ]] ; then
	CMD=$1
	shift
fi

if [[ $1 != "" ]] ; then
	PORT=$1
	PID_FILE=listener$PORT.pid
	shift
fi

if [[ $1 != "" ]] ; then
	PROCESSOR=$1
	shift
fi

if [[ -z "${MAIN_LISTENER_JAVA_OPTS}" ]] ; then
	MAIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

LOGGING_ID=ContestListener.$PORT
LOGGING_PROPERTY=com.topcoder.logging.id
if [ "$CMD" = "run" ] ; then
	echo "MAIN_LISTENER_JAVA_OPTS=$MAIN_LISTENER_JAVA_OPTS"
    $JAVACMD -verbose:gc -cp $CP $MAIN_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT
    #$PROCESSOR $@
elif [ "$CMD" = "start" ] ; then
    echo "nohup $JAVACMD -Dcontestconstants.ACCEPT_MULTIPLE_SUBMISSIONS=true -DVM_INSTANCE_ID=Listener -cp $CP $MAIN_LISTENER_JAVA_OPTS -Dclientsocket.buffersize.25000=524288 -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $PROCESSOR $@ >$LOGFILE 2>&1 &"
    nohup $JAVACMD -Dcontestconstants.ACCEPT_MULTIPLE_SUBMISSIONS=true -DVM_INSTANCE_ID=Listener -cp $CP $MAIN_LISTENER_JAVA_OPTS -Dclientsocket.buffersize.25000=524288 -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $PROCESSOR $@ >$LOGFILE 2>&1 &
    echo $! > $PID_FILE
	echo "start, port=$PORT, processor=$PROCESSOR, MAIN_LISTENER_JAVA_OPTS=$MAIN_LISTENER_JAVA_OPTS"
elif [ "$CMD" = "stop" ] ; then
    kill `cat $PID_FILE`
    rm -f $PID_FILE
	echo "stop, port=$PORT"
elif [ "$CMD" = "kill" ] ; then
    kill -3 `cat $PID_FILE`
    rm -f $PID_FILE
	echo "kill, port=$PORT"
else
    echo "Usage:"
    echo "listener.sh (run|start|stop) <port> <processor> <monitor_port>"
    echo "	run   - start listener in the foreground"
    echo "	start - start listener in the background"
    echo "	stop  - stop listener"
    echo "	kill  - kill listener with 'kill -3'"
fi

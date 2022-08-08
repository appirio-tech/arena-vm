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
CP=$CP:$LIBS/*
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

LOGGING_ID=ContestListener.$PORT
LOGGING_PROPERTY=com.topcoder.logging.id
if [ "$CMD" = "run" ] ; then
	echo "MAIN_LISTENER_JAVA_OPTS=$MAIN_LISTENER_JAVA_OPTS"
    $JAVACMD -verbose:gc -cp $CP $MAIN_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT
    #$PROCESSOR $@
elif [ "$CMD" = "start" ] ; then
    echo "nohup $JAVACMD $JAVA_OPTS -Dcontestconstants.ACCEPT_MULTIPLE_SUBMISSIONS=true -DVM_INSTANCE_ID=Listener -cp $CP $MAIN_LISTENER_JAVA_OPTS -Dclientsocket.buffersize.25000=524288 -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $PROCESSOR $@ >$LOGFILE 2>&1 &"
    nohup $JAVACMD $JAVA_OPTS -Dcontestconstants.ACCEPT_MULTIPLE_SUBMISSIONS=true -DVM_INSTANCE_ID=Listener -cp $CP $MAIN_LISTENER_JAVA_OPTS -Dclientsocket.buffersize.25000=524288 -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $PROCESSOR $@ >$LOGFILE 2>&1 &
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

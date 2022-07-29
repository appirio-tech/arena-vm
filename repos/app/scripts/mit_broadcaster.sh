#!/bin/sh

JAVACMD=$JAVA_HOME/bin/java
MAIN=com.topcoder.server.broadcaster.MITBroadcaster
BASE=..
LOGFILE=mit_broadcaster-`date +%Y-%m-%d-%H-%M-%S`.log
CMD=usage
PORT=5010

CP=$BASE/build/classes:$BASE/resources:$CLASSPATH

if [[ $1 != "" ]] ; then
	CMD=$1
	shift
fi

if [[ $1 != "" ]] ; then
	PORT=$1
	PID_FILE=mit_broadcaster$PORT.pid
	shift
fi

if [ "$CMD" = "run" ] ; then
    $JAVACMD -cp $CP $MAIN $PORT
elif [ "$CMD" = "start" ] ; then
    nohup $JAVACMD -cp $CP $MAIN $PORT >$LOGFILE 2>&1 &
    echo $! > $PID_FILE
	echo "start, port=$PORT"
elif [ "$CMD" = "stop" ] ; then
    kill `cat $PID_FILE`
    rm -f $PID_FILE
	echo "stop, port=$PORT"
else
    echo "Usage:"
    echo "mit_broadcaster.sh (run|start|stop) <port>"
    echo "	run   - start in the foreground"
    echo "	start - start in the background"
fi

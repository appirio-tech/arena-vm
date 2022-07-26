#!/bin/bash


# argument 1 - start | stop | kill, depending on what you want to do.
# argument 2 - the port on which to start the admin listener.
# argument 3 - (optional) the ip and port at which the contest server is listening for admin connections,
#   in the format ip:port.  If unspecified, no contest server connection will be made.
#   This probably is not what you want.
# argument 4 - (optional) the port used by the logging server.  If unspecified, no logging server will be
#   instantiated.
JAVACMD=java
BASE=..
MAIN=com.topcoder.server.AdminListener.AdminListenerMain
PORT=6000
CMD=usage
MAXMEM=1024m
LOGFILE=adminServer-`date +%Y-%m-%d-%H-%M-%S`.log

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
	PID_FILE=adminListener$PORT.pid
	shift
fi

if [[ $1 != "" ]] ; then
	CONTEST=$1
	shift
fi

CUSTOM_SECURITY=""
JAVA_VER=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)
if [ "$JAVA_VER" -ge 52 ] ; then
  echo "Use custom.security for JDK >= 1.8"
  CUSTOM_SECURITY="-Djava.security.properties=custom.security"
fi

LOGGING_ID=AdminListener.$PORT
LOGGING_PROPERTY=com.topcoder.logging.id

if [ "$CMD" = "run" ] ; then
    $JAVACMD -cp $CP -Xmx$MAXMEM $CUSTOM_SECURITY -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $CONTEST $@
elif [ "$CMD" = "start" ] ; then
    nohup $JAVACMD -cp $CP -Xmx$MAXMEM $CUSTOM_SECURITY -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $CONTEST $@ >$LOGFILE 2>&1 &
    echo $! > $PID_FILE
	echo "start, port=$PORT, contest=$CONTEST, maxmem=$MAXMEM"
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
    echo "adminListener.sh (run|start|stop|kill) [port] [contest listener IP:port] [logging port]"
    echo "	run   - start listener in the foreground"
    echo "	start - start listener in the background"
    echo "	stop  - stop listener"
    echo "	kill  - kill listener with 'kill -3'"
fi

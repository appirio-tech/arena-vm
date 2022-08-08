#!/bin/bash

ENDORSED_DIR=-Djava.endorsed.dirs=${JBOSS_HOME}/lib/endorsed

if [ "$JAVA_VER" -ge 55 ] ; then
  # JDK 11 does not support endorsed dir
  echo "Detected JDK >= 11, don't use endorsed dir"
  ENDORSED_DIR=""
fi

echo "nohup java ${JAVA_OPTS} ${JBOSS_JAVA_OPTS} $ENDORSED_DIR -classpath $JBOSS_HOME/bin/run.jar:$JBOSS_HOME/lib/tools.jar:$CP org.jboss.Main &"
nohup java ${JAVA_OPTS} ${JBOSS_JAVA_OPTS} $ENDORSED_DIR -classpath $JBOSS_HOME/bin/run.jar:$JBOSS_HOME/lib/tools.jar:$CP org.jboss.Main &

exit

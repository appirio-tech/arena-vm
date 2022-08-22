#!/bin/bash

ENDORSED_DIR=-Djava.endorsed.dirs=${JBOSS_HOME}/lib/endorsed

if [ "$JAVA_VER" -ge 55 ] ; then
  # JDK 11 does not support endorsed dir
  echo "Detected JDK >= 11, don't use endorsed dir"
  ENDORSED_DIR=""
fi

CUSTOM_SECURITY=""
if [ "$JAVA_VER" -ge 52 ] ; then
  echo "Use custom.security for JDK >= 1.8"
  CUSTOM_SECURITY="-Djava.security.properties=custom.security"
fi

if [[ -z "${JBOSS_JAVA_OPTS}" ]] ; then
	JBOSS_JAVA_OPTS="-Xms2048m -Xmx8192m"
fi
JBOSS_JAVA_OPTS="$JBOSS_JAVA_OPTS -Djavax.net.ssl.trustStore=TC.cloud.ldap.keystore"

echo "nohup java ${JBOSS_JAVA_OPTS} $ENDORSED_DIR -classpath $JBOSS_HOME/bin/run.jar:$CP org.jboss.Main &"
nohup java ${JBOSS_JAVA_OPTS} $CUSTOM_SECURITY $ENDORSED_DIR -classpath $JBOSS_HOME/bin/run.jar:$CP org.jboss.Main &

exit

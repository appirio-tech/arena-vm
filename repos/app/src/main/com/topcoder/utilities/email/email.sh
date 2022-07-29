#!/bin/ksh
JAVA=/usr/java/bin/java
LOGFILE=email.log
#echo > ${LOGFILE}
${JAVA} -Xms32m -Xmx32m -Dweblogic.system.executeThreadCount=10 -Dweblogic.system.percentSocketReaders=50 com.topcoder.email.Sender >> ${LOGFILE} 2>&1
echo "The jms emailer gateway has finished executing and is starting up again..." >> ${LOGFILE}

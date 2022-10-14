CONTROLLER_NODE_ID=CT-MAIN

if [[ -z "${CONTROLLER_JAVA_OPTS}" ]] ; then
	CONTROLLER_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi
echo "CONTROLLER_JAVA_OPTS=$CONTROLLER_JAVA_OPTS"

nohup java -cp ./conf:./lib/*:$CP $CONTROLLER_JAVA_OPTS \
  -Dcom.topcoder.farm.type=controller \
  -Dcom.topcoder.farm.id=$CONTROLLER_NODE_ID \
  -Dcom.topcoder.commandline.io=socket \
  -Dcom.topcoder.commandline.io.port=15968 \
  -Dserversocket.buffersize.25000=1048576 \
  -Dserversocket.buffersize.25001=1048576 \
  -Dconfiguration.xml.url=file:///home/apps/controller/deploy/conf/config.xml \
  -DconfigurationProvider.class=com.topcoder.farm.controller.configuration.XMLConfigurationProvider \
  -Dcom.topcoder.farm.controller.queue.comparator_class=com.topcoder.farm.controller.queue.DefaultQueueComparator \
  com.topcoder.farm.controller.ControllerMain $CONTROLLER_NODE_ID &

echo "$!" > controller.pid

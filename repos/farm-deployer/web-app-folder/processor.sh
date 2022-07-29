NODE_ID=PR1-1
java -cp jars/farm-server-1.0.jar:jars/farm-deployment-processor-{$NODE_ID}-1.0.jar:jars/classes-1.0.jar:jars/farm-shared-1.0.jar:jars/farm-deployer-1.0.jar:jars/commons-io-1.2.jar:jars/commons-logging-1.1.jar:jars/dom4j-1.6.1.jar:jars/log4j-1.2.13.jar:jars/xerces-2.6.2.jar:jars/xpp3-1.1.3.4d_b4_min.jar:jars/xstream-1.1.3.jar -Dcom.topcoder.farm.processor.queue.comparator_class=com.topcoder.farm.processor.queue.DefaultQueueComparator -Dcom.topcoder.commandline.io.port=15967 -Dcom.topcoder.commandline.io=auto -DconfigurationProvider.class=com.topcoder.farm.processor.configuration.XMLConfigurationProvider -Dconfiguration.xml.url=http://63.118.154.180:8085/farm-deployer/config?type=processor\&id=$NODE_ID -Dcom.topcoder.farm.type=processor -Dcom.topcoder.farm.id=$NODE_ID com.topcoder.farm.processor.ProcessorMain $NODE_ID




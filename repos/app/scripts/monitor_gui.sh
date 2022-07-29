#!/bin/sh
source /etc/profile
CP=../resources:../lib/jars/@filename.security@:../build/classes:../lib/jars/@filename.log4j@:../lib/jars/@filename.ifxjdbc@:../lib/jars/@filename.lightweight_xml_parser@:$CLASSPATH
java -cp $CP com.topcoder.client.contestMonitor.view.gui.MonitorGUIMain $@


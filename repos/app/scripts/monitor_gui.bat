set CP=../resources;../build/classes;../lib/jars/@filename.log4j@;../lib/jars/@filename.security@;../lib/jars/@filename.ifxjdbc@;../lib/jars/@filename.lightweight_xml_parser@
%JAVA_HOME%/bin/java -Xmx128M -Xms128M -Dsun.java2d.noddraw=true -cp %CP% com.topcoder.client.contestMonitor.view.gui.MonitorGUIMain %1 %2 %3 %4 %5 %6 %7 %8 %9

set JAVACMD=c:/j2sdk1.4.0/bin/java
set MAIN=com.topcoder.utilities.GetColumns
set BASE=..
set CP=%BASE%/build/classes;%BASE%/resources;%BASE%/lib/jars/@filename.ifxjdbc@;%BASE%/lib/jars/@filename.log4j@

%JAVACMD% -classpath %CP% %MAIN% %1

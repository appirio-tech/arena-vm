set JAVACMD=%JAVA_HOME%/bin/java
set MAIN=com.topcoder.server.listener.ListenerMain
set BASE=..
set JARS=%BASE%/lib/jars
set JBOSS_JARS=%JBOSS_HOME%/client
set CP=%BASE%/resources;%BASE%/build/classes
set CP=%CP%;%JARS%/@filename.classes@
set CP=%CP%;%JARS%/@filename.arena-shared@
set CP=%CP%;%JARS%/@filename.client-common@
set CP=%CP%;%JARS%/@filename.client-socket@
set CP=%CP%;%JARS%/@filename.compeng-common@
set CP=%CP%;%JARS%/@filename.shared@
set CP=%CP%;%JARS%/@filename.concurrent@
set CP=%CP%;%JARS%/@filename.custom-serialization@
set CP=%CP%;%JARS%/@filename.encoder@
set CP=%CP%;%JARS%/@filename.farm-client@
set CP=%CP%;%JARS%/@filename.farm-deployer@
set CP=%CP%;%JARS%/@filename.farm-shared@
set CP=%CP%;%JARS%/@filename.http-tunnel-client@
set CP=%CP%;%JARS%/@filename.http-tunnel-server@
set CP=%CP%;%JARS%/@filename.logging@
set CP=%CP%;%JARS%/@filename.nbio-listener@
set CP=%CP%;%JARS%/@filename.security@
set CP=%CP%;%JARS%/@filename.shared@
set CP=%CP%;%JARS%/@filename.log4j@
set CP=%CP%;%JARS%/@filename.commons-digester@
set CP=%CP%;%JARS%/@filename.activemq-core@
set CP=%CP%;%JARS%/@filename.activeio-core@
set CP=%CP%;%JARS%/@filename.backport-util-concurrent@
set CP=%CP%;%JARS%/@filename.jms@
set CP=%CP%;%JARS%/@filename.xpp3_min@
set CP=%CP%;%JARS%/@filename.commons-lang3@
set CP=%CP%;%JARS%/@filename.commons-beanutils@
set CP=%CP%;%JARS%/@filename.commons-collections@
set CP=%CP%;%JARS%/@filename.commons-logging@
set CP=%CP%;%JARS%/@filename.jbossall-client@
set CP=%CP%;%JARS%/@filename.base_exception@
set CP=%CP%;%JARS%/@filename.basic_type_serialization@

%JAVACMD% -cp %CP% -Dcom.topcoder.logging.id=ContestListener.5003 -DisListenerServer=true %MAIN% 5003 d 5005 d 5004

# Opts used to connect AWS SQS.
export SQS_AWS_OPTS="-Darena.sqs-endpoint=https://sqs.us-east-1.amazonaws.com -Darena.env-prefix=dev -Daws.accessKeyId=<aws-access-key-id> -Daws.secretKey=<aws-secret-key>"
# In prod env, you need use -Darena.env-prefix=prod, e.g:
# export SQS_AWS_OPTS="-Darena.sqs-endpoint=https://sqs.us-east-1.amazonaws.com -Darena.env-prefix=prod -Daws.accessKeyId=<aws-access-key-id> -Daws.secretKey=<aws-secret-key>"

# Opts to config JBoss java process.
export JBOSS_JAVA_OPTS="-Xms1024m -Xmx2048m"
# In prod env, you may want to increase the JVM memory, e.g:
# export JBOSS_JAVA_OPTS="-Xms4096m -Xmx6144m"

# Opts to config Main Listener java process.
export MAIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export MAIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config Admin Listener java process.
export ADMIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export ADMIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config MPSQAS Listener java process.
export MPSQAS_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export MPSQAS_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config WebSocket Listener java process.
export WEBSOCKET_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export WEBSOCKET_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# The time to wait JBoss startup, in seconds
export JBOSS_WAIT_TIME=120

# The time to wait Main Listener startup, in seconds
export MAIN_LISTENRER_WAIT_TIME=240

# Copy security keystore files if necessary
cp ~/env/security.keystore.cloud ~/app/scripts/
cp ~/env/TC.cloud.ldap.keystore ~/app/scripts/

# You may also copy/change any other config files if necessary

# E.g, if you want to use another LDAP.properties:
# cp ~/env/LDAP.properties ~/app/resources/
# cp ~/env/LDAP.properties ~/app/jboss-4.0.5.GA/server/default/conf/

# E.g, if you want to use another com/topcoder/security/Util.properties:
# cp ~/env/Util.properties ~/app/resources/com/topcoder/security/
# cp ~/env/Util.properties ~/app/jboss-4.0.5.GA/server/default/conf/com/topcoder/security/
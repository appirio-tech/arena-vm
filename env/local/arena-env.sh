export SQS_AWS_OPTS="-Darena.sqs-endpoint=http://arena-sqs:9324 -Darena.env-prefix=dev -Daws.accessKeyId=x -Daws.secretKey=x"

export JBOSS_JAVA_OPTS="-Xms1024m -Xmx2048m"
export MAIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
export ADMIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
export MPSQAS_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
export WEBSOCKET_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"

export JBOSS_WAIT_TIME=30
export MAIN_LISTENRER_WAIT_TIME=20

cp ~/env/security.keystore.cloud ~/app/scripts/
cp ~/env/TC.cloud.ldap.keystore ~/app/scripts/
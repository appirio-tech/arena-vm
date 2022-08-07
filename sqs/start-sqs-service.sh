#!/bin/sh

echo Starting SQS service...
nohup java -Dconfig.file=/sqs/elasticmq-server.conf -jar /sqs/elasticmq-server-0.8.5.jar &

sleep 5

echo Creating SQS queues...
javac -cp "/sqs/*" LocalSqsSetup.java
java -cp ".:/sqs/*" LocalSqsSetup "http://localhost:9324" devArenaCode- practice compile srm-test mm-test compile-windows srm-test-windows mm-test-windows admin-test listener-results proc-results

echo SQS queues created

tail -f /dev/null
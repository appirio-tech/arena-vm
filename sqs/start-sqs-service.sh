#!/bin/sh

nohup java -Dconfig.file=/sqs/elasticmq-server.conf -jar /sqs/elasticmq-server-0.8.5.jar &

tail -f /dev/null
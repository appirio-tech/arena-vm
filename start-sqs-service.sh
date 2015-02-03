#!/bin/bash

java -Dconfig.file=elasticmq-server.conf -jar ~/dev/arena-vm/elasticmq-server-0.8.5.jar &

sleep 10

# create local sqs queues
java -cp "/home/apps/app/lib/jars/*" -Darena.sqs-endpoint='http://localhost:9324' com.topcoder.arena.util.sqs.LocalSqsSetup 'http://localhost:9324' devArenaCode- compile admin-test listener-results proc-results mm-test practice srm-test
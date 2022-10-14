PID=`cat farm.pid`
pkill -9 -P $PID
kill -9 $PID

PID=`cat farm.pid`
pkill -P $PID
kill $PID

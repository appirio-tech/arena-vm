#!/bin/sh
echo "$$"
exec java -Dpid=$$ $@

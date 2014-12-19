#!/bin/bash
#  Sample VM initialization script
#	
#  Works with the topcoder.generic.centOS image family 
#
#  Created 29 Nov 2010 by thx1138
#

echo script start...
cd /root/startup-tmp

#get the handle for the user's public key
HANDLE=`/root/get-vm-param.sh handle`
ROOT=`/root/get-vm-param.sh root`

#Get the Public IP of this VM
NEW_PUBLIC_IP=`/root/ec2-metadata --public-ipv4 |  awk -F ' ' '{print $2}'`


# Add member public key to all user's authorized_keys file
if [ "$HANDLE" != "" ]; then
  echo "Updating authorized_keys"
  /usr/bin/curl -g -s "http://54.164.153.88/tc?module=BasicRSS&c=rss_get_key&dsid=30&hn=${HANDLE}" |  /usr/bin/xmlstarlet sel  -t -m //item -v description >> /root/member_authorized_keys
  cat /root/member_authorized_keys >> /home/apps/.ssh/authorized_keys
  cat /root/member_authorized_keys >> /home/api/.ssh/authorized_keys
  cat /root/member_authorized_keys >> /home/tc/.ssh/authorized_keys
fi

################################################################
# Allow root access for user
################################################################
if [ "$ROOT" = "yes" ]; then
	cat /root/member_authorized_keys >> /root/.ssh/authorized_keys
fi

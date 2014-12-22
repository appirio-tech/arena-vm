#!/bin/bash

# it is assumed openldap, informix, and mysql are already installed

cd /root/arena-vm

# ldap
service slapd start

# informix
su - informix -c /opt/IBM/informix/bin/oninit

# mysql
service mysqld start

# cache
su - cache -c 'cd ~/jboss-4.0.5.GA/bin;./start.sh'

# reg2
su - tc -c 'cd ~/jboss-4.0.4.GA/bin;./start.sh'

# tc-api
# need to copy a config with CORS support
cp ./web.js /home/api/tc-api/config/servers/
su - api -c 'cd tc-api;sh start.sh'

# nginx - tc/reg2
service nginx start

# copy member key for login
./copy-member-key.sh > member-key.log 2>&1

su - apps -c 'cd /home/apps/dev/arena-vm;git pull'

su - apps -c 'cd /home/apps/dev/arena-vm;./deploy.sh' > deploy.log 2>&1

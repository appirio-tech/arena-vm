#!/bin/bash

# it is assumed openldap, informix, and mysql are already installed

cd /root

# ldap
service slapd start

# informix
su - informix -c /opt/IBM/informix/bin/oninit

# mysql
service mysql start

# copy member key for login
copy-member-key.sh > member-key.log 2>&1

su - apps -c 'cd /home/apps/dev/arena-vm;git pull'

su - apps -c '/home/apps/dev/arena-vm/deploy.sh > deploy.log >2&1'

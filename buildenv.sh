#!/bin/bash
cp -rf *.properties.$LOGICAL_ENV repos/app/
mv -f security.keystore.cloud env/
mv -f TC.cloud.ldap.keystore env/
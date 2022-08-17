#!/bin/bash
mv -f token.properties.$LOGICAL_ENV repos/app/

mv -f TC.prod.ldap.keystore repos/app/scripts/TC.cloud.ldap.keystore
mv -f LDAP.properties repos/app/resources/

mv -f security.keystore.cloud repos/app/scripts/
mv -f Util.properties repos/app/resources/com/topcoder/security/
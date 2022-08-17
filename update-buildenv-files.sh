#!/bin/bash
mv -f token.properties.$LOGICAL_ENV repos/app/

mv -f TC.cloud.ldap.keystore repos/app/scripts/
mv -f LDAP.properties repos/app/resources/

mv -f security.keystore.cloud repos/app/scripts/
mv -f Util.properties repos/app/resources/com/topcoder/security/
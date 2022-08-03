## Dev Docker Deployment

This is to deploy in docker in Topcoder dev environment.

### Prerequisites

  - Docker

### Docker compose

[arena-vm/docker-compose-dev.yml](../docker-compose-dev.yml) is used to deploy in Topcoder dev environment.

```bash
docker-compose -f docker-compose-dev.yml up -d
```



### Files need to change

Following files in [arena-vm/env/dev/](../env/dev/) folder need be changed according to dev environment:

- [arena-vm/env/dev/env.sh](../env/dev/env.sh): Change the `SQS_HOST` according to dev environment

  ```bash
  export SQS_HOST=sqs.topcoder-dev.com
  export SQS_URL=http://$SQS_HOST:9324
  ```

- [arena-vm/env/dev/security.keystore.cloud](../env/dev/security.keystore.cloud): Change to the security keystore in dev environment

- [arena-vm/env/dev/TC.cloud.ldap.keystore](../env/dev/TC.cloud.ldap.keystore): Change to the ldap keystore in dev environment



### Tokens need to change

Following tokens in  [arena-vm/repos/app/token.properties.dev](../repos/app/token.properties.dev)  need be taken care of according to dev environment:

#### Informix tokens

These are informix related tokens. Change theses according to dev environment:

```properties
# The database related configurations
@database.server@=arena-informix
@database.dwserver@=arena-informix
@database.port@=2021
@database.dwport@=2021
@database.username@=informix
@database.password@=1nf0rm1x
@database.INFORMIXSERVER@=informixoltp_tcp
@database.DWSERVER@=datawarehouse_tcp
```

#### Mysql tokens

These are mysql related tokens. Change theses according to dev environment:

```properties
@farm.mysql.port@=3306
@farm.mysql.host@=arena-mysql
@farm.mysql.username@=farm
@farm.mysql.password@=farmpass
```

#### LDAP tokens

These are LDAP related tokens. Change theses according to dev environment:

```properties
## Tokens used in LDAP.properties
@ldapHost@=arena-ldap
@ldapPort@=389
@ldapBindDn@=cn=Manager,dc=topcoder,dc=com
@ldapBindPwd@=secret
```

#### TC-API tokens

These are tc-api related tokens. Change theses according to dev environment:

```properties
# Tokens related to TC API
@tcApi.baseUrl@=https://api.topcoder-dev.com
@tcApi.srcToImg.endPoint@=/convertSourceCodeToImage
@tcApi.srcToImg.style@=idea
```

#### SSO tokens

These are SSO related tokens. Change theses according to dev environment:

```properties
@ssoCookieKey@=tcsso
@ssoHashSecret@=GKDKJF80dbdc541fe829898aa01d9e30118bab5d6b9fe94fd052a40069385f5628
@ssoDomain@=topcoder-dev.com
@jwtCookieKey@=tcjwt
```


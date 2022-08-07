## TC Docker Deployment

This is about how to deploy in docker in Topcoder environments.

### Prerequisites

  - Docker

### Build Docker Image

The docker image is built using token file [arena-vm/repos/app/token.properties.dev](../repos/app/token.properties.dev) for dev env.

Similarly, when built for prod env, token file `arena-vm/repos/app/token.properties.prod` will be used.

#### Informix tokens

These are informix related tokens. Change theses according to TC environment:

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

These are mysql related tokens. Change theses according to TC environment:

```properties
@farm.mysql.port@=3306
@farm.mysql.host@=arena-mysql
@farm.mysql.username@=farm
@farm.mysql.password@=farmpass
```

#### LDAP tokens

These are LDAP related tokens. Change theses according to TC environment:

```properties
## Tokens used in LDAP.properties
@ldapHost@=arena-ldap
@ldapPort@=389
@ldapBindDn@=cn=Manager,dc=topcoder,dc=com
@ldapBindPwd@=secret
```

#### TC-API tokens

These are tc-api related tokens. Change theses according to TC environment:

```properties
# Tokens related to TC API
@tcApi.baseUrl@=https://api.topcoder-dev.com
@tcApi.srcToImg.endPoint@=/convertSourceCodeToImage
@tcApi.srcToImg.style@=idea
```

#### SSO tokens

These are SSO related tokens. Change theses according to TC environment:

```properties
@ssoCookieKey@=tcsso
@ssoHashSecret@=GKDKJF80dbdc541fe829898aa01d9e30118bab5d6b9fe94fd052a40069385f5628
@ssoDomain@=topcoder-dev.com
@jwtCookieKey@=tcjwt
```



### Mount Volume /home/apps/env

You must mount a volume to `/home/apps/env` container directory.

This directory must contain a file `arena-env.sh` , when container starts, it will be invoked to prepare any necessary env setup.

For example, the [arena-env.sh](../env/dev/arena-env.sh) for TC dev environment:

```bash
# Opts used to connect AWS SQS.
export SQS_AWS_OPTS="-Darena.sqs-endpoint=https://sqs.us-east-1.amazonaws.com -Darena.env-prefix=dev -Daws.accessKeyId=<aws-access-key-id> -Daws.secretKey=<aws-secret-key>"
# In prod env, you need use -Darena.env-prefix=prod, e.g:
# export SQS_AWS_OPTS="-Darena.sqs-endpoint=https://sqs.us-east-1.amazonaws.com -Darena.env-prefix=prod -Daws.accessKeyId=<aws-access-key-id> -Daws.secretKey=<aws-secret-key>"

# Opts to config JBoss java process.
export JBOSS_JAVA_OPTS="-Xms1024m -Xmx2048m"
# In prod env, you may want to increase the JVM memory, e.g:
# export JBOSS_JAVA_OPTS="-Xms4096m -Xmx6144m"

# Opts to config Main Listener java process.
export MAIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export MAIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config Admin Listener java process.
export ADMIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export ADMIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config MPSQAS Listener java process.
export MPSQAS_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export MPSQAS_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# Opts to config WebSocket Listener java process.
export WEBSOCKET_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
# In prod env, you may want to increase the JVM memory, e.g:
# export WEBSOCKET_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"

# The time to wait JBoss startup, in seconds
export JBOSS_WAIT_TIME=120

# The time to wait Main Listener startup, in seconds
export MAIN_LISTENRER_WAIT_TIME=240

# Copy security keystore files if necessary
cp ~/env/security.keystore.cloud ~/app/scripts/
cp ~/env/TC.cloud.ldap.keystore ~/app/scripts/

# You may also copy/change any other config files if necessary

# E.g, if you want to use another LDAP.properties:
# cp ~/env/LDAP.properties ~/app/resources/
# cp ~/env/LDAP.properties ~/app/jboss-4.0.5.GA/server/default/conf/

# E.g, if you want to use another com/topcoder/security/Util.properties:
# cp ~/env/Util.properties ~/app/resources/com/topcoder/security/
# cp ~/env/Util.properties ~/app/jboss-4.0.5.GA/server/default/conf/com/topcoder/security/
```



By using the volume strategy, it provides flexibility to adjust the docker image without rebuild it. 

Theoretically, we only need rebuild docker image if there is some substantial change (like some java/cpp source code changes). If we just want to change some env or config files, it's possible to just use the mount volume to change them.



### Start Docker Containers

The JBoss/Listeners and Farm Processor are started separately. Since you only need one container for JBoss&Listeners, while you may need multiple Farm Processors.

- To start container for JBoss & Listeners:

  ````yaml
    arena-app-dev:
      image: "tc-arena-app:latest"
      container_name: "arena-app-dev"
      # The volume mount to setup dev env
      volumes:
        - ./env/dev:/home/apps/env
      # The command to start JBoss & Listeners
      command: ["/home/apps/start-services.sh", "app"]
      # The ports to expose to external
      # 5001: Main Listener
      # 5003: Admin Listener
      # 5016: WebSocket Listener
      # 5037: MPSQAS Listener
      ports:
        - "5001:5001"
        - "5003:5003"
        - "5016:5016"
        - "5037:5037"
  ````

- To start container for Farm Processor:

  ```yaml
    arena-processor-dev:
      image: "tc-arena-app:latest"
      container_name: "arena-processor-dev"
      # The volume mount to setup dev env
      volumes:
        - ./env/dev:/home/apps/env
      # The command to start Farm Processor
      command: ["/home/apps/start-services.sh", "processor"]
      # Notice the PROCESSOR_OPTS environment:
      #   -Darena.processor-queues defines the queues to monitor
      #   -Darena.processor-default-timeout defines the timeout for processing a message, in minutes
      environment:
        - PROCESSOR_OPTS=-Darena.processor-queues=practice,compile,srm-test,admin-test,mm-test -Darena.processor-default-timeout=5
  ```



### Start Multiple Processors

You can start multiple Farm Processors containers. Following are some suggestions for product environment.

Suggestion 1, for `practice` queue which handles compile/test of pratice room, you don't want it to affect the SRM/MM matches, then you can start a dedicated processor for it:

```yaml
  # Dedicated processor for 'practice' queue
  arena-processor-practice:
    image: "tc-arena-app:latest"
    container_name: "arena-processor-practice"
    volumes:
      - ./env/prod:/home/apps/env
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=practice -Darena.processor-default-timeout=5
```



Suggestion 2, you want to seprately process the `compile` queue from test queues, then you can start a dedicated processor for it:

```yaml
  # Dedicated processor for 'compile' queue
  arena-processor-compile:
    image: "tc-arena-app:latest"
    container_name: "arena-processor-compile"
    volumes:
      - ./env/prod:/home/apps/env
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=compile -Darena.processor-default-timeout=5
```



Suggestion 3, for `mm-test` queue which handles test of MM matches, the MM test will likely take a long time up to 15 minutes, then you can start a dedicated processor for it:

```yaml
  # Dedicated processor for 'mm-test' queue
  arena-processor-mm-test:
    image: "tc-arena-app:latest"
    container_name: "arena-processor-mm-test"
    volumes:
      - ./env/prod:/home/apps/env
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=mm-test -Darena.processor-default-timeout=15
```



Suggestion 4, you have decided the `srm-test` and `admin-test` queues can be processed together, since you found that there isn't much messages in `admin-test` queues so there is no need to waste resource to start a dedicated processor for it, then you can start a processor for both queues:

```yaml
  # Processor for 'srm-test' and 'admin-test' queues
  arena-processor-srm-admin-test:
    image: "tc-arena-app:latest"
    container_name: "arena-processor-srm-admin-test"
    volumes:
      - ./env/prod:/home/apps/env
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=srm-test,admin-test -Darena.processor-default-timeout=5
```



**Note: You can even start multiple Farm Processors for the same queue, e.g. since the MM test will likely take long time, you can start multiple Farm Processors for the same `mm-test` queue.**

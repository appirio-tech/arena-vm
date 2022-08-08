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



### Setup env

When build image, the `./env/$ARENA_BUILD_TARGET` directory is copied into docker image at path `/home/apps/env`. E.g. for dev env `./env/dev` directory is copied, for prod env `./env/prod` directory is copied.

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



### Start Docker Containers

The JBoss/Listeners and Farm Processor are started separately. Since you only need one container for JBoss/Listeners, while you may need multiple Farm Processors.

- To start container for JBoss & Listeners:

  ````yaml
    arena-app-dev:
      image: "arena-app:dev"
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
      image: "arena-app:dev"
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
    image: "arena-app:prod"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=practice -Darena.processor-default-timeout=5
```



Suggestion 2, you want to seprately process the `compile` queue from test queues, then you can start a dedicated processor for it:

```yaml
  # Dedicated processor for 'compile' queue
  arena-processor-compile:
    image: "arena-app:prod"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=compile -Darena.processor-default-timeout=5
```



Suggestion 3, you have decided the `srm-test` and `admin-test` queues can be processed together, since you found that there isn't much messages in `admin-test` queues so there is no need to waste resource to start a dedicated processor for it, then you can start a processor for both queues:

```yaml
  # Processor for 'srm-test' and 'admin-test' queues
  arena-processor-srm-admin-test:
    image: "arena-app:prod"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=srm-test,admin-test -Darena.processor-default-timeout=5
```



Suggestion 4, for `mm-test` queue which handles test of MM matches, the MM test will likely take a long time up to 15 minutes, then you can start a dedicated processor for it:

```yaml
  # Dedicated processor for 'mm-test' queue
  arena-processor-mm-test:
    image: "arena-app:prod"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=mm-test -Darena.processor-default-timeout=15
```



Suggestion 5, assuming we have a real MM contest, it’s expected to have large amount of registrants and long time to execute, then we can create a dedicated AWS SQS queue for that specific MM contest round (assuming the created queue name is `some-mm-queue`), then insert it into Mysql table `FARM_QUEUE_CONFIG`:

```sql
INSERT INTO FARM_QUEUE_CONFIG (ROUND, APP, `ACTION`, PLATFORM, PRACTICE, QUEUE_NAME)
VALUES('<mm-round-id>', 'marathon', 'test', 'nix', 0, 'some-mm-queue');
```

Then you can start multiple processors for the `some-mm-queue`, either by using Docker Swarm or Kubernate to scale, or you may simply using docker-compose, e.g:

```yaml
  # Dedicated processor for 'some-mm-queue'
  arena-processor-some-mm:
    image: "arena-app:prod"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_OPTS=-Darena.processor-queues=some-mm-queue -Darena.processor-default-timeout=15
```

```bash
# Start 10 processors for 'some-mm-queue' queue
docker-compose up -d --scale arena-processor-some-mm=10
```

One thing to note about the `OneQueue<->MultiProcessors` relationship, AWS SQS has a concept of [visibility timeout](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html), within the visibility time range, one message will be visible to only one processor. When multiple processors exist for same queue, the visibility timeout of the queue should be set to a higher value to avoid duplicate processing of a given message. E.g. in the above case, if it’s expected 15 minutes at most to process a given message, then set visibility timeout of `some-mm-queue` to 15 minutes.


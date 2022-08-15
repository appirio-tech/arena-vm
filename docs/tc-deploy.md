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
@database.port@=2021
@database.username@=informix
@database.password@=1nf0rm1x
@database.INFORMIXSERVER@=informixoltp_tcp
```

#### Mysql tokens

These are mysql related tokens. Change theses according to TC environment:

```properties
@farm.mysql.host@=arena-mysql
@farm.mysql.port@=3306
@farm.mysql.username@=farm
@farm.mysql.password@=farmpass
```

#### LDAP tokens

These are LDAP related tokens. Change theses according to TC environment:

```properties
## Tokens used in LDAP.properties
@ldapHost@=arena-ldap
@ldapPort@=389
```

#### Arena host tokens

These are arena host related tokens. Change theses according to TC environment:

```properties
# @farmHost@ and @farmControllerAppHost@ should be the host whether Farm Controller is deployed
@farmHost@=arena-controller
@farmControllerAppHost@=arena-controller

# @arenaListernerAppHost@ should be the host whether Listener is deployed
@arenaListernerAppHost@=arena-app
```

#### SSO tokens

These are SSO related tokens. Change theses according to TC environment:

```properties
@ssoCookieKey@=tcsso
@ssoHashSecret@=Yb2oMAtUoJyl6LvHEnRYF1Q5u5ags7DN6PXgDPZAU9Ku68k7wTJHIDaIT0DjXYubam
@ssoDomain@=topcoder-dev.com
@jwtCookieKey@=tcjwt
```



### Security Related Files

There are some security related files in `arena-vm/repos/app` folder, you need change them for dev/prod env before build image.

For LDAP:

- repos/app/scripts/TC.cloud.ldap.keystore
- repos/app/resources/LDAP.properties

For password encryption:

- repos/app/scripts/security.keystore.cloud
- repos/app/resources/com/topcoder/security/Util.properties



### Container Environment Variables

Environment variables for Farm Controller:

| Environment Variable | Description                               | Default Value       |
| -------------------- | ----------------------------------------- | ------------------- |
| CONTROLLER_JAVA_OPTS | The opts to start controller Java process | -Xms1024m -Xmx2048m |

Environment variables for Farm Processor:

| Environment Variable    | Description                              | Default Value       |
| ----------------------- | ---------------------------------------- | ------------------- |
| PROCESSOR_JAVA_OPTS     | The opts to start processor Java process | -Xms1024m -Xmx2048m |
| PROCESSOR_GROUP_ID      | The group name the processor belongs to  | PR-LX               |
| PROCESSOR_MAX_TASK_TIME | Max task execution time in milliseconds  | 850000              |

Environment variables for JBoss & Listeners:

| Environment Variable             | Description                                           | Default Value       |
| -------------------------------- | ----------------------------------------------------- | ------------------- |
| JBOSS_JAVA_OPTS                  | The opts to start JBoss Java process                  | -Xms2048m -Xmx8192m |
| MAIN_LISTENER_JAVA_OPTS          | The opts to start Main Listener Java process          | -Xms1024m -Xmx2048m |
| ADMIN_LISTENER_JAVA_OPTS         | The opts to start Admin Listener Java process         | -Xms1024m -Xmx2048m |
| MPSQAS_LISTENER_JAVA_OPTS        | The opts to start MPSQAS Listener Java process        | -Xms1024m -Xmx2048m |
| WEBSOCKET_LISTENER_JAVA_OPTS     | The opts to start WebSocket Listener Java process     | -Xms1024m -Xmx2048m |
| JBOSS_STARTUP_WAIT_TIME          | The time to wait for JBoss startup in seconds         | 120                 |
| MAIN_LISTENRER_STARTUP_WAIT_TIME | The time to wait for Main Listener startup in seconds | 120                 |



### Start Docker Containers

- Start container for Farm Controller:

  ```yaml
    arena-controller-dev:
      image: "arena-app:dev"
      command: ["/home/apps/start-services.sh", "controller"]
      ports:
        - "25000:25000"
        - "25001:25001"
      environment:
        - CONTROLLER_JAVA_OPTS="-Xms256m -Xmx1538m"
  ```

  

- Start container for Farm Processor:

  ```yaml
    arena-processor-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "processor"]
      environment:
        - PROCESSOR_JAVA_OPTS="-Xms256m -Xmx1538m"
        - PROCESSOR_GROUP_ID=PR-LX
        - PROCESSOR_MAX_TASK_TIME=850000
  ```

  

- Start container for JBoss & Listeners:

  ````yaml
    arena-app-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "app"]
      ports:
        - "5001:5001"
        - "5003:5003"
        - "5016:5016"
        - "5037:5037"
      environment:
        - JBOSS_JAVA_OPTS="-Xms512m -Xmx2048m"
        - MAIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
        - ADMIN_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
        - MPSQAS_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
        - WEBSOCKET_LISTENER_JAVA_OPTS="-Xms256m -Xmx1024m"
        - JBOSS_STARTUP_WAIT_TIME=60
        - MAIN_LISTENRER_STARTUP_WAIT_TIME=60
  ````

  


### Start Multiple Processors

At first refer to [ArenaArchitecture.md](./ArenaArchitecture.md) about the processor groups.

Assume you have 2 groups defined: `Group-One` and `Group-Two`, then you can start 2 processors:

```yaml
  arena-processor-group-one:
    image: "arena-app:dev"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_JAVA_OPTS="-Xms256m -Xmx1538m"
      - PROCESSOR_GROUP_ID=Group-One
      - PROCESSOR_MAX_TASK_TIME=850000
  arena-processor-group-two:
    image: "arena-app:dev"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_JAVA_OPTS="-Xms256m -Xmx1538m"
      - PROCESSOR_GROUP_ID=Group-Two
      - PROCESSOR_MAX_TASK_TIME=850000
```

Also you can start multiple processors for a same group.

E.g. start 5 processors for `Group-One`, and start 10 processors for `Group-Two`.

```bash
docker-compose up -d --scale arena-processor-group-one=5
docker-compose up -d --scale arena-processor-group-two=10
```


arena-vm
========

Initialization scripts for the Arena VM

## Local Deployment

### Prerequisites

  - Docker
  - [Github account with SSH key configured](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)

### Local Docker Deploy

```bash
# Goto docker folder
cd docker

# Start docker containers
docker-compose up -d

# Checkout Github repos 
# Note your Github account need be configured with SSH key
# Replace "/path-to-ssh-private-key" to your actual path of SSH private key
docker exec -it arena-app bash /home/apps/docker/checkout.sh "$(cat /path-to-ssh-private-key)"

# Build arena services, will take several minutes
docker exec -it arena-app bash /home/apps/docker/build.sh

# Start arena services, will take several minutes
docker exec -it arena-app bash -c "cd /home/apps/dev/arena-vm && /home/apps/dev/arena-vm/start-services.sh"
```



### Verify Client

Add `127.0.0.1 tc.cloud.topcoder.com` to your hosts. Then:

Verify admin client:

```bash
# Copy built admin client from docker
docker cp arena-app:/home/apps/dev/app/dist/admin-client-7.1.5.zip .

# Unzip
unzip admin-client-7.1.5.zip -d admin-client

# Run admin client
cd admin-client
sh admin.sh

# Then login with heffan/password
```

Verify arena client:

```bash
# Copy built arena client from docker
docker cp arena-app:/home/apps/dev/comp-eng/arena-client/build/arena-client-7.1.3.zip .

# Unzip
unzip arena-client-7.1.3.zip -d arena-client

# Run arena client
cd arena-client
sh arena.sh

# Then login with heffan/password
```



### Files changed

Refer to `docker/checkout.sh` for the file changes:

- `app/ivy.xml`:
  - change `spring-context` version from `4.1.0.RELEASE` to `5.3.22`
- `app/token.properties`:
  - change informix host to `arena-informix`
  - change mysql host to `arena-mysql`
  - change ldap host to `arena-ldap`
- `farm-server/ivy.xml`:
  - add `commons-codec` and `aws-java-sdk` dependencies
- `farm-server/src/scripts/processor.sh`:
  - add `sleep 10` to wait for processor to start
- `glue/settings/ivysettings-public.xml`:
  - set public root to `https://repo1.maven.org/maven2` 
- `glue/settings/ivysettings-default-chain.xml`:
  - remove `<ibiblio name="ibiblio" m2compatible="true" root="http://maven.appirio.net:8080" />` line
- `arena-vm/elasticmq-server.conf`:
  - change `sqs.topcoder.com` to `localhost`
- `arena-vm/start-farm-processor.sh`:
  - change `sqs.topcoder.com` to `localhost`
- `arena-vm/start-sqs-service.sh`:
  - use `nohup` to run `elasticmq-server`
- `arena-vm/TC.cloud.ldap.keystore`:
  - created using `TC_PROD_CA.pem` from `appiriodevops/ldap`


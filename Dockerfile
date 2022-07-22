FROM openjdk:8-alpine

RUN apk add --update bash tar wget unzip

RUN ["mkdir", "-p", "/home/apps/dev"]

RUN adduser -D -s /bin/bash apps

USER apps

WORKDIR /home/apps

RUN cd /home/apps \
       && wget http://downloads.sourceforge.net/project/jboss/JBoss/JBoss-4.0.5.GA/jboss-4.0.5.GA.zip \
       && unzip jboss-4.0.5.GA.zip \
       && rm -rf jboss-4.0.5.GA.zip

ENV JBOSS_HOME=/home/apps/jboss-4.0.5.GA
ENV PATH=$PATH:/home/apps/jboss-4.0.5.GA/bin

ADD ./ /home/apps/dev/arena-vm/

RUN ["tar", "-xvzf", "/home/apps/dev/arena-vm/osfiles.tgz"]
RUN ["tar", "-xvzf", "/home/apps/dev/arena-vm/linux-osfiles.tgz"]
RUN ["cp", "-r", "/home/apps/app/jboss-4.0.5.GA", "/home/apps/"]

ADD security.keystore.cloud /home/apps/app/scripts/
ADD TC.cloud.ldap.keystore /home/apps/app/scripts/

CMD ["/home/apps/dev/arena-vm/start-services.sh"]

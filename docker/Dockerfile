FROM centos:centos7

RUN yum install -y which wget unzip git make gcc gcc-c++ net-tools.x86_64 java-1.8.0-openjdk-devel

WORKDIR /

RUN wget -O apache-ant-1.7.0-bin.tar.gz https://archive.apache.org/dist/ant/binaries/apache-ant-1.7.0-bin.tar.gz \
  && tar zxf apache-ant-1.7.0-bin.tar.gz \
  && mv apache-ant-1.7.0 /opt/ \
  && rm -f apache-ant-1.7.0-bin.tar.gz

RUN wget -O apache-maven-3.2.5-bin.zip https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.zip \
  && unzip apache-maven-3.2.5-bin.zip \
  && mv apache-maven-3.2.5 /opt/ \
  && rm -f apache-maven-3.2.5-bin.zip

RUN wget -O ant-contrib-1.0b2-bin.tar.gz --no-check-certificate https://sourceforge.net/projects/ant-contrib/files/ant-contrib/ant-contrib-1.0b2/ant-contrib-1.0b2-bin.tar.gz/download \
  && tar zxf ant-contrib-1.0b2-bin.tar.gz \
  && cp -f ant-contrib/lib/ant-contrib.jar /opt/apache-ant-1.7.0/lib \
  && rm -rf ant-contrib \
  && rm -f ant-contrib-1.0b2-bin.tar.gz

RUN adduser -s /bin/bash apps
USER apps
WORKDIR /home/apps

RUN wget -O jboss-4.0.5.GA.zip --no-check-certificate https://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.0.5.GA/jboss-4.0.5.GA.zip/download \
  && unzip jboss-4.0.5.GA.zip \
  && rm -f jboss-4.0.5.GA.zip

ENV JBOSS_HOME=/home/apps/jboss-4.0.5.GA
ENV JAVA_HOME=/etc/alternatives/java_sdk
ENV ANT_HOME=/opt/apache-ant-1.7.0
ENV ANT_OPTS=-Xmx1024m
ENV PATH=$JAVA_HOME/bin:$ANT_HOME/bin:/opt/apache-maven-3.2.5/bin:$PATH
ENV BUILD_COMPILE_SOURCE=1.6

RUN mkdir -p /home/apps/docker
COPY . /home/apps/docker/

CMD ["sleep", "infinity"]
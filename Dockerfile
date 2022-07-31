FROM centos:centos7

RUN yum install -y which wget unzip git make net-tools python3 java-11-openjdk-devel
RUN yum install -y centos-release-scl-rh
RUN yum install -y devtoolset-11-gcc devtoolset-11-gcc-c++ 

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

# These are devtoolset-11 env for gcc11
ENV PCP_DIR=/opt/rh/devtoolset-11/root
ENV LD_LIBRARY_PATH=$PCP_DIR/usr/lib64:$PCP_DIR/usr/lib:$PCP_DIR/usr/lib64/dyninst:$PCP_DIR/usr/lib/dyninst

ENV JBOSS_HOME=/home/apps/jboss-4.0.5.GA
ENV JAVA_HOME=/etc/alternatives/java_sdk
ENV ANT_HOME=/opt/apache-ant-1.7.0
ENV ANT_OPTS=-Xmx1024m
ENV PATH=$PCP_DIR/usr/bin:$JAVA_HOME/bin:$ANT_HOME/bin:/opt/apache-maven-3.2.5/bin:$PATH
ENV BUILD_COMPILE_SOURCE=1.6
ENV ARENA_BUILD_TARGET=local

COPY --chown=apps ./repos /home/apps/dev/

RUN cd ~/dev/commons-aws \
  && mvn clean package -DskipTests=true \
  && mkdir -p ~/.ivy2/.ivy-cache \
  && cp ~/dev/commons-aws/target/commons-aws-0.0.1-SNAPSHOT.jar ~/.ivy2/.ivy-cache/
RUN cd ~/dev/app \
  && ant clean-all clean-cache \
  && ant publish-workspace-all \
  && ant compile-cpp2 \
  && ant package-AdminTool
RUN cd ~/dev/comp-eng/arena-client \
  && ant package-applet
RUN cd ~/dev/comp-eng/mpsqas-client \
  && ant package-applet
RUN cd ~/dev/app \
  && ant package-app-deployment
RUN cd ~/dev/farm-server \
  && ant -DprocessorType=-64bit package-processor-deployment
RUN cd ~ \
  && tar -xzvf ~/dev/app/build/artifacts/osfiles.tgz \
  && tar -xzvf ~/dev/farm-server/build/artifacts/linux-osfiles.tgz
RUN cp -r /home/apps/app/jboss-4.0.5.GA /home/apps/
RUN cp ~/dev/arena-vm/security.keystore.cloud ~/app/scripts/

COPY --chown=apps ./TC.cloud.ldap.keystore /home/apps/app/scripts/
RUN sed -i 's/sqs.topcoder.com/localhost/' ~/dev/arena-vm/elasticmq-server.conf
RUN sed -i 's/sqs.topcoder.com/localhost/' ~/dev/arena-vm/start-farm-processor.sh

CMD ["/home/apps/dev/arena-vm/start-services.sh"]
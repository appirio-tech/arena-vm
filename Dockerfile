FROM fedora

# initial updates
RUN yum update -y
RUN yum clean all
RUN yum install -y git
RUN yum install -y dotnet-sdk-6.0-6.0.113-1.fc37
# depot tools
RUN git config --global user.name "Topcoder" && git config --global user.email "support@topcoder.com"
RUN git config --global core.autocrlf false && git config --global core.filemode false
# get v8
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
ENV PATH="/depot_tools:$PATH"
RUN mkdir chr && cd chr
#RUN fetch chromium
#RUN cd src && ./build/install-build-deps.sh
#RUN gclient sync

RUN yum install -y which
RUN yum install -y wget
RUN yum install -y unzip
RUN yum install -y make
RUN yum install -y net-tools
RUN yum install -y python3
RUN yum install -y java-11-openjdk-devel
#RUN yum install -y centos-release-scl-rh
#RUN yum install -y devtoolset-11-gcc devtoolset-11-gcc-c++

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
RUN mkdir /home/apps/.ivy2 && mkdir /home/apps/.ivy2/.ivy-cache

RUN cp /home/apps/dev/backport-util-concurrent-2.1.jar /home/apps/.ivy2/.ivy-cache/ \
    && cp /home/apps/dev/activation-1.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/activeio-core-3.0.0-incubator.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/activemq-core-4.1.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/antlr-2.7.6.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/antlr-2.7.6rc1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/asm-1.5.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/asm-3.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/asm-attrs-1.5.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/backport-util-concurrent-2.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/base_exception-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/base_exception-2.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/basic_type_serialization-1.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/c3p0-0.9.0.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/c3p0-0.9.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/c3p0-0.9.1.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/cglib-2.1.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/cobertura.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/command_line_utility-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-beanutils-1.7.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-collections-3.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-dbcp-1.2.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-digester-1.8.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-discovery-0.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-io-1.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-io-2.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-logging-1.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/commons-pool-1.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/configuration_api-2.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/configuration_manager-2.1.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/data_validation-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/dom4j-1.6.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hibernate-3.1.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hibernate-annotations-3.3.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hibernate-3.2.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hibernate-3.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/id_generator-3.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/id_generator-3.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/ifxjdbc-3.00.JC3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/image_manipulation-1.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/image_overlay-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jaxb-api-jboss-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-4.2.2-ejb3x.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-4.2.2-jaxws.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/db_connection_factory-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/ejb3-persistence-3.3.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/file_system_server-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/file_upload-2.0.2.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/file_upload-2.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/guid_generator-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/heartbeat-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossall-client-4.0.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/image_manipulation-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jnlp-1.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossws-client-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hibernate-commons-annotations-3.3.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/hsqldb-1.8.0.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/htmllexer-jive-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossws-common-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/htmlparser-jive-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/httpunit-1.6.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/image_resizing-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/ip_server-2.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jai_codec-1.1.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jai_core-1.1.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/junit-3.8.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-cache-jdk50-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-common-client-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-jaxrpc-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-jaxws-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-saaj-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-xml-binding-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossall-client-4.0.3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/object_formatter-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossall-client-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossall-client-6.1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/random_string_image-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/scheduler-plugin.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/lightweight_xml_parser-1.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/log4j-1.2.13.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jersey-core-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jersey-server-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jivebase-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jiveforums-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jms-1.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jnlp-1.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jnlp-servlet-1.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/json_object-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jsp-api-2.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jsr311-api-1.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jta-1.0.1B.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/log4j-1.2.17.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mail-1.4.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mail-1.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mockejb-0.6-beta2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mysql-connector-java-3.1.13.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jboss-system-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mysql-connector-java-5.0.8.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/object_factory-2.0.1.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/postgresql-8.1-407.jdbc3.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/random_string_generator-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jbossws-spi-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/spell_check-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/topcoder_commons_utility.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/jgroups-4.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/unique_key_generator-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/velocity-dep-jive-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/webwork-jive-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/wsdl4j-1.6.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xalan-2.6.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xerces-2.6.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xml-apis-2.6.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xpp3_min-1.1.3.4.O.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xstream-1.2.2.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xwork-jive-4.2.5.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/mysql-connector-java-5.0.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/servlet-api-2.4.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/typesafe_enum-1.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xpp3-1.1.3.4d_b4_min.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/PactsClientServices-8.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/security-3.0.0.jar /home/apps/.ivy2/.ivy-cache \
    && cp /home/apps/dev/xstream-1.1.3.jar /home/apps/.ivy2/.ivy-cache

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

COPY --chown=apps ./env/local /home/apps/env/
COPY --chown=apps ./start-services.sh /home/apps/

CMD ["/home/apps/start-services.sh"]
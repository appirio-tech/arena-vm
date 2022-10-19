FROM centos:centos7

RUN yum update -y
RUN yum install -y which wget unzip git make nc net-tools python3 java-11-openjdk-devel
RUN yum install -y centos-release-scl-rh
RUN yum install -y devtoolset-11-gcc devtoolset-11-gcc-c++ 

# These are devtoolset-11 env for gcc11
ENV PCP_DIR=/opt/rh/devtoolset-11/root
ENV LD_LIBRARY_PATH=$PCP_DIR/usr/lib64:$PCP_DIR/usr/lib:$PCP_DIR/usr/lib64/dyninst:$PCP_DIR/usr/lib/dyninst
ENV PATH=$PCP_DIR/usr/bin:$PATH

WORKDIR /

RUN wget -O apache-ant-1.10.12-bin.tar.gz https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.12-bin.tar.gz \
  && tar zxf apache-ant-1.10.12-bin.tar.gz \
  && mv apache-ant-1.10.12 /opt/ \
  && rm -f apache-ant-1.10.12-bin.tar.gz

RUN wget -O ant-contrib-1.0b2-bin.tar.gz https://sourceforge.net/projects/ant-contrib/files/ant-contrib/ant-contrib-1.0b2/ant-contrib-1.0b2-bin.tar.gz/download \
  && tar zxf ant-contrib-1.0b2-bin.tar.gz \
  && cp -f ant-contrib/lib/ant-contrib.jar /opt/apache-ant-1.10.12/lib \
  && rm -rf ant-contrib \
  && rm -f ant-contrib-1.0b2-bin.tar.gz

RUN wget -O astyle_3.1_linux.tar.gz https://sourceforge.net/projects/astyle/files/astyle/astyle%203.1/astyle_3.1_linux.tar.gz/download \
  && tar zxf astyle_3.1_linux.tar.gz \
  && cd /astyle/build/gcc \
  && make \
  && cp -f /astyle/build/gcc/bin/astyle /usr/local/bin/ \
  && cd / \
  && rm -rf astyle \
  && rm -f astyle_3.1_linux.tar.gz

RUN adduser -s /bin/bash apps
USER apps
WORKDIR /home/apps

RUN wget -O jboss-4.0.5.GA.zip https://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.0.5.GA/jboss-4.0.5.GA.zip/download \
  && unzip jboss-4.0.5.GA.zip \
  && rm -f jboss-4.0.5.GA.zip

RUN ln -s /home/apps/jboss-4.0.5.GA /home/apps/jboss

ARG ARENA_BUILD_TARGET=local

ENV JBOSS_HOME=/home/apps/jboss
ENV JAVA_HOME=/etc/alternatives/java_sdk
ENV ANT_HOME=/opt/apache-ant-1.10.12
ENV ANT_OPTS=-Xmx1024m
ENV PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH
ENV BUILD_COMPILE_SOURCE=8
ENV ARENA_BUILD_TARGET=$ARENA_BUILD_TARGET

COPY --chown=apps ./repos /home/apps/dev/

RUN mkdir -p ~/.ivy2/.ivy-cache
COPY --chown=apps ./arena-libs /home/apps/.ivy2/.ivy-cache/

RUN cd ~/dev/app \
  && ant clean-all clean-cache \
  && ant publish-workspace-all \
  && ant compile-cpp2 \
  && ant deploy-app \
  && ant setup-jboss deploy-jboss \
  && ant deploy-controller \
  && ant deploy-processor \
  && ant package-processor-win \
  && ant package-AdminTool

RUN cd ~/dev/comp-eng/arena-client \
  && ant package-applet

RUN cd ~/dev/comp-eng/mpsqas-client \
  && ant package-applet

COPY --chown=apps ./start-services.sh /home/apps/

CMD ["/home/apps/start-services.sh"]
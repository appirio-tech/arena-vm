FROM fedora

RUN yum update -y
RUN yum install -y which wget zip unzip git make nc net-tools python3 java-11-openjdk-devel
RUN yum install -y devtoolset-11-gcc devtoolset-11-gcc-c++
RUN yum install -y epel-release
RUN yum install -y nginx
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

# These are devtoolset-11 env for gcc11
ENV PCP_DIR=/opt/rh/devtoolset-11/root
ENV LD_LIBRARY_PATH=$PCP_DIR/usr/lib64:$PCP_DIR/usr/lib:$PCP_DIR/usr/lib64/dyninst:$PCP_DIR/usr/lib/dyninst
ENV PATH=$PCP_DIR/usr/bin:$PATH

WORKDIR /

COPY --chown=root ./apps/apache-ant-1.10.12-bin.tar.gz /
RUN tar zxf apache-ant-1.10.12-bin.tar.gz \
  && mv apache-ant-1.10.12 /opt/ \
  && rm -f apache-ant-1.10.12-bin.tar.gz

COPY --chown=root ./apps/ant-contrib-1.0b2-bin.tar.gz /
RUN tar zxf ant-contrib-1.0b2-bin.tar.gz \
  && cp -f ant-contrib/lib/ant-contrib.jar /opt/apache-ant-1.10.12/lib \
  && rm -rf ant-contrib \
  && rm -f ant-contrib-1.0b2-bin.tar.gz

COPY --chown=root ./apps/astyle_3.1_linux.tar.gz /
RUN tar zxf astyle_3.1_linux.tar.gz \
  && cd /astyle/build/gcc \
  && make \
  && cp -f /astyle/build/gcc/bin/astyle /usr/local/bin/ \
  && cd / \
  && rm -rf astyle \
  && rm -f astyle_3.1_linux.tar.gz

RUN adduser -s /bin/bash apps

RUN mkdir -p /var/lib/nginx/tmp /var/log/nginx \
    && chown -R apps:apps /var/lib/nginx /var/log/nginx \
    && chmod -R 755 /var/lib/nginx /var/log/nginx

USER apps
WORKDIR /home/apps

COPY --chown=apps ./apps/jboss-4.0.5.GA.zip /home/apps
RUN unzip jboss-4.0.5.GA.zip \
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

RUN mkdir -p /home/apps/applets
RUN unzip /home/apps/dev/app/dist/admin-client.zip -d /home/apps/applets
RUN unzip /home/apps/dev/comp-eng/arena-client/build/arena-client.zip -d /home/apps/applets
RUN unzip /home/apps/dev/comp-eng/mpsqas-client/build/mpsqas-client.zip -d /home/apps/applets
RUN zip -r -j applets.zip /home/apps/applets

COPY --chown=apps ./nginx-applets.conf /home/apps/

CMD ["/home/apps/start-services.sh"]
FROM fedora

RUN yum update -y
RUN yum install -y which
RUN yum install -y wget
RUN yum install -y zip
RUN yum install -y unzip
RUN yum install -y git
RUN yum install -y make
RUN yum install -y nc
RUN yum install -y net-tools
RUN yum install -y python3
RUN yum install -y java-11-openjdk-devel
RUN yum install -y nginx
RUN yum install -y dotnet-sdk-6.0-6.0.113-1.fc37
RUN yum install -y v8-devel

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
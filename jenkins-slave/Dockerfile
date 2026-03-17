FROM ubuntu:23.04

LABEL maintainer=" Timur Galeev <timur_galeev@outlook.com>" \
  description="Docker image to build Java applications."

#================================================
# Customize sources for apt-get
#================================================
RUN DISTRIB_CODENAME=$(cat /etc/*release* | grep DISTRIB_CODENAME | cut -f2 -d'=') \
  && echo "deb http://archive.ubuntu.com/ubuntu ${DISTRIB_CODENAME} main universe\n" > /etc/apt/sources.list \
  && echo "deb http://archive.ubuntu.com/ubuntu ${DISTRIB_CODENAME}-updates main universe\n" >> /etc/apt/sources.list \
  && echo "deb http://security.ubuntu.com/ubuntu ${DISTRIB_CODENAME}-security main universe\n" >> /etc/apt/sources.list

RUN apt-get update -qqy \
  && apt-get -qqy --no-install-recommends install software-properties-common \
  && add-apt-repository -y ppa:git-core/ppa

# Make sure the package repository is up to date.
RUN apt-get update \
  && apt-get upgrade -y --autoremove \
  && apt-get --no-install-recommends install -qqy \
  git \
  locales \
  tar zip unzip \
  wget curl \
  python pip groff \
  jq \
  parallel \
  uuid-runtime \
  git-lfs \
  less vim tree \
  openssh-server \
  build-essential \
  openjdk-8-jdk \
  apt-transport-https \
  ca-certificates \
  gnupg-agent \
  rlwrap \
  rsync \
  software-properties-common \
  && rm -rf /var/lib/apt/lists/* \
  && sed -i 's/securerandom\.source=file:\/dev\/random/securerandom\.source=file:\/dev\/urandom/' ./usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/java.security

# workaround https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=775775
RUN [ -f "/etc/ssl/certs/java/cacerts" ] || /var/lib/dpkg/info/ca-certificates-java.postinst configure

# workaround "You are using pip version 8.1.1, however version 9.0.1 is available."
RUN pip install --upgrade pip setuptools

RUN pip install yq

#====================================
# Maven
#====================================
ENV MAVEN_VERSION 3.6.2

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

#====================================
# Set the locale
#====================================
RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

#====================================
# Add user jenkins to the image
#====================================
RUN adduser --quiet jenkins && \
  echo "jenkins:jenkins" | chpasswd && \
  mkdir /home/jenkins/.m2

#====================================
# Docker
#====================================
RUN groupadd -g 497 docker && groupadd -g 996 docker2 && usermod -a -G docker,docker2 jenkins
RUN wget -qO- https://get.docker.com/ | sh
RUN export MIRROR_SOURCE=https://registry.hub.docker.com
RUN export MIRROR_SOURCE_INDEX=https://registry.hub.docker.com

#====================================
# Docker-compose
#====================================
ARG DOCKER_COMPOSE_VERSION=1.22.0
RUN curl -sL https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m) > /usr/local/bin/docker-compose
RUN chmod +x /usr/local/bin/docker-compose

#====================================
# AWS CLI
#====================================
RUN pip install awscli

# compatibility with AWS CLI Plugin which expects pip to be installed as user
RUN mkdir -p /home/jenkins/.local/bin/ \
  && ln -s /usr/bin/pip /home/jenkins/.local/bin/pip \
  && chown -R jenkins:jenkins /home/jenkins/.local

#====================================
# NODE JS
# See https://github.com/nodesource/distributions/blob/master/README.md
# See https://nodejs.org/en/download/package-manager/#debian-and-ubuntu-based-linux-distributions
#====================================
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash \
  && apt-get install -y nodejs

#====================================
# Kubernetes CLI
# See https://storage.googleapis.com/kubernetes-release/release/stable.txt
#====================================
RUN curl https://storage.googleapis.com/kubernetes-release/release/v1.16.1/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl && chmod +x /usr/local/bin/kubectl

#====================================
# Copy authorized keys
#====================================
COPY .ssh/authorized_keys /home/jenkins/.ssh/authorized_keys

RUN sed -i 's|session    required     pam_loginuid.so|session    optional     pam_loginuid.so|g' /etc/pam.d/sshd && \
  mkdir -p /var/run/sshd && \
  chown -R jenkins:jenkins /home/jenkins/.m2/ && \
  chown -R jenkins:jenkins /home/jenkins/.ssh/

# Standard SSH port
EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]

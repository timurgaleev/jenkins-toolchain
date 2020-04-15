FROM ubuntu:18.04

RUN apt-get update && \
    apt-get -qy full-upgrade && \
    apt-get install -qy git && \
# Install a basic SSH server
    apt-get -qqy --no-install-recommends install jq parallel uuid-runtime gzip tar default-jdk openssh-server  && \
    sed -i 's|session    required     pam_loginuid.so|session    optional     pam_loginuid.so|g' /etc/pam.d/sshd && \
    mkdir -p /var/run/sshd && \
# Cleanup old packages
    apt-get -qy autoremove

# Install JDK 8 (latest stable edition at 2019-04-01)
    # apt-get install -qy openjdk-8-jdk && \
# Install maven
    # apt-get install -qy maven && \

# Add user jenkins to the image
    # adduser --quiet jenkins && \
# Set password for the jenkins user (you may want to alter this).
    # echo "jenkins:jenkins" | chpasswd && \
    # mkdir /home/jenkins/.m2

RUN wget -q https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk-11.0.2_linux-x64_bin.tar.gz \
    && tar xfvz /tmp/openjdk-11.0.2_linux-x64_bin.tar.gz --directory /usr/lib/jvm \
    && rm -f /tmp/openjdk-11.0.2_linux-x64_bin.tar.gz

RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk-11.0.2/bin/java 20000
RUN update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk-11.0.2/bin/javac 20000
# RUN update-alternatives --config java
RUN java -version

RUN groupadd -g 497 docker && groupadd -g 996 docker2 && usermod -a -G docker,docker2 jenkins
RUN wget -qO- https://get.docker.com/ | sh
RUN export MIRROR_SOURCE=https://registry.hub.docker.com
RUN export MIRROR_SOURCE_INDEX=https://registry.hub.docker.com

ARG DOCKER_COMPOSE_VERSION=1.22.0
RUN curl -sL https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
RUN chmod +x /usr/local/bin/docker-compose

ARG JENKINS_REMOTING_VERSION=3.29
ARG AGENT_WORKDIR=/home/jenkins/agent
RUN curl --create-dirs -sSLo /usr/share/jenkins/slave.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${JENKINS_REMOTING_VERSION}/remoting-${JENKINS_REMOTING_VERSION}.jar \
    && chmod 755 /usr/share/jenkins \
    && chmod 644 /usr/share/jenkins/slave.jar

USER jenkins
ENV AGENT_WORKDIR=${AGENT_WORKDIR}
RUN mkdir /home/jenkins/.jenkins && mkdir -p ${AGENT_WORKDIR}
WORKDIR /home/jenkins

VOLUME /home/jenkins/.jenkins
VOLUME ${AGENT_WORKDIR}

# Copy authorized keys
COPY .ssh/authorized_keys /home/jenkins/.ssh/authorized_keys

RUN chown -R jenkins:jenkins /home/jenkins/.m2/ && \
    chown -R jenkins:jenkins /home/jenkins/.ssh/

# Standard SSH port
EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]

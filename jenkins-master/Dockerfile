FROM jenkins/jenkins:2.230-jdk11

USER root

# Create docker group
RUN groupadd -g 996 docker && usermod -aG docker jenkins

# Install the AWS CLI for retrieving credentials from S3
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends python3 python3-pip python3-setuptools docker && \
    apt-get autoremove -y --purge && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    python3 -m pip install --no-cache-dir --upgrade pip awscli

USER jenkins

COPY plugins/plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY casc_configs/* /usr/share/jenkins/ref/casc_configs/
COPY email-templates/* /usr/share/jenkins/ref/email-templates/
COPY groovy-scripts/* /usr/share/jenkins/ref/init.groovy.d/
COPY secrets/* /usr/share/jenkins/ref/secrets/slave-to-master-security-kill-switch
COPY user-content/* /usr/share/jenkins/ref/userContent/

ENV CASC_JENKINS_CONFIG /usr/share/jenkins/ref/casc_configs

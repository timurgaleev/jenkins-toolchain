
pipeline {
  agent {
    label 'docker'
  }

  options{
    ansiColor('xterm')
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '30',
        artifactNumToKeepStr: '20',
        daysToKeepStr: '30',
        numToKeepStr: '20'
      )
    )
    disableConcurrentBuilds()
    timeout(time: 20, unit: 'MINUTES')
  }

  environment {
    ECR_URL = '411719562396.dkr.ecr.us-east-1.amazonaws.com'
    ERROR_EMAIL = 'timur_galeev@outlook.com'
    GIT_SHA7 = "${GIT_COMMIT[0..6]}"
    IMAGE_NAME = 'jenkins'
    TAG = "${env.BRANCH_NAME.replace('/','-')}-${env.BUILD_NUMBER}"
    TIMESTAMP = "${new Date(currentBuild.startTimeInMillis).format("yyMMddHHmm")}"
  }

  parameters {
    string(defaultValue: "2.204.1-jdk11-${env.BRANCH_NAME.replace('/','-')}",
           description: 'Tag for your jenkins-master docker image? (Git sha will be automatically appended to the image tag)',
           name: 'jenkins_master_image_tag')
    booleanParam(name: 'PUSH_JENKINS_DOCKER_IMAGE_TAG',
                 defaultValue: false,
                 description: 'Toggle this value if you want to Push the jenkins image to ECR')
  }

  stages {
    stage('Error email setup') {
      when { expression { env.BRANCH_NAME != 'master' } }
      steps {
        script {
          GIT_COMMIT = sh(returnStdout: true, script: "git rev-parse HEAD^{commit}")
          ERROR_EMAIL = sh(returnStdout: true, script: "git --no-pager show -s --format='%ae' ${GIT_COMMIT}")
        }
        echo "Send topic branch build errors to commit author ${ERROR_EMAIL}"
      }
    }

    stage('Build Jenkins master image') {
      steps {
        dir('jenkins-master') {
          sh "docker build -t $ECR_URL/$IMAGE_NAME:${params.jenkins_master_image_tag}-${GIT_SHA7} ."
        }
      }
    }

    stage('Twistlock stage') {
      steps {
        runTwistlock(
          repository: "${ECR_URL}/${IMAGE_NAME}",
          tag: "${params.jenkins_master_image_tag}-${GIT_SHA7}"
        )
      }
    }

    stage('Push Jenkins master image to ECR') {
      when {
        expression {
          params.PUSH_JENKINS_DOCKER_IMAGE_TAG == true
        }
      }
      steps {
        sh """
          set +x
          \$(aws ecr get-login --no-include-email --region us-east-1)
          set -xe
          docker push $ECR_URL/$IMAGE_NAME:${params.jenkins_master_image_tag}-${GIT_SHA7}
          docker rmi $ECR_URL/$IMAGE_NAME:${params.jenkins_master_image_tag}-${GIT_SHA7}
        """
      }
    }
  }
  post {
    failure {
      emailext (
        to: "${ERROR_EMAIL}",
        subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
        body: 'Failed to build Jenkins master image',
        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
      )
    }
  }
}

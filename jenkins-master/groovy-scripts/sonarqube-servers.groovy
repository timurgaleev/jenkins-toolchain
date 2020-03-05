import hudson.model.*
import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.TriggersConfig
import hudson.tools.*

def instance = Jenkins.getInstance()
def sonarName = 'Sonarqube server'
def sonarServerUrl = System.getenv('SONARQUBE_SERVER_URL')
def sonarCredentialsId = 'sonarqube-authentication-token'

println '--> Configuring SonarQube'

def SonarGlobalConfiguration sonarConfig = instance.getDescriptor(SonarGlobalConfiguration.class)

def existingSonarInstallations = sonarConfig.getInstallations()*.getName()
def sonarInstallationExists = false

existingSonarInstallations.each { name ->
  sonarInstallationExists = true
  println("--> SonarQube configuration already exists: " + name)
}

if (!sonarConfig.get().isBuildWrapperEnabled()) {
  sonarConfig.get().setBuildWrapperEnabled(true);
}

if (!sonarInstallationExists) {
  def sonarInstallation = new SonarInstallation(
    sonarName,
    sonarServerUrl,
    sonarCredentialsId,
    null,
    null,
    '',
    '',
    '',
    new TriggersConfig()
  )
  sonarConfig.setInstallations((SonarInstallation[]) sonarInstallation)
  sonarConfig.save()
}

println '--> Configuring SonarQube... done'

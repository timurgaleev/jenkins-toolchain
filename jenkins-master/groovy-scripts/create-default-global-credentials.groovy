import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import org.jenkinsci.plugins.kubernetes.credentials.FileSystemServiceAccountCredential
import com.datapipe.jenkins.vault.credentials.VaultAppRoleCredential

global_domain = Domain.global()
provider = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
credentials_store = provider.getStore()

sonarqubeSecretText = new StringCredentialsImpl(
  CredentialsScope.GLOBAL,
  'sonarqube-authentication-token',
  'SonarQube authentication token',
  Secret.fromString(System.getenv('SONARQUBE_TOKEN'))
)
credentials_store.addCredentials(global_domain, sonarqubeSecretText)

k8sServiceAccount = new FileSystemServiceAccountCredential(
  CredentialsScope.GLOBAL,
  'jenkins',
  'jenkins service account'
)
credentials_store.addCredentials(global_domain, k8sServiceAccount)

vaultAddressSecretText = new StringCredentialsImpl(
  CredentialsScope.GLOBAL,
  'vault-addr',
  'Vault address',
  Secret.fromString(System.getenv('VAULT_ADDRESS'))
)
credentials_store.addCredentials(global_domain, vaultAddressSecretText)

vaultAppRoleSecret = new VaultAppRoleCredential(
  CredentialsScope.GLOBAL,
  'jenkins-approle',
  'Vault AppRole authentication for Jenkins',
  'jenkins',
  Secret.fromString(System.getenv('VAULT_JENKINS_SECRET_ID')),
  'approle'
)
credentials_store.addCredentials(global_domain, vaultAppRoleSecret)

provider.save()
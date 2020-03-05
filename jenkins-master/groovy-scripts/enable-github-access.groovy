import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl

global_domain = Domain.global()
provider = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
credentials_store = provider.getStore()

secretText = new StringCredentialsImpl(
  CredentialsScope.GLOBAL,
  'jenkins-github-api-token',
  'Jenkins integration with github',
  Secret.fromString(System.getenv('GITHUB_API_TOKEN'))
)
credentials_store.addCredentials(global_domain, secretText)

githubUsernamePassword = new UsernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  'github-api-token',
  'Username and github api token to access private repos (cf-deployer)',
  'cf-deployer',
  Secret.fromString(System.getenv('GITHUB_API_TOKEN')).getPlainText()
)
credentials_store.addCredentials(global_domain, githubUsernamePassword)

provider.save()

import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

if (System.getenv('LDAP_SERVER')) {
  println '--> LDAP is enabled, skipping jenkins own user database configuration'
  return
}

if (instance.security.toString() != 'SECURED' && System.getenv('GOOGLE_AUTH') != 'enabled') {
  println '--> Securing jenkins...'

  def username = System.getenv('JENKINS_USERNAME')
  def password = System.getenv('JENKINS_USER_PASSWORD')

  def hudsonRealm = new HudsonPrivateSecurityRealm(false)
  hudsonRealm.createAccount(username,password)
  instance.setSecurityRealm(hudsonRealm)

  def strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
  strategy.setAllowAnonymousRead(false)
  instance.setAuthorizationStrategy(strategy)

  instance.save()

  println '--> Jenkins own db security completed'
} else {
  println '--> Nothing changed from security/users scripts'
}

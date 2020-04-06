import jenkins.model.Jenkins
import org.jenkinsci.plugins.authorizeproject.*
import org.jenkinsci.plugins.authorizeproject.strategy.*
import jenkins.security.QueueItemAuthenticatorConfiguration

Thread.start {
  sleep 4000
  def instance = Jenkins.getInstance()

  def strategyMap = [
    (instance.getDescriptor(AnonymousAuthorizationStrategy.class).getId()): true, 
    (instance.getDescriptor(TriggeringUsersAuthorizationStrategy.class).getId()): true,
    (instance.getDescriptor(SpecificUsersAuthorizationStrategy.class).getId()): false,
    (instance.getDescriptor(SystemAuthorizationStrategy.class).getId()): false
  ]

  def authenticators = QueueItemAuthenticatorConfiguration.get().getAuthenticators()
  def configureProjectAuthenticator = true
  def configureDefaultAuthenticator = true
  for (authenticator in authenticators) {
    if (authenticator instanceof ProjectQueueItemAuthenticator) {
      configureProjectAuthenticator = false
    }
    if (authenticator instanceof GlobalQueueItemAuthenticator) {
      configureDefaultAuthenticator = false
    }
  }

  if (configureProjectAuthenticator) {
    authenticators.add(new ProjectQueueItemAuthenticator(strategyMap))
  }

  if (configureDefaultAuthenticator) {
    authenticators.add(new GlobalQueueItemAuthenticator(new TriggeringUsersAuthorizationStrategy()))
  }

  instance.save()

  println '--> Authorize project access control for builds configuration complete'
}

import jenkins.model.*
import hudson.security.LDAPSecurityRealm
import jenkins.security.plugins.ldap.FromGroupSearchLDAPGroupMembershipStrategy
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.*
import hudson.util.Secret
import jenkins.model.IdStrategy
import jenkins.security.plugins.ldap.LDAPConfiguration
import net.sf.json.JSONObject

if (!System.getenv('LDAP_SERVER')) {
  println '--> LDAP is disabled'
  return
}

if(!binding.hasVariable('ldap_settings')) {
  ldap_settings = [
    'server': System.getenv('LDAP_SERVER'),
    'rootDN': System.getenv('LDAP_ROOT_DN'),
    'inhibitInferRootDN': false,
    'managerDN': System.getenv('LDAP_MANAGER_DN'),
    'managerPasswordSecret': System.getenv('LDAP_MANAGER_PASSWORD_SECRET'),
    'userSearchBase': 'cn=users,cn=accounts',
    'groupSearchBase': 'cn=groups,cn=accounts',
    'groupSearchFilter': '',
    'groupMembershipFilter': '(| (member={0}) (uniqueMember={0}) (memberUid={1}))'
  ]
}
if(!(ldap_settings instanceof Map)) {
  throw new Exception('ldap_settings must be a Map.')
}

ldap_settings = ldap_settings as JSONObject

if(!(Jenkins.instance.securityRealm instanceof LDAPSecurityRealm)) {
  LDAPConfiguration conf = new LDAPConfiguration(
    ldap_settings.optString('server'),
    ldap_settings.optString('rootDN'),
    ldap_settings.optBoolean('inhibitInferRootDN'),
    ldap_settings.optString('managerDN'),
    Secret.fromString(ldap_settings.optString('managerPasswordSecret')))

  conf.userSearchBase = ldap_settings.optString('userSearchBase')
  conf.userSearch = ldap_settings.optString('userSearch', LDAPSecurityRealm.DescriptorImpl.DEFAULT_USER_SEARCH)
  conf.groupSearchBase = ldap_settings.optString('groupSearchBase')
  conf.groupSearchFilter = ldap_settings.optString('groupSearchFilter')
  conf.groupMembershipStrategy = new FromGroupSearchLDAPGroupMembershipStrategy(ldap_settings.optString('groupMembershipFilter'))
  conf.environmentProperties = (ldap_settings.opt('environmentProperties')?:[:]).collect { k, v ->
    new LDAPSecurityRealm.EnvironmentProperty(k.toString(), v.toString())
  } as LDAPSecurityRealm.EnvironmentProperty[]
  conf.displayNameAttributeName = ldap_settings.optString('displayNameAttributeName', LDAPSecurityRealm.DescriptorImpl.DEFAULT_DISPLAYNAME_ATTRIBUTE_NAME)
  conf.mailAddressAttributeName = ldap_settings.optString('mailAddressAttributeName', LDAPSecurityRealm.DescriptorImpl.DEFAULT_MAILADDRESS_ATTRIBUTE_NAME)

  List<LDAPConfiguration> configurations = [conf]
  Jenkins.instance.securityRealm = new LDAPSecurityRealm(
    configurations,
    ldap_settings.optBoolean('disableMailAddressResolver'),
    null, // new LDAPSecurityRealm.CacheConfiguration(50, 60),
    IdStrategy.CASE_INSENSITIVE,
    IdStrategy.CASE_INSENSITIVE)

  Jenkins.instance.securityRealm.setDisableRolePrefixing(true)
  // Logged-in users strategy
  // def strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
  // strategy.setAllowAnonymousRead(false)
  // Jenkins.instance.setAuthorizationStrategy(strategy)

  // Role-Based strategy
  def roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
  Jenkins.instance.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

  Jenkins.instance.save()
  println 'Security realm set to LDAP.'
} else {
  println 'Nothing changed. LDAP security realm already configured.'
}

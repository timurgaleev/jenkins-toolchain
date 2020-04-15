import java.nio.file.Files
import net.sf.json.JSONObject
import org.jenkinsci.plugins.plaincredentials.impl.*
import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
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

//SSH key

def jenkinsMasterKeyParameters = [
  description:  'Master Key for agent',
  id:           'agent_root',
  secret:       '',
  userName:     'jenkins',
  key:          new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource('''-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn
NhAAAAAwEAAQAAAQEA7Zk4Uk0U5iik7GnDleW+YpqXO2laBIX7hSwb9h2XXRfqkXynBhIy
7QsZ/s0GJ2YK7vFexBJblZGSYO7Cu3PrJH0WJHJJIef+a8kkcCpAGG3Xaorv5FsEnRiSTq
QY/w+VCBpvgHzc+bj82TZdgAqoIvBEHwb9D00JugDkoQCH5HYNQ2KM0s4bZL67rEiLr4Qa
4FUGE9VkgZLGs862Xhfkhh64JZ2r/t3F8/vZpGupp/grcjkU/PV8Gc1MuExC0WYP0Hnc7U
nUmDk9e3vSFiKxt+61BBo8Hx900vDwAnfV3tVmtMRph7xiVcSXD+KW2OJltUGSE1F+TEjx
DvrdSrTs/QAAA8juNsnN7jbJzQAAAAdzc2gtcnNhAAABAQDtmThSTRTmKKTsacOV5b5imp
c7aVoEhfuFLBv2HZddF+qRfKcGEjLtCxn+zQYnZgru8V7EEluVkZJg7sK7c+skfRYkckkh
5/5rySRwKkAYbddqiu/kWwSdGJJOpBj/D5UIGm+AfNz5uPzZNl2ACqgi8EQfBv0PTQm6AO
ShAIfkdg1DYozSzhtkvrusSIuvhBrgVQYT1WSBksazzrZeF+SGHrglnav+3cXz+9mka6mn
+CtyORT89XwZzUy4TELRZg/QedztSdSYOT17e9IWIrG37rUEGjwfH3TS8PACd9Xe1Wa0xG
mHvGJVxJcP4pbY4mW1QZITUX5MSPEO+t1KtOz9AAAAAwEAAQAAAQEApb1AAIksjvDDOvb8
LFA64mWcISF4XbdooJkIWEKEF7hbNGmTyTRgueIInClApbMU5WESDgwQPuFxDpT+2ewelz
F6hKoEQdanL2D8PhH67A+TOQr6FH05VqyRQrJNjX69Iw3twd9X9gsItiyqRGsqplSb8Fjb
phTDODDvSzAnm0E2GuSDo4xQ4K/3eXJEknYVtXIxDw2F8JU/zNk9Un8K0tfnYpsdFf+uws
ff7mjmAh+tm1O/FVj+ye27PSY4W2J1KwldYj1Wexg6IzUAq3bhQJcSshAYdoo3md78S1PS
5ekepuE79INEgPG9fplDMHjLlPpzyOLpGFGB4/wLkm3FAQAAAIBkR1oMFQ11iMILZnaHum
mG4SG+FEs2eV6siKbk/FYPZWIX10H0sHJ+nm73pClibuSfVOpbX1Jf4VTzOLVT7RG2K+CU
hpTq3Tu2JccjYKHccDtpBylSZVyxgLej5VhNrrRlpWLs2ERQ8eFODU0bB48YWMMmrcC8NU
hPXEDLCUn2ywAAAIEA+e47Nib7FZT8DnrHVKwhtNNk+4VKWG9pUyhzYF/DlIs+8B8Os12c
E2uLpfQQdPjYAgxNlhjKOxWjRDq/rToCBJhghULpdduhYXuyNnMezM8202AF4wy4OCzkqF
mclTsXt+Xg5J1QKVlYc+pNduWNdi22zHI4rW94RBoqn1MV+LEAAACBAPNeUpl8zwiEKJeA
ybtpG0IJCm+R7vEc6wXST75OzrdooxyochP5GhGruX/J14TPtPu1m8k0oFVn0K0nJb3J88
xtCVMe527W2i0njQTX0AOCX+yFQ9eoPReT2Gnq1/3+DeQeP+ZdW3Cf1Yi5oCKbNqyRhU1k
8Kpc20fLGGolnwwNAAAAD3NzaF9qZW5raW5zLnB1YgECAw==
-----END OPENSSH PRIVATE KEY-----''')
]

// define private key
def privateKey = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  jenkinsMasterKeyParameters.id,
  jenkinsMasterKeyParameters.userName,
  jenkinsMasterKeyParameters.key,
  jenkinsMasterKeyParameters.secret,
  jenkinsMasterKeyParameters.description
)

// add credential to store
credentials_store.addCredentials(global_domain, privateKey)

provider.save()
println("Credentials have been added successfully.")
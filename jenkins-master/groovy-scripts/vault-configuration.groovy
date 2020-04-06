import jenkins.model.*
import com.datapipe.jenkins.vault.configuration.GlobalVaultConfiguration
import com.datapipe.jenkins.vault.configuration.VaultConfiguration

Thread.start {
  sleep 2000
  println '--> Configuring Vault globally'

  GlobalVaultConfiguration globalVaultConfiguration = GlobalVaultConfiguration.get()

  VaultConfiguration vaultConfiguration = new VaultConfiguration()
  vaultConfiguration.setVaultUrl(System.getenv('VAULT_ADDRESS'))
  vaultConfiguration.setVaultCredentialId('jenkins-approle')
  vaultConfiguration.setVaultNamespace('')
  vaultConfiguration.setEngineVersion(2)
  vaultConfiguration.setFailIfNotFound(true)
  vaultConfiguration.setSkipSslVerification(false)
  vaultConfiguration.setTimeout(30)

  globalVaultConfiguration.setConfiguration(vaultConfiguration)

  println '--> Configuring Vault globally... done'
}

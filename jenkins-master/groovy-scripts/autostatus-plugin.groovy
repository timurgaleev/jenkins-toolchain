s
import jenkins.model.Jenkins
import org.jenkinsci.plugins.githubautostatus.BuildStatusConfig

// We want github-autostatus plugin 'Send to Github' disabled by default
def buildStatus = BuildStatusConfig.get()
buildStatus.getEnableGithub()
buildStatus.setEnableGithub(false)


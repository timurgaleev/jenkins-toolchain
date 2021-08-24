import hudson.model.*
import jenkins.model.*

def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()

// Set Jenkins root URL
jenkinsLocationConfiguration.setUrl(System.getenv('JENKINS_ROOT_URL'))

jenkinsLocationConfiguration.setAdminAddress('Jenkins <timur_galeev@outlook.com>')

jenkinsLocationConfiguration.save()

// Set number of master executors
Jenkins.instance.setNumExecutors(0)

// Set slave agent port
def agentport = System.getenv('JENKINS_SLAVE_AGENT_PORT') ?: '50000'
Jenkins.instance.setSlaveAgentPort(agentport.toInteger())

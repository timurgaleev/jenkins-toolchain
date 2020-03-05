import jenkins.model.*
import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.volumes.HostPathVolume

def instance = Jenkins.getInstance()

def awsaccount = System.getenv('AWS_ACCOUNT')
def ecrregion = System.getenv('ECR_REGION')
def environment = System.getenv('JENKINS_ENVIRONMENT')
def jenkinsiamrole = System.getenv('JENKINS_IAM_ROLE')
def jenkinsagentport = System.getenv('JENKINS_SLAVE_AGENT_PORT')

Thread.start {
  sleep 6000
  println '--> Configuring Kubernetes cloud'

  def k8sCloud = new KubernetesCloud("jenkins-${environment}")
  k8sCloud.setServerUrl('https://kubernetes.default.svc.cluster.local')
  k8sCloud.setSkipTlsVerify(false)
  k8sCloud.setNamespace('jenkins')
  // k8sCloud.setCredentialsId('kubernetes-service-account')
  k8sCloud.setDirectConnection(false)
  k8sCloud.setJenkinsUrl(instance.getRootUrl())
  k8sCloud.setJenkinsTunnel("jenkins-jnlp.jenkins.svc.cluster.local:${jenkinsagentport}")
  k8sCloud.setConnectTimeout(10)
  k8sCloud.setReadTimeout(15)
  k8sCloud.setContainerCapStr("50")
  // k8sCloud.setRetentionTimeout(60)
  // k8sCloud.setPodRetention(0)
  k8sCloud.setMaxRequestsPerHostStr("50")
  k8sCloud.setWaitForPodSec(900)

  def eksPodAnnotation = new PodAnnotation("eks.amazonaws.com/role-arn", jenkinsiamrole)
  def dockerSocket = new HostPathVolume("/var/run/docker.sock", "/var/run/docker.sock");

  def podTemplate = new PodTemplate()
  podTemplate.setName('jenkins-agent')
  podTemplate.setNamespace('jenkins')
  podTemplate.setLabel('jenkins-agent')
  podTemplate.setAnnotations([eksPodAnnotation])
  podTemplate.setHostNetwork(false)
  podTemplate.setRemoteFs('/home/jenkins')
  podTemplate.setRunAsGroup('0')
  podTemplate.setRunAsUser('0')
  podTemplate.setServiceAccount('jenkins')
  podTemplate.setShowRawYaml(false)
  podTemplate.setVolumes([dockerSocket])

  ContainerTemplate jnlpJenkinsAgent = new ContainerTemplate('jnlp', "${awsaccount}.dkr.ecr.${ecrregion}.amazonaws.com/jenkins:jenkins-agent-jdk11")
  jnlpJenkinsAgent.setAlwaysPullImage(false)
  jnlpJenkinsAgent.setArgs('')
  jnlpJenkinsAgent.setCommand('')
  jnlpJenkinsAgent.setResourceRequestCpu('500m')
  jnlpJenkinsAgent.setResourceRequestMemory('1024Mi')
  jnlpJenkinsAgent.setTtyEnabled(true)

  podTemplate.setContainers([jnlpJenkinsAgent])
  k8sCloud.addTemplate(podTemplate)

  podTemplate = new PodTemplate()
  podTemplate.setName('docker')
  podTemplate.setNamespace('jenkins')
  podTemplate.setLabel('docker')
  podTemplate.setAnnotations([eksPodAnnotation])
  podTemplate.setHostNetwork(false)
  podTemplate.setRemoteFs('/home/jenkins')
  podTemplate.setRunAsGroup('0')
  podTemplate.setRunAsUser('0')
  podTemplate.setServiceAccount('jenkins')
  podTemplate.setShowRawYaml(false)
  podTemplate.setVolumes([dockerSocket])


  ContainerTemplate jnlpDockerTemplate = new ContainerTemplate('jnlp', "${awsaccount}.dkr.ecr.${ecrregion}.amazonaws.com/jenkins:jnlp-docker")
  jnlpDockerTemplate.setAlwaysPullImage(false)
  jnlpDockerTemplate.setArgs('')
  jnlpDockerTemplate.setCommand('')
  jnlpDockerTemplate.setResourceRequestCpu('500m')
  jnlpDockerTemplate.setResourceRequestMemory('1024Mi')

  podTemplate.setContainers([jnlpDockerTemplate])
  k8sCloud.addTemplate(podTemplate)

  instance.clouds.replace(k8sCloud)
  instance.save()

  def clouds = Jenkins.instance.clouds
  if (clouds) {
    println "--> Current configured clouds: ${clouds*.name}"
    println '--> Configuring Kubernetes cloud... done'
  } else {
    println '--> No clouds found or configured'
  }
}

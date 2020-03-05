import hudson.model.*
import jenkins.model.*
import hudson.tools.*
import hudson.util.Secret

def smtpuser = System.getenv('SMTP_USER')
def smtppassword = System.getenv('SMTP_PASSWORD')
def smtphost = System.getenv('SMTP_HOST')
def smtport = System.getenv('SMTP_PORT') ?: "25"

def instance = Jenkins.getInstance()
def mailer = instance.getDescriptor("hudson.tasks.Mailer")
def emailext = instance.getDescriptor("hudson.plugins.emailext.ExtendedEmailPublisher")

Thread.start {
  sleep 3000
  println '--> Configuring smtp'

  mailer.setSmtpHost(smtphost)
  mailer.setSmtpPort(smtport)
  mailer.setSmtpAuth(smtpuser, smtppassword)
  mailer.setUseSsl(true)
  mailer.setReplyToAddress("timur_galeev@outlook.com")
  mailer.setCharset("UTF-8")

  emailext.smtpUsername = smtpuser
  emailext.smtpPassword = Secret.fromString(smtppassword)
  emailext.smtpServer = smtphost
  emailext.smtpPort = smtport
  emailext.useSsl = true
  emailext.charset = "UTF-8"
  emailext.defaultContentType = "text/html"
  emailext.defaultReplyTo = "timur_galeev@outlook.com"
  emailext.recipientList = "aksissound@gmail.com"

  instance.save()
  println '--> Configuring smtp... done'
}

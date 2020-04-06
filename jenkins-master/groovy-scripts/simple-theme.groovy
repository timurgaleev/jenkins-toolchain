import jenkins.model.Jenkins
import org.jenkinsci.plugins.simpletheme.CssUrlThemeElement

Jenkins jenkins = Jenkins.get()

Thread.start {
  sleep 5000

  println '--> Configuring simple-theme'

  def themeDecorator = jenkins.getExtensionList(org.codefirst.SimpleThemeDecorator.class).first()

  themeDecorator.setElements([
    new CssUrlThemeElement(System.getenv('JENKINS_THEME_PATH'))
  ])

  themeDecorator.save()
  jenkins.save()
  println '--> Configuring simple-theme... done'
}

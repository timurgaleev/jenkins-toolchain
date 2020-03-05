// From https://github.com/allure-framework/allure-docs/blob/master/docs/reporting/jenkins.adoc
import ru.yandex.qatools.allure.jenkins.tools.*
import hudson.tools.InstallSourceProperty
import hudson.tools.ToolProperty
import hudson.tools.ToolPropertyDescriptor
import hudson.util.DescribableList

def isp = new InstallSourceProperty()
def autoInstaller = new AllureCommandlineInstaller("2.13.0")
isp.installers.add(autoInstaller)

def proplist = new DescribableList<ToolProperty<?>, ToolPropertyDescriptor>()
proplist.add(isp)

def installation = new AllureCommandlineInstallation("allure2130", "", proplist)
def allureDesc = jenkins.model.Jenkins.instance.getExtensionList(AllureCommandlineInstallation.DescriptorImpl.class)[0]

allureDesc.setInstallations(installation)
allureDesc.save()

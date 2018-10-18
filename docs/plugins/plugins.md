# <a id="Plugins"></a> Plugins
The examples - especially the two [primary pipelines](../pipelines/pipelines.html) - use plugins that may not be part of a standard installation of Jenkins; this will likely be the case for the Compuware plugins. All plugins described here can be obtained from the Jenkins plugin marketplace using `Manage Jenkins` / `Manage Plugins`.

## <a id="Compuware plugins"></a> Compuware plugins
Compuware provides a continously growing set of plugins that allow connecting to the mainframe and using Compuware's (and other) tools within a Jenkins job/pipeline. 

### <a id="Compuware Common Configuration"></a> Compuware Common Configuration
The [Compuware Common Configuration](https://wiki.jenkins.io/display/JENKINS/Compuware+Common+Configuration+Plugin) plugin allows defining and storing configuration settings that are used by and shared between the other Compuware plugins.

### <a id="Compuware ISPW Operations Plugin"></a> Compuware ISPW Operations Plugin
The [Compuware ISPW Operations Plugin](https://wiki.jenkins.io/display/JENKINS/Compuware+ISPW+Operations+Plugin) allows using ISPW REST API operations without having to code native http requests

### <a id="Compuware Source Code Download for Endevor, PDS, and ISPW"></a> Compuware Source Code Download for Endevor, PDS, and ISPW
The [Compuware Source Code Download for Endevor, PDS, and ISPW](https://wiki.jenkins-ci.org/display/JENKINS/Compuware+Source+Code+Download+for+Endevor%2C+PDS%2C+and+ISPW+Plugin) plugin allows downloading source code and other assets stored in mainframe SCM tools:
- ISPW
- Endevor
- Plain PDS

### <a id="Compuware Topaz for Total Test"></a> Compuware Topaz for Total Test
The [Compuware Topaz for Total Test](https://wiki.jenkins-ci.org/display/JENKINS/Compuware+Topaz+for+Total+Test+Plugin) plugin allows execution of Topaz for Total Test unit test scenarios and suites and retrieving of the results. The results will be downloaded and stored in `html` format as well as `xml` format for further use by SonarQube. Collection of Xpediter Code Coverage data can be triggered alongside the execution of the unit tests.

### <a id="Compuware Xpediter Code Coverage"></a> Compuware Xpediter Code Coverage
The [Compuware Xpediter Code Coverage](https://wiki.jenkins-ci.org/display/JENKINS/Compuware+Xpediter+Code+Coverage+Plugin) plugin allows querying an Xpediter Code Coverage repository and downloading the results. These results will be stored in `xml` format for further use by SonarQube.

### <a id="Compuware Topaz Utilities"></a> Compuware Topaz Utilities
The [Compuware Topaz Utilities](https://wiki.jenkins-ci.org/display/JENKINS/Compuware+Topaz+Utilities+Plugin) plugin will provide a collection of utilities around interfacing to and using tools on mainframes. In its first version it allows execution and checking return codes of JCL. The JCL may be stored in PDS members on the mainframe or in string variables during execution.

## <a id="Third party plugins"></a> Third party plugins
Other plugins that are used by the examples are:

### <a id="Pipeline Utility Steps"></a> Pipeline Utility Steps
The [Pipeline Utility Steps](https://wiki.jenkins.io/display/JENKINS/Pipeline+Utility+Steps+Plugin) plugin provides a set of script methhods that are being used in the examples like
- `findFiles`   to search for files by name pattern
- `zip`         to create zip archives
- `unzip`       to unzip archives

### <a id="Config File Provider"></a> Config File Provider
The [Config File Provider](https://wiki.jenkins.io/display/JENKINS/Config+File+Provider+Plugin) allows defining files and storing their content within Jenkins thus allowing to define configuration files that do not have to be stored on disk within e.g. the Git repository storing the `jenkinsfile`.

### <a id="Credentials Binding Plugin"></a> Credentials Binding Plugin
Some plugins/methods like the `httpRequest` require the use of plain text credentials or tokens rather than using credential IDs as provided by the Jenkins Crendentials manager. The [Credentials Binding Plugin]() allows converting a Jenkins credentials ID into a variable containing the plain text stored in the credential definition. Thus, these plugins can be used without having to expose any plain *secret* texts within the code of the scripts.
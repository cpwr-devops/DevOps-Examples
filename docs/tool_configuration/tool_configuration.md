---
title: Tool Configurations
layout: tool_configuration
---
# <a id="Tool Configurations"></a> Tool Configurations
The scenarios covered by the [primary pipelines](../pipelines/pipelines.html), the [other code examples](../code_examples/code_examples.html), and more generally the use of the Compuware and third party [plugins](../plugins/plugins.html), require certain configurations and setting, both within Jenkins and within other tools provided by Compuware.

## <a id="Compuware Enterprise Services"></a> Compuware Enterprise Services
Next to serving as web application server for Compuware's web based tools like [iStrobe](https://compuware.com/strobe-mainframe-performance-monitoring/) or the [ISPW web interface](https://compuware.com/ispw-source-code-management/), Compuware Enterprise Services (CES) provides a set of services that allow interfacing with Compuware tools outside the mainframe. In the context of Jenkins and CI/CD these are

### <a id="ISPW Webhooks"></a> ISPW Webhooks
ISPW allows registering webhooks to use events in the ISPW software life cycle to trigger events outside of ISPW like triggering a Jenkins pipeline. CES may be used as one source to define and register webhooks for ISPW. Both *primary* pipelines make use of such webhooks defined in CES. Refer to the CES online help, chapter **Compuware Enterprise Services**, section **Webhooks** for documentation on setting up ISPW webhooks, and the parameters that may be passed from ISPW to the webhook.

[Definition of a webhook used in the examples](./webhook_setup.html)

### <a id="CES Credentials Token"></a> CES Credentials Token
The Compuware plugins (and other operations) make use of credential tokens defined in CES. These tokens store mainfram TSO user id and password and allow authentication against the mainframe without using clear text credentials within script code. 

[Definition of CES credentials token](./CES_credentials_token.html)

## <a id="Jenkins"></a> Jenkins
The plugins used by the examples require additional setup like server locations/URLs or additional credentials to use. We describe the required steps by "location" in the Jenkins UI.

### <a id="Configure system"></a> Manage Jenkins / Configure system
The settings under `Manage Jenkins` -> `Configure System` will be required for the definition of at least
- [SonarQube server information](./SonarQube_server.html) to store the SonarQube server URL under a reference name
- [Compuware Configurations](./Compuware_configurations.html) to define settings shared by the Compuware plugins
- [Global Pipeline Libraries](./Pipeline_libraries_config.html)
- [XL Release](./XLR_config.html)

### <a id="Global Tool Configuration"></a> Manage Jenkins / Global Tool Configuration
The settings under `Manage Jenkins` -> `Global Tool Configuration` will be required for the definition of at least
- [Git](./Jenkins_Git_config.html) to point to the installation of Git local to the Jenkins server. The Jenkins server requires its own `git.exe`.
- [SonarQube Scanner](./SonarQube_scanner.html) to define a reference to the path to the Sonar Scanner installation local to the Jenkins server

### <a id="Credentials"></a> Manage Jenkins / Credentials
Use the credentials manager to store the following credentials for use in the examples

- A user ID / password token for a valid logon to the required mainframe LPAR used by plugins that do not use the CES credentials token (used as parameter `HCI_Token`)
- A secret text credential token to mask the [CES credentials ID created and retreieved from CES](CES_credentials_token.html) (used as parameter `CES_Token`)
- A user ID / password token for a valid logon to the GitHub repository storing Topaz for Total Test unit test assets (used as parameter `Git_Credentials`)

![Jenkins Credentials](./images/Jenkins credentials.png)

### <a id="Managed Files"></a> Manage Jenkins / Managed Files
The option `Manage Jenkins` -> `Managed Files` will be available after installation of the [Config File Provider](https://wiki.jenkins.io/display/JENKINS/Config+File+Provider+Plugin) plugin. The examples make use of configuration files handled and stored by this plugin. Especially this will be a list of TSO user IDs and [corresponding mail addresses](./Config_Files.html). Over time other configuration files will use the same technology.
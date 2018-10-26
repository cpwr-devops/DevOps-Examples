---
layout: config_files
---
# <a id="Config Files"></a> Configuration Files
The [Mainframe_CI_Pipeline_from_Shared_Lib](../pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.html#Mainframe_CI_Pipeline_from_Shared_Lib) uses two external files, storing configuration on the environment it is running in. There are two files stored externally (in a GitHub repository, in folder `(root)/config/pipeline`). The first two files will get downloaded from the GitHub repo and read during initialization of the `PipelineConfig` class. For a detailed description of the parameters refer to [the pipeline parameters](../pipeline/pipeline_parameters.html#The pipeline parameters).

The values are stored as `parameter=value` pairs in records. Each record contains one pair. The parameter names must remain as they are. The values are processed a entered (trailing blanks will be ignored).

One file containing [email adresses](../tool_configuration/Config_Files.html#The email list) that is controlled via the [Config File Provider](https://wiki.jenkins.io/display/JENKINS/Config+File+Provider+Plugin) plugin.

## <a id="pipeline.config"></a> pipeline.config
The `pipeline.config` file contains configuration settings about the 'environment' the Jenkins pipeline is executing in/for, i.e. URLs of servers like SonarQube or XL Release and others. 

## <a id="tttgit.config"></a> tttgit.config
The example scenarios use GitHub to store Topaz for Total Test unit test projects. The `tttgit.config` file stores information on which branch of the repository to use.
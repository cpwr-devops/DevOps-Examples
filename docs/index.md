# DevOps-Examples
This repostitory is dedicated to providing information and working examples for anyone who is looking for information on how to integrate mainframe development into CI/CD pipelines, specifically Jenkins pipelines to start with. 

The pages will contain example code and documentation on
- whole Jenkins pipelines as we would suggest as starting point
- code snippets for specific tasks and purposes outside the general purpose
- using the Compuware plugins to integrate mainframe development
- using third party plugins
- setting up Jenkins and Jenkins plugins being used

Some pieces of code already show how to use the underlying APIs rather than the Jenkins specific plugins, and we intend to expand to other CI/CD tools as the need arises. 

## People wanting to contribute
Everyone perusing these pages is welcome to provide feedback, input and suggestions for improvement; as well as asking for specific topics to be covered in the future.

## The repository structure and content

### Primary examples
Currently, we have published two examples of "complete" pipelines which show partly different process steps and different techniques in Jenkins. 
- [Mainframe-CI-Example-pipeline.jenkinsfile](./Mainframe-CI-Example-pipeline.md) - ([code](https://github.com/cpwr-devops/DevOps-Examples/blob/suggest/Jenkinsfile/Mainframe-CI-Example-pipeline.jenkinsfile)) - is a scripted pipeline using parameters
- [Mainframe_CI_Pipeline_from_Shared_Lib.groovy](./Mainframe_CI_Pipeline_from_Shared_Lib.md) - ([code](https://github.com/cpwr-devops/DevOps-Examples/blob/suggest/vars/Mainframe_CI_Pipeline_from_Shared_Lib.groovy)) - is a pipeline loaded from a Jenkins shared library.

Currently, both examples use a development scenario based on
- [ISPW](https://compuware.com/ispw-source-code-management/) as SCM to store and govern mainframe sources
- [Git (GitHub)](https://github.com/) as SCM to store unit test assets
- [Topaz for Total Test](https://compuware.com/topaz-for-total-test-automation/) as mainframe unit testing tool to create and maintain unit test assets
- [Xpediter Code Coverage](https://compuware.com/xpediter-mainframe-debugging-tools/) as tool to gather code coverage data during execution of the unit tests
- [SonarQube](https://www.sonarsource.com/) as server for code analysis and setting up quality gates
- [XLRelease](https://xebialabs.com/) as CD server for release steps following the initial CI process Jenkins

### Other code examples
Code snippets and examples related to Jenkins/Groovy will be stored alongside the *Mainframe-CI-Example-pipeline* in the [Jenkinsfile folder](https://github.com/cpwr-devops/DevOps-Examples/tree/suggest/Jenkinsfile) of the repository.

Code snippets and examples not directly related are stored in separate folders in the root directory of the repository. Currently these are
- [ISPW-REST-API-Examples](https://github.com/cpwr-devops/DevOps-Examples/tree/suggest/ISPW-REST-API-Examples) containing a Windows powershell script that demonstrates the use of ISPW's REST APIs. This code may be used a starting point if Jenkins is not the CI server of choice.

### The complete folder structure
Based on the descritpion above and due to the requirements for the use of [Pipeline Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) in Jenkins the folder structure of the DevOps-Examples repository is as follows:

    (root)
    +- src                                                  # Groovy source files 
    |   +- com
    |       +- compuware
    |           +- devops
    |               +- util                                 # classes used by the pipelines
    |
    +- vars
    |   +- Mainframe_CI_Pipeline_from_Shared_Lib.groovy     # primary example pipeline (Shared Library)
    |
    +- config                                               # configuration and other files used by the pipelines
    |   +- pipeline                                          
    |       +- pipeline.config                              # environment specific configuration
    |       +- tttgit.config                                # configuration for the GitHub repository storing unit test assets
    |   +- skels                                            # mainframe JCL "skeleton" files
    |
    +- Jenkinsfile                                          # scripted pipeline code and groovy example code
    |   +- Mainframe-CI-Example-pipeline.jenkinsfile        # primary example pipeline (Scripted Pipeline)
    |
    +- ISPW-REST-API-Examples                               # Code examples using the ISPW REST API
    |   +- ISPW_Operations.ps1                              # Windows powershell script as wrapper for all available ISPW API calls
    |
    +- docs                                                 # GitHub pages

## Next pages

- [Plugins](./plugins/plugins.md) 
for a list and description of "non standard" plugins that are used within the examples

- [Tool Configurations](./tool_configuration/tool_configuration.md) 
for descriptions of setting up the different tools (inside and outside of Jenkins) in play

- [Primary Pipelines](./pipelines/pipelines.md) 
for detailed descriptions of the *primary* pipelines

- [Code examples](./code_examples/code_examples.md) 
for descriptions of Groovy and non-Groovy code examples outside the *primary* pipelines

- [Helper classes](./helper_classes/helper_classes.md)
for descriptions of the classes in `src/com/compuware/devops/util` that are being used by the [Mainframe_CI_Pipeline_from_Shared_Lib.groovy](./Mainframe_CI_Pipeline_from_Shared_Lib.md) and other code examples.
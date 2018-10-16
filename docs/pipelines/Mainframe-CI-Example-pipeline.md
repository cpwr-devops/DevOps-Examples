# Mainframe-CI-Example-pipeline.jenkinsfile
This is an example of a [scripted pipeline](https://github.com/cpwr-devops/DevOps-Examples/blob/master/Jenkinsfile/Mainframe-CI-Example-pipeline.jenkinsfile) using Compuware's and other plugins to implement the following process and scenario

## Outside Jenkins
- A developer checks out a set of sources, copybooks and other components required to fulfill a specific requirement. These are stored and administered in [ISPW](https://compuware.com/ispw-source-code-management/)
- The developer creates or modifies a set of unit tests using [Topaz for Total Test](https://compuware.com/topaz-for-total-test-automation/). In order to share the unit tests between development teams and to use them in Jenkins, the Topaz for Total Test projects are stored and administered using Git - [GitHub](https://github.com/) in the case of these examples, to be more precise.
Naming conventions are used to correlate Topaz for Total Test unit test projects to the target programs of the tests:
    - The Git repository uses `<ISPW_Stream_Name>_<ISPW_Application_Name>_Unit_Tests` as name
    - The Topaz for Total Test project name uses `<Target_Program_Name>_Unit_Tests` as project name 
    - The test scenario files or test suite files contain the `<Target_Program_Name>` as first part of their file name, followed by an underscore `_`, follwowed by any trailing name. Topaz for Total Test itself requires the file extensions
        - `.testscenario` for test scenarios
        - `.testsuite` for test suites
- Once a mainframe developer has finished working on a set of pieces of code that are stored in a mainframe SCM -  in our examples - they promote their changes to the next level in the application's life cycle. This activity creates an ISPW set container containing all components that are part of this specific promotion.

## Inside the Jenkins pipeline
- The [ISPW webhook](../tool_configuration/webhhok_setup.md) triggers the [pipeline job](./Mainframe-CI-Example-pipeline_config.md)
- The job will 
    - download all COBOL sources and COBOL copybooks from ISPW (the mainframe) that are part of the set triggering this specific pipeline execution
    - clone the Git repository for the ISPW application, using the fixed stream name `FTSDEMO` in our examples
    - build a list of all downloaded COBOL sources
    - build a list of all downloaded Topaz for Total Test `.testscenario` files
    - match the two lists and execute all unit test scenarios that have a matching COBOL source; execution of the unit tests will collect code coverage data
    - download code coverage results from the underlying [Xpediter Code Coverage](https://compuware.com/xpediter-mainframe-debugging-tools/) repository
    - pass downloaded COBOL sources, the results of the unit tests, and code coverage metrics to SonarQube using the Sonar Scanner
    - query the resulting Sonar quality gate
    - if the quality gate passes an XL Release template will be triggered to execute CD stages beyond the Jenkins pipeline

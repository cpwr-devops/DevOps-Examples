# Pipelines
These examples of Jenkins pipelines make use of Compuware's and other plugins to implement the following process and scenario. They may be seen a kind of **standard approach** for implementing a CI process for maingframe development. The scenario(s) and code are based on request and requirements from customers who already have started implementing their own pipeline and asked Compuware for advice. The code itself shows our solution to these, while the respective solutions as implemented by our customers are - in parts considerably - different.

The code reflects common patterns that we see emerging at different customers. In addition, the examples are supposed to help mainframe developers to familiarize with [Groovy](http://groovy-lang.org/documentation.html) and its concepts, as well as demonstrate some of the [ideosynchrasies of the Jenkins Groovy dialect](./Jenkins_Groovy_Ideosynchrasies.md) that we stumbled across.

## Scenario Outside Jenkins
- A developer checks out a set of sources, copybooks and other components required to fulfill a specific requirement. These are stored and administered in [ISPW](https://compuware.com/ispw-source-code-management/)
- The developer creates or modifies a set of unit tests using [Topaz for Total Test](https://compuware.com/topaz-for-total-test-automation/). In order to share the unit tests between development teams and to use them in Jenkins, the Topaz for Total Test projects are stored and administered using Git - [GitHub](https://github.com/) in the case of these examples, to be more precise.
Naming conventions are used to correlate Topaz for Total Test unit test projects to the target programs of the tests:
    - The Git repository uses `<ISPW_Stream_Name>_<ISPW_Application_Name>_Unit_Tests` as name
    - The Topaz for Total Test project name uses `<Target_Program_Name>_Unit_Tests` as project name 
    - The test scenario files or test suite files contain the `<Target_Program_Name>` as first part of their file name, followed by an underscore `_`, follwowed by any trailing name. Topaz for Total Test itself requires the file extensions
        - `.testscenario` for test scenarios
        - `.testsuite` for test suites
- Once a mainframe developer has finished working on a set of pieces of code that are stored in a mainframe SCM -  in our examples - they promote their changes to the next level in the application's life cycle. This activity creates an ISPW set container containing all components that are part of this specific promotion.

## Inside Jenkins - the two primary pipelines
The [ISPW webhooks](../tool_configuration/webhhok_setup.md) will then trigger one of the following pipeline jobs
- [Mainframe-CI-Example-pipeline_config.jenkinsfile](./Mainframe-CI-Example-pipeline.md)
- [Mainframe_CI_Pipeline_from_Shared_Lib.groovy](./pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.md)
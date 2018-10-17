# <a id="Topaz for Total Test Scenario"></a> Topaz for Total Test Scenario

## <a id="Initial recording of test cases"></a> Initial recording of test cases

## <a id="Naming conventions"></a> Naming conventions
Naming conventions are used to correlate Topaz for Total Test unit test projects to the target programs of the tests:
- The Git repository uses `<ISPW_Stream_Name>_<ISPW_Application_Name>_Unit_Tests` as name
- The Topaz for Total Test project name uses `<Target_Program_Name>_Unit_Tests` as project name 
- The test scenario files or test suite files contain the `<Target_Program_Name>` as first part of their file name, followed by an underscore `_`, follwowed by any trailing name. 

Topaz for Total Test itself requires the file extensions
- `.testscenario` for test scenarios
- `.testsuite` for test suites

## <a id="The Topaz for Total Test project structure"></a> The Topaz for Total Test project structure

## <a id="The runner.jcl"></a> The runner.jcl
Due to the design of the underlying application, there are three paths through the development stages. Therefore, there will be three different `STEPLIB` concatenations to use in the Topaz for Total Test `runner.jcl`. The approach taken in the example is to have three different versions of the `runner.jcl` each using a different `STEPLIB` concatenation. The code of the pipeline will determine which `jcl` to use.
- `Runner_PATH1.jcl` is used for the path from `DEV1` to `PRD`
- `Runner_PATH2.jcl` is used for the path from `DEV2` to `PRD`
- `Runner_PATH3.jcl` is used for the path from `DEV3` to `PRD`

![Life Cylce](../tool_configuration/images/Example life cycle.png)

## <a id="Adding new test cases"></a> Adding new test cases

## <a id="Storing the Topaz for Total Test project in Git/GitHub"></a> Storing the Topaz for Total Test project in Git/GitHub
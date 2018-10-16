# Mainframe-CI-Example-pipeline.jenkinsfile
Once this pipeline has been triggered, the [job](https://github.com/cpwr-devops/DevOps-Examples/blob/suggest/Jenkinsfile/Mainframe-CI-Example-pipeline.jenkinsfile) will
- download all COBOL sources and COBOL copybooks from ISPW (the mainframe) that are part of the set triggering this specific pipeline execution
- clone the Git repository for the ISPW application, using the fixed stream name `FTSDEMO` in our examples
- build a list of all downloaded COBOL sources
- build a list of all downloaded Topaz for Total Test `.testscenario` files
- match the two lists and execute all unit test scenarios that have a matching COBOL source; execution of the unit tests will collect code coverage data
- download code coverage results from the underlying [Xpediter Code Coverage](https://compuware.com/xpediter-mainframe-debugging-tools/) repository
- pass downloaded COBOL sources, the results of the unit tests, and code coverage metrics to SonarQube using the Sonar Scanner
- query the resulting Sonar quality gate
- if the quality gate passes an XL Release template will be triggered to execute CD stages beyond the Jenkins pipeline

## Setting up the pipeline job
The job itself is defined via the usual way of creating a new pipeline job. It is important, though, to make sure that the resulting job uses parameters by checking the `This project is parameterized' box, 

![Parameterized Pipeline](./images/parametertized pipeline.png)

and succesively adding the following string parameters (the default values are the ones used for the examples).

![Adding parameters](./images/Adding parameters.png)

The parameters in this first set are specific to the individual execution of the pipeline
<table>
    <tr>
        <td>**Name**</td>
        <td>**Default value**</td>
        <td>**Description**</td>
    </tr>
    <tr>
        <td>ISPW_Stream</td>
        <td>FTSDEMO</td>
        <td>ISPW Stream Name</td>
    </tr>
    <tr>
        <td>ISPW_Application</td>
        <td>RXN3</td>
        <td>ISPW Application</td>
    </tr>
    <tr>
        <td>ISPW_Src_Level</td>
        <td>DEV1</td>
        <td>ISPW Level the promote has been started from</td>
    </tr>
    <tr>
        <td>ISPW_Release</td>
        <td></td>
        <td>ISPW Release Name</td>
    </tr>
    <tr>
        <td>ISPW_Container</td>
        <td></td>
        <td>ISPW Set ID</td>
    </tr>
    <tr>
        <td>ISPW_Container_Type</td>
        <td>2</td>
        <td>ISPW Container Type
    0 - assignment
    1 - release
    2 - set
        </td>
    </tr>
    <tr>
        <td>ISPW_Owner</td>
        <td></td>
        <td>ISPW Owner User ID</td>
    </tr>
</table>

The second set of parameters is installation specific and reference tokens and other IDs that have been defined during the configuration phase
<table>
    <tr>
        <td>**Name**</td>
        <td>**Default value**</td>
        <td>**Description**</td>
    </tr>
    <tr>
        <td>CES_Token</td>
        <td></td>
        <td>Jenkins internal Token ID for the CES Token</td>
    </tr>
    <tr>
        <td>HCI_Conn_ID</td>
        <td></td>
        <td>Jenkins internal ID for HCI Connection</td>
    </tr>
    <tr>
        <td>HCI_Token</td>
        <td></td>
        <td>Jenkins internal ID for HCI Token</td>
    </tr>
    <tr>
        <td>CC_repository</td>
        <td></td>
        <td>Code Coverage Repository - Check with your Xpedietr Code Coverage administrator for the name to use</td>
    </tr>
    <tr>
        <td>Git_Project</td>
        <td></td>
        <td>Github (or other Git based repository) project used to store the Topaz for Total Test Projects</td>
    </tr>
</table>

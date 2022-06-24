#!/usr/bin/env groovy
import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import jenkins.plugins.http_request.*
import java.net.URL

/**
 This is an example Jenkins Pipeline Script that runs a CI process against COBOL Code using Jenkins Shared Libraries.  
 As the basic example, this pipeline is designed to be triggered from ISPW 
 on the promotion of code from a Test level in a controlled level.  The pipeline runs a series of quality checks on the 
 promoted code to ensure that it meets the quality standards that an organization defined in SonarQube.
 
 This Pipeline uses the following Jenkins Plugins
 Compuware Common Configuration Plugin - https://plugins.jenkins.io/compuware-common-configuration
 Compuware Source Code Download for Endevor, PDS, and ISPW Plugin - https://plugins.jenkins.io/compuware-scm-downloader
 Compuware Topaz for Total Test Plugin - https://plugins.jenkins.io/compuware-topaz-for-total-test
 Compuware Xpediter Code Coverage Plugin - https://plugins.jenkins.io/compuware-xpediter-code-coverage
 Pipeline Utilities Plugin - https://plugins.jenkins.io/pipeline-utility-steps
 SonarQube Scanner Plugin - https://plugins.jenkins.io/sonar
 XebiaLabs XL Release Plugin - https://plugins.jenkins.io/xlrelease-plugin
 
 This Pipeline Requires the below Parameters to be defined in the Jenkins Job
 The Jenkins Parameters can be supplied by a ISPW webhook by defining a webhook like the example below.  

 The Jenkins Parameters can be supplied by a ISPW webhook by defining a webhook like the example below.  
 
 http://<<your jenkins server>>/job/<<your jenkins job>>/buildWithParameters??ISPW_Stream=$$stream$$&ISPW_Application=$$application$$&ISPW_Release=$$release$$&ISPW_Assignment=$$assignment$$&ISPW_Set_Id=$$setID$$&ISPW_Src_Level=$$level$$&ISPW_Owner=$$owner$$&ISPW_Event=$$event$$&ISPW_Operation=$$operation$$

 ISPW Webhook Parameter List, these parameters need to be defined in the Jenkins job configuration and will be passed by the ISPW Webhook
 @param ISPW_Stream         - ISPW Stream that had the code promotion
 @param ISPW_Application    - ISPW Application that had the code promotion
 @param ISPW_Release        - The ISPW Release Value that will be passed to XL Release
 @param ISPW_Assignment     - The ISPW Assignemnt that has been promoted 
 @param ISPW_Src_Level      - ISPW Level that code was promoted from
 @param ISPW_Owner          - The ISPW Owner value from the ISPW Set that was created for the promotion

 The script or jenkinsfile defined in the job configuration needs to call this pipeline and pass the parameters above in a Map:

 ispwStream:        ISPW_Stream,
 ispwApplication:   ISPW_Application,
 ispwAssignment:    ISPW_Assignment,
 ispwRelease:       ISPW_Release,
 ispwSrcLevel:      ISPW_Src_Level,
 ispwOwner:         ISPW_Owner

 In addition to the parameters passed via Webhook, the pipeline also takes the following parameters from the call, which need to extend the map. 
 These parameters may differ between differennt applications/instances of the job implemented by the pipeline.
 cesToken:          <CES_Token>,            CES Personal Access Token.  These are configured in Compuware Enterprise Services / Security / Personal Access Tokens 
 jenkinsCesToken:   <Jenkins_CES_Token>,    Jenkins Credentials ID for the CES Personal Access Token
 hciConnectionId:   <HCI_Conn_ID>,          HCI Connection ID configured in the Compuware Common Configuration Plugin.  Use Pipeline Syntax Generator to determine this value. 
 hciToken:          <HCI_Token>,            The ID of the Jenkins Credential for the TSO ID that will used to execute the pipeline
 ccRepository:      <CoCo_Repository>,      The Compuware Xpediter Code Coverage Repository that the Pipeline will use
 gitProject:        <Git_Project>,          Github project/user used to store the Topaz for Total Test Projects
 gitCredentials:    <Git_Credentials>       Jenkins credentials for logging into git 
*/

/**
 In the basic example dertain parameters were hard coded into the pipeline. These would be setting that apply to any instance of the pipeline. Instad of hardcoding, 
 we make use of the Shared Library resource folder, which may store configuration files, and we read the configuration from those file. This example pipeline assumes the
 configuration stired as .yml file:

git:                        Git related 
  url:                      - URL of Git repository server
  tttRepoExtension:         - Extension of repoitory for TTT assets
  branch:                   - Branch to use
sq:                         SonarQube related
  scannerName:              - Name of SonarQube Scanner installation in "Manage Jenkins" -> "Global Tool Configuration" -> "SonarQube Scanner Installations"
  serverName:               - Name of SonarQube Server in "Manage Jenkins" -> "Configure System" -> "Sonar Qube servers"
xlr:                        XLRelease related
  template:                 - Release template to start
  user:                     - XLR user credentials to use
ttt:                        TTT related
  general:                  general settings
    folder:                     - Target folder to clone TTT repo into
    sonarResultsFolder:         - Folder containing Sonar Qube result report files    
    sonarResultsFile:           - standard name of Sonar results file     
  virtualized:              Settings for Virtualized Tests
    folder:                     - Folder container Virtualized Tests
    envirnment:                 - Environemnt ID from CES/TTT repository to use 
    targetSonarResults:         - if Total Test CLI is execute dmore than once, the Sonar results files need to be renamed into this name for Virtualized Tests
  nonVirtualized:           Settings for Non Virtualized Tests
    folder:                     - Folder container Non Virtualized Tests  
    environment:                - Environemnt ID from CES/TTT repository to use 
    targetSonarResults:         - if Total Test CLI is execute dmore than once, the Sonar results files need to be renamed into this name for Non Virtualized Tests
ces:                        CES related
  url:                      - URL
ispw:                       ISPW related
  runtime:                  - Runtime configuration
  changedProgramsFile:      - Json file containing list of compnents affected by an ISPW operation. Will be generated by ISPW plugins, automatically. 
  mfSourceFolder:           - directory that contains cobol source downloaded from ISPW

*/

String  configFile
String  mailListFile

/**
Call method to execute the pipeline from a shared library
@param pipelineParams - Map of paramter/value pairs
*/
def call(Map pipelineParams)
{
    configFile                  = 'pipelineConfig.yml'
    mailListFile                = 'mailList.yml'

    //*********************************************************************************
    // Read pipelineConfig.yml and mailList.yml from Shared Library resources folder as
    // yaml document.
    //*********************************************************************************
    def pipelineConfig          = readYaml(text: libraryResource(configFile))
    def mailList                = readYaml(text: libraryResource(mailListFile))

    // Determine the current ISPW Path and Level that the code Promotion is from
    def pathNum                 = pipelineParams.ispwSrcLevel.charAt(pipelineParams.ispwSrcLevel.length() - 1)
    
    // Also set the Level that the code currently resides in
    def ispwTargetLevel         = "QA" + pathNum

    def mailRecipient           = mailList[(pipelineParams.ispwOwner.toUpperCase())]
    def mailMessageExtension    = ''

    def ccDdioOverride          = "SALESSUP.${pipelineParams.ispwApplication}.${pipelineParams.ispwSrcLevel}.LOAD.SSD"

    node
    {
        // Clean out any previously downloaded source
        dir("./") 
        {
            deleteDir()
        }

        /*
        This stage is used to retrieve source from ISPW
        */ 
        stage("Retrieve Code From ISPW")
        {
            checkout(
                [
                    $class:             'IspwContainerConfiguration', 
                    connectionId:       "${pipelineParams.hciConnectionId}",
                    credentialsId:      "${pipelineParams.hciToken}", 
                    componentType:      '', 
                    containerName:      pipelineParams.ispwAssignment, 
                    containerType:      '0', // 0 Assignment, 1 Release, 2 Set, 
                    ispwDownloadAll:    false, 
                    ispwDownloadIncl:   true, 
                    serverConfig:       '', 
                    serverLevel:        ispwTargetLevel
                ]
            )
        }
        
        /* 
        This stage is used to retrieve Topaz for Total Tests from Git
        */ 
        stage("Retrieve Tests")
        {
            echo "Checking out Branch " + pipelineConfig.git.branch

            //Retrieve the Tests from Github that match that ISPW Stream and Application
            def gitFullUrl = "${pipelineConfig.git.url}/${pipelineParams.gitProject}/${pipelineParams.ispwStream}_${pipelineParams.ispwApplication}${pipelineConfig.git.tttRepoExtension}"

            checkout(
                changelog:  false, 
                poll:       false, 
                scm:        [
                    $class:                             'GitSCM', 
                    branches:                           [[
                        name: "*/${pipelineConfig.git.branch}"
                        ]], 
                    doGenerateSubmoduleConfigurations:  false, 
                    extensions:                         [[
                        $class:             'RelativeTargetDirectory', 
                        relativeTargetDir:  "${pipelineConfig.ttt.general.folder}"
                    ]], 
                    submoduleCfg:                       [], 
                    userRemoteConfigs:                  [[
                        credentialsId:  "${pipelineParams.gitCredentials}", 
                        name:           'origin', 
                        url:            "${gitFullUrl}"
                    ]]
                ]
            )

        }

        stage("Execute related Non Virtualized Tests")
        {

            totaltest(
                serverUrl:                          pipelineConfig.ces.url, 
                serverCredentialsId:                pipelineParams.hciToken, 
                credentialsId:                      pipelineParams.hciToken, 
                environmentId:                      pipelineConfig.ttt.nonVirtualized.environment,
                localConfig:                        false,              
                folderPath:                         pipelineConfig.ttt.general.folder + '/' + pipelineConfig.ttt.nonVirtualized.folder, 
                recursive:                          true, 
                selectProgramsOption:               false, 
                haltPipelineOnFailure:              false,                 
                stopIfTestFailsOrThresholdReached:  false,
                createJUnitReport:                  true, 
                createReport:                       true, 
                createResult:                       true, 
                createSonarReport:                  true,
                collectCodeCoverage:                true,
                collectCCRepository:                pipelineParams.ccRepository,
                collectCCSystem:                    pipelineParams.ispwApplication,
                collectCCTestID:                    BUILD_NUMBER,
                clearCodeCoverage:                  false,
                logLevel:                           'INFO'
            )

            // Process the Total Test Junit result files into Jenkins
            junit allowEmptyResults: true, keepLongStdio: true, testResults: "TTTUnit/*.xml"
        }

        /* 
        This stage retrieve Code Coverage metrics from Xpediter Code Coverage for the test executed in the Pipeline
        */ 
        stage("Collect Coverage Metrics")
        {
            // Code Coverage needs to match the code coverage metrics back to the source code in order for them to be loaded in SonarQube
            // The source variable is the location of the source that was downloaded from ISPW
            def String ccSources="${pipelineParams.ispwApplication}/${pipelineConfig.ispw.mfSourceFolder}"

            // The Code Coverage Plugin passes it's primary configuration in the string or a file
            def ccproperties = 'cc.sources=' + ccSources + '\rcc.repos=' + pipelineParams.ccRepository + '\rcc.system=' + pipelineParams.ispwApplication  + '\rcc.test=' + BUILD_NUMBER + '\rcc.ddio.overrides=' + ccDdioOverride

            step(
                [
                    $class:                 'CodeCoverageBuilder',                    
                    connectionId:           pipelineParams.hciConnectionId, 
                    credentialsId:          pipelineParams.hciToken,
                    analysisProperties:     ccproperties    // Pass in the analysisProperties as a string
                ]
            )
        }

        /* 
        This stage pushes the Source Code, Test Metrics and Coverage metrics into SonarQube and then checks the status of the SonarQube Quality Gate.  
        If the SonarQube quality date fails, the Pipeline fails and stops
        */ 
        stage("Check SonarQube Quality Gate") 
        {
            // Retrieve the location of the SonarQube Scanner bin files  
            def scannerHome = tool pipelineConfig.sq.scannerName

            withSonarQubeEnv(pipelineConfig.sq.serverName)       // Name of the SonarQube server defined in Jenkins / Configure Systems / SonarQube server section
            {
                // Call the SonarQube Scanner with properties defined above
                bat "${scannerHome}/bin/sonar-scanner "                                                                         + 
                // Folder containing test definitions, i.e. TTT scenarios
                    " -Dsonar.tests=${pipelineConfig.ttt.general.folder}"                                                       +
                // File (or list of files) containing test results in Sonar format                    
                    " -Dsonar.testExecutionReportPaths=${pipelineConfig.ttt.general.sonarResultsFolder}/${pipelineConfig.ttt.general.sonarResultsFile}"                          +
                // File containing Code Coverage results in Sonar format
                    " -Dsonar.coverageReportPaths=Coverage/CodeCoverage.xml"                                                    +
                // Sonar project key to use/create
                    " -Dsonar.projectKey=${JOB_NAME}"                                                                           +
                // Sonar project name to use/create
                    " -Dsonar.projectName=${JOB_NAME}"                                                                          +
                    " -Dsonar.projectVersion=1.0"                                                                               +
                // Folder containing the (mainframe) sources to analyze
                    " -Dsonar.sources=${pipelineParams.ispwApplication}/${pipelineConfig.ispw.mfSourceFolder}"                  +
                // Folder containing the (mainframe) copybooks
                    " -Dsonar.cobol.copy.directories=${pipelineParams.ispwApplication}/${pipelineConfig.ispw.mfSourceFolder}"   + 
                // File extensions Sonar is supposed to recognize for "sources". The list also needs to contain any TTT related extensions                    
                    " -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,stub,results,scenario"                             +
                // File extensions Sonar is supposed to recognize for "copybooks"                   
                    " -Dsonar.cobol.copy.suffixes=cpy"                                                                          +
                    " -Dsonar.sourceEncoding=UTF-8"
            }
            
            // Wait for the results of the SonarQube Quality Gate
            timeout(time: 2, unit: 'MINUTES') {
                
                // Wait for webhook call back from SonarQube.  SonarQube webhook for callback to Jenkins must be configured on the SonarQube server.
                def qg = waitForQualityGate()

                // Evaluate the status of the Quality Gate
                if (qg.status != 'OK')
                {

                    mailMessageExtension        = "Promoted code failed the Quality gate. Assignent will be regressed. Review Logs and apply corrections as indicated."

                    echo "Regress Assignment ${pipelineParams.ispwAssignment}, Level ${ispwTargetLevel}"

                    ispwOperation(
                        connectionId:           pipelineParams.hciConnectionId, 
                        credentialsId:          pipelineParams.jenkinsCesToken,
                        consoleLogResponseBody: true,  
                        ispwAction:             'RegressAssignment', 
                        ispwRequestBody:        """
                            runtimeConfiguration=${pipelineConfig.ispw.runtime}
                            assignmentId=${pipelineParams.ispwAssignment}
                            level=${ispwTargetLevel}
                            """
                    )

                    currentBuild.result  = 'FAILURE'

                    emailext(
                        subject:    '$DEFAULT_SUBJECT',
                        body:       '$DEFAULT_CONTENT \n' + mailMessageExtension,
                        replyTo:    '$DEFAULT_REPLYTO',
                        to:         "${mailRecipient}"
                    )

                    error "Sonar quality gate failure: ${qg.status}"
                }
                else
                {

                    mailMessageExtension    = "Generated code passed the Quality gate. XL Release will be started."

                    emailext(
                        subject:    '$DEFAULT_SUBJECT',
                        body:       '$DEFAULT_CONTENT \n' + mailMessageExtension,
                        replyTo:    '$DEFAULT_REPLYTO',
                        to:         "${mailRecipient}"
                    )                   
                }   
            }   
        }

        /* 
        This stage triggers a XL Release Pipeline that will move code into the high levels in the ISPW Lifecycle  
        */ 
        stage("Start release in XL Release")
        {
            // Trigger XL Release Jenkins Plugin to kickoff a Release
            xlrCreateRelease(
                releaseTitle:       'A Release for $BUILD_TAG',
                serverCredentials:  "${pipelineConfig.xlr.user}",
                startRelease:       true,
                template:           "${pipelineConfig.xlr.template}",
                variables:          [
                    [propertyName: 'ISPW_Dev_level',    propertyValue: "${ispwTargetLevel}"],
                    [propertyName: 'ISPW_RELEASE_ID',   propertyValue: "${pipelineParams.ispwRelease}"],
                    [propertyName: 'CES_Token',         propertyValue: "${pipelineParams.cesToken}"]
                ]
            )
        }
    }
}
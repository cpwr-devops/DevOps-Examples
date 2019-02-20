#!/usr/bin/env groovy
import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import jenkins.plugins.http_request.*
import java.net.URL
import com.compuware.devops.util.*

/**
 Helper Methods for the Pipeline Script
*/
PipelineConfig  pConfig
GitHelper       gitHelper
IspwHelper      ispwHelper
TttHelper       tttHelper
SonarHelper     sonarHelper 

String          mailMessageExtension

def initialize(pipelineParams)
{
    def mailListlines
    /* Read list of mailaddresses from "private" Config File */
    /* The configFileProvider creates a temporary file on disk and returns its path as variable */
    configFileProvider(
        [
            configFile(
                fileId: 'MailList', 
                variable: 'mailListFilePath'
            )
        ]
    ) 
    {
        File mailConfigFile = new File(mailListFilePath)

        if(!mailConfigFile.exists())
        {
            steps.error "File - ${mailListFilePath} - not found! \n Aborting Pipeline"
        }

        mailListlines = mailConfigFile.readLines()
    }

    pConfig     = new   PipelineConfig(
                            steps, 
                            workspace,
                            pipelineParams,
                            mailListlines
                        )

    pConfig.initialize()                                            

    gitHelper   = new   GitHelper(
                            steps
                        )

    ispwHelper  = new   IspwHelper(
                            steps, 
                            pConfig
                        )

    tttHelper   = new   TttHelper(
                            this,
                            steps,
                            pConfig
                        )

    sonarHelper = new SonarHelper(this, steps, pConfig)

    sonarHelper.initialize()
}

/**
Call method to execute the pipeline from a shared library
@param pipelineParams - Map of paramter/value pairs
*/
def call(Map pipelineParams)
{
    node
    {
        stage("Initialization")
        {
            
            dir(".\\") 
            {
                deleteDir()
            }

            initialize(pipelineParams) 
        }
                
        /* Download all sources that are part of the container  */
        stage("Retrieve Mainframe Code")
        {
            ispwHelper.downloadAllSources(pConfig.ispwTargetLevel)
            ispwHelper.downloadCopyBooks(workspace)
        }
        
        /* Retrieve the Tests from Github that match that ISPW Stream and Application */
        stage("Execute Integration Tests")
        {            
            def gitUrlFullPath = "${pConfig.gitUrl}/${pConfig.gitTttFtRepo}"
            
            gitHelper.checkout(gitUrlFullPath, pConfig.gitBranch, pConfig.gitCredentials, pConfig.tttFolder)


            withCredentials(
                [usernamePassword(credentialsId: "${pConfig.hciTokenId}", usernameVariable: 'userId', passwordVariable: 'password')]
            ) 
            {
                tttHelper.initialize()
                tttHelper.executeFunctionalTests(userId, password)
            }
        }

        stage("Check SonarQube Quality Gate") 
        {
            /*sonarHelper.scan()*/
            def scannerHome     = tool "scanner"
            
            def SQ_TestResult   = '-Dsonar.testExecutionReportPaths=TestResults\\SonarTestReport.xml'

            //def TestFolder      = '"C:\\Users\\pfhsxk0\\.jenkins\\workspace\\RNU_Functional_Test\\TTT_Demo\\Functional Test"'
            def TestFolder      = '"tests\\FTSDEMO_RXN3_Functional_Tests\\Functional Test"'

            withSonarQubeEnv("localhost") 
            {
                //" -Dsonar.tests=${TestFolder} ${SQ_TestResult}"
                def SQ_Tests                = " -Dsonar.tests=${TestFolder} ${SQ_TestResult}"
                def SQ_ProjectKey           = " -Dsonar.projectKey=RNU_Functional_Tests -Dsonar.projectName=RNU_Functional_Tests -Dsonar.projectVersion=1.0"
                def SQ_Source               = " -Dsonar.sources=${pConfig.ispwApplication}\\MF_Source"
                def SQ_Copybook             = " -Dsonar.cobol.copy.directories=${pConfig.ispwApplication}\\MF_Source"
                def SQ_Cobol_conf           = " -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,stub -Dsonar.cobol.copy.suffixes=cpy -Dsonar.sourceEncoding=UTF-8"
                bat "${scannerHome}/bin/sonar-scanner" + SQ_Tests + SQ_ProjectKey + SQ_Source + SQ_Copybook + SQ_Cobol_conf
            }
            /*
            // Wait for the results of the SonarQube Quality Gate
            timeout(time: 2, unit: 'MINUTES') 
            {                
                // Wait for webhook call back from SonarQube.  SonarQube webhook for callback to Jenkins must be configured on the SonarQube server.
                def sonarGate = waitForQualityGate()
                
                // Evaluate the status of the Quality Gate
                if (sonarGate.status != 'OK')
                {
                    echo "Sonar quality gate failure: ${sonarGate.status}"
                    echo "Pipeline will be aborted and ISPW Assignment will be regressed"

                    mailMessageExtension = "Generated code failed the Quality gate. Review Logs and apply corrections as indicated."
                    currentBuild.result = "FAILURE"

                    error "Exiting Pipeline" // Exit the pipeline with an error if the SonarQube Quality Gate is failing
                }
                else
                {
                    mailMessageExtension = "Generated code passed the Quality gate and may be promoted."
                }
            } 
            */  
        }

        /* 
        This stage triggers a XL Release Pipeline that will move code into the high levels in the ISPW Lifecycle  
        */
        /* 
        stage("Send Mail")
        {
            // Send Standard Email
            emailext subject:       '$DEFAULT_SUBJECT',
                        body:       '$DEFAULT_CONTENT \n' + mailMessageExtension,
                        replyTo:    '$DEFAULT_REPLYTO',
                        to:         "${pConfig.mailRecipient}"

        } 
        */       
    }
}
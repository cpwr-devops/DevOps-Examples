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
    // Clean out any previously downloaded source
    dir(".\\") 
    {
        deleteDir()
    }

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

    withCredentials([usernamePassword(credentialsId: "${pConfig.gitCredentials}", passwordVariable: 'gitPassword', usernameVariable: 'gitUsername')]) 
    {
        gitHelper.initialize(gitPassword, gitUsername, pConfig.ispwOwner, pConfig.mailRecipient)
    }

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
            initialize(pipelineParams) 
        }
                
        /* Download all sources that are part of the container  */
        stage("Retrieve Mainframe Code")
        {
            ispwHelper.downloadSources(pConfig.ispwSrcLevel)
        }
        
        /* Retrieve the Tests from Github that match that ISPWW Stream and Application */
        stage("Execute Unit Tests")
        {            
            def gitUrlFullPath = "${pConfig.gitUrl}/${pConfig.gitTttUtRepo}"
            
            /* Check out unit tests from GitHub */
            gitHelper.checkout(gitUrlFullPath, pConfig.gitBranch, pConfig.gitCredentials, pConfig.tttFolder)

            /* initialize requires the TTT projects to be present in the Jenkins workspace, therefore it can only execute after downloading from GitHub */
            tttHelper.initialize()  

            /* Clean up Code Coverage results from previous run */
            tttHelper.cleanUpCodeCoverageResults()

            /* Execute unit tests */
            tttHelper.loopThruScenarios()
         
            tttHelper.passResultsToJunit()
        }

        /* 
        This stage retrieve Code Coverage metrics from Xpediter Code Coverage for the test executed in the Pipeline
        */ 
        stage("Collect Metrics")
        {
            tttHelper.collectCodeCoverageResults()
        }

        /* 
        This stage pushes the Source Code, Test Metrics and Coverage metrics into SonarQube and then checks the status of the SonarQube Quality Gate.  
        If the SonarQube quality date fails, the Pipeline fails and stops
        */ 
        stage("Check SonarQube Quality Gate") 
        {
            ispwHelper.downloadCopyBooks(workspace)            

            sonarHelper.scan("UT")

            String sonarGateResult = sonarHelper.checkQualityGate()

            // Evaluate the status of the Quality Gate
            if (sonarGateResult != 'OK')
            {
                echo "Sonar quality gate failure: ${sonarGateResult}"

                mailMessageExtension = "Generated code failed the Quality gate. Review Logs and apply corrections as indicated."
                currentBuild.result = "FAILURE"

                // Exit the pipeline with an error if the SonarQube Quality Gate is failing
                error "Exiting Pipeline" 
            }
            else
            {
                mailMessageExtension = "Generated code passed the Quality gate and may be promoted. \n" +
                    "SonarQube results may be reviewed at " + 
                    pConfig.sqServerUrl + 
                    "/dashboard?id=" + 
                    sonarHelper.determineUtProjectName()
            }   
        }

        /* 
        This stage triggers a XL Release Pipeline that will move code into the high levels in the ISPW Lifecycle  
        */ 
        stage("Send Mail")
        {
            // Send Standard Email
            emailext subject:       '$DEFAULT_SUBJECT',
                        body:       '$DEFAULT_CONTENT \n' + mailMessageExtension,
                        replyTo:    '$DEFAULT_REPLYTO',
                        to:         "${pConfig.mailRecipient}"

        }        
    }
}

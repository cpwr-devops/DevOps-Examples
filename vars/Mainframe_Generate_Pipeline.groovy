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
            initialize(pipelineParams) 
        }
                
        /* Download all sources that are part of the container  */
        stage("Retrieve Mainframe Code")
        {
            ispwHelper.downloadSources(pConfig.ispwSrcLevel)
            ispwHelper.downloadCopyBooks(workspace)
        }
        
        /* Retrieve the Tests from Github that match that ISPWW Stream and Application */
        stage("Execute Unit Tests")
        {            
            def gitUrlFullPath = "${pConfig.gitUrl}/${pConfig.gitTttUtRepo}"
            
            gitHelper.checkout(gitUrlFullPath, pConfig.gitBranch, pConfig.gitCredentials, pConfig.tttFolder)
        //}

        /* 
        This stage executes any Total Test Projects related to the mainframe source that was downloaded
        */ 
        //stage("Execute related Unit Tests")
        //{
            tttHelper.initialize()                                            
            tttHelper.loopThruScenarios()
            //tttHelper.passResultsToJunit()
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
            sonarHelper.scan("UT")

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
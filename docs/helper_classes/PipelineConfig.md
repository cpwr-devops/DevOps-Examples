--- 
title: PipelineConfig.groovy
layout: helper_classes
---
# <a id="PipelineConfig"></a> PipelineConfig.groovy

```groovy
package com.compuware.devops.util

/* 
    Pipeline execution specific and server specific parameters which are use throughout the pipeline
*/
class PipelineConfig implements Serializable
{
    def steps
    def mailListLines
    def mailListMap = [:]

    private String configGitProject    = "Jenkinsfiles"         // Git Hub Repository containing the configuration files for the pipeline
    private String configGitBranch                              // Branch in Git Hub Repository containing the configuration files for the pipeline    
    private String configGitPath       = "config"               // Folder in Git Hub Repository containing the configuration files for the pipeline    

    private String configPath           = 'config\\pipeline'    // Path containing config files after downloading them from Git Hub Repository
    private String pipelineConfigFile   = 'pipeline.config'     // Config file containing pipeline configuration
    private String tttGitConfigFile     = 'tttgit.config'       // Config gile containing for TTT projects stroed in Git Hub

    private String workspace

/* Environment specific settings, which differ between Jenkins servers and applications, but not between runs */
    public String gitTargetBranch                               // Used for synchronizing TTT project stored in Git with programs stored in ISPW
    public String gitBranch                                     // Used for synchronizing TTT project stored in Git with programs stored in ISPW
    
    public String sqScannerName                                 // Sonar Qube Scanner Tool name as defined in "Manage Jenkins" -> "Global Tool Configuration" -> "SonarQube scanner"
    public String sqServerName                                  // Sonar Qube Scanner Server name as defined in "Manage Jenkins" -> "Configure System" -> "SonarQube servers"
    public String sqServerUrl                                   // URL to the SonarQube server
    public String mfSourceFolder                                // Folder containing sources after downloading from ISPW
    public String xlrTemplate                                   // XL Release template to start
    public String xlrUser                                       // XL Release user to use
    public String tttFolder                                     // Folder containing TTT projects after downloading from Git Hub
    public String ispwUrl                                       // ISPW/CES URL for native REST API calls
    public String ispwRuntime                                   // ISPW Runtime

/* Runtime specific settings, which differ runs and get passed as parameters or determined during execution */
    public String ispwStream
    public String ispwApplication
    public String ispwRelease
    public String ispwAssignment
    public String ispwContainer
    public String ispwContainerType
    public String ispwSrcLevel
    public String ispwTargetLevel
    public String ispwOwner         
    public String applicationPathNum

    public String gitProject        
    public String gitCredentials    
    public String gitUrl            
    public String gitTttRepo        

    public String cesTokenId        
    public String hciConnId         
    public String hciTokenId        
    public String ccRepository      

    public String tttJcl 
      
    public String mailRecipient 
```
<a id="PipelineConfig"></a>
```
    def PipelineConfig(steps, workspace, params, mailListLines)
    {
        this.configGitBranch    = params.Config_Git_Branch
        this.steps              = steps
        this.workspace          = workspace
        this.mailListLines      = mailListLines

        this.ispwStream         = params.ISPW_Stream
        this.ispwApplication    = params.ISPW_Application
        this.ispwRelease        = params.ISPW_Release
        this.ispwAssignment     = params.ISPW_Assignment
        this.ispwContainer      = params.ISPW_Container
        this.ispwContainerType  = params.ISPW_Container_Type
        this.ispwOwner          = params.ISPW_Owner        
        this.ispwSrcLevel       = params.ISPW_Src_Level

        this.applicationPathNum = ispwSrcLevel.charAt(ispwSrcLevel.length() - 1)
        this.ispwTargetLevel    = "QA" + applicationPathNum
        this.tttJcl             = "Runner_PATH" + applicationPathNum + ".jcl"

        this.gitProject         = params.Git_Project
        this.gitCredentials     = params.Git_Credentials
        
        this.gitUrl             = "https://github.com/${gitProject}"
        this.gitTttRepo         = "${ispwStream}_${ispwApplication}_Unit_Tests.git"

        this.cesTokenId         = params.CES_Token       
        this.hciConnId          = params.HCI_Conn_ID
        this.hciTokenId         = params.HCI_Token
        this.ccRepository       = params.CC_repository
    }
```
<a id="initialize"></a>
```
    /* A Groovy idiosynchrasy prevents constructors to use methods, therefore class might require an additional "initialize" method to initialize the class */
    def initialize()
    {
        steps.dir(".\\") 
        {
            steps.deleteDir()
        }

        GitHelper gitHelper     = new GitHelper(steps)

        gitHelper.checkoutPath(gitUrl, configGitBranch, configGitPath, gitCredentials, configGitProject)

        setServerConfig()

        setTttGitConfig()

        setMailConfig()    
    }
```
<a id="setServerConfig"></a>
```
    /* Read configuration values from pipeline.config file */
    def setServerConfig()
    {
        def lineToken
        def parmName
        def parmValue

        def lines = readConfigFile("${pipelineConfigFile}")

        lines.each
        {
            lineToken   = it.toString().tokenize("=")
            parmName    = lineToken.get(0).toString()
            parmValue   = lineToken.get(1).toString().trim()

            switch(parmName)
            {
                case "SQ_SCANNER_NAME":
                    sqScannerName   = parmValue
                    break;
                case "SQ_SERVER_NAME": 
                    sqServerName    = parmValue
                    break;
                case "SQ_SERVER_URL":
                    sqServerUrl     = parmValue
                    break;
                case "MF_SOURCE_FOLDER":
                    mfSourceFolder  = parmValue
                    break;
                case "XLR_TEMPLATE":
                    xlrTemplate     = parmValue
                    break;
                case "XLR_USER":
                    xlrUser         = parmValue
                    break;
                case "TTT_FOLDER":
                    tttFolder       = parmValue
                    break;
                case "ISPW_URL":
                    ispwUrl         = parmValue
                    break;
                case "ISPW_RUNTIME":
                    ispwRuntime     = parmValue
                    break;
                default:
                    steps.echo "Found unknown Parameter " + parmName + " " + parmValue + "\nWill ignore and continue."
                    break;
            }
        }
    }
```
<a id="setTttGitConfig"></a>
```
    /* Read configuration values from tttgit.config file */
    def setTttGitConfig()
    {
        def lineToken
        def parmName
        def parmValue
        def lines = readConfigFile("${tttGitConfigFile}")

        lines.each
        {
            lineToken   = it.toString().tokenize("=")
            parmName    = lineToken.get(0).toString()
            parmValue   = lineToken.get(1).toString().trim()

            switch(parmName)
            {
                case "TTT_GIT_TARGET_BRANCH":
                    gitTargetBranch   = parmValue
                    break;
                case "TTT_GIT_BRANCH": 
                    gitBranch    = parmValue
                    break;
                default:
                    steps.echo "Found unknown Parameter " + parmName + " " + parmValue + "\nWill ignore and continue."
                    break;
            }
        }
    }
```
<a id="setMailConfig"></a>
```
    /* Read list of email addresses from config file */
    def setMailConfig()
    {        
        def lineToken
        def tsoUser
        def emailAddress

        mailListLines.each
        {
            lineToken       = it.toString().tokenize(":")
            tsoUser         = lineToken.get(0).toString()
            emailAddress    = lineToken.get(1).toString().trim()

            this.mailListMap."${tsoUser}" = "${emailAddress}"
        }

        this.mailRecipient  = mailListMap[(ispwOwner.toUpperCase())]
    }
```
<a id="readConfigFile"></a>
```    
    def readConfigFile(String fileName)
    {
        def filePath = "${workspace}\\${configPath}\\${fileName}"

        FileHelper fileHelper = new FileHelper()

        return fileHelper.readLines(filePath)
    }
}
```

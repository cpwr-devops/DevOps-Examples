--- 
title: IspwHelper.groovy
layout: helper_classes
---
# <a id="IspwHelper"></a> IspwHelper.groovy

```groovy
package com.compuware.devops.util

import groovy.json.JsonSlurper
import jenkins.plugins.http_request.*
import com.compuware.devops.util.TaskInfo

/* Wrapper class to simplify use of ISPW functions */
class IspwHelper implements Serializable 
{
    def steps

    def String ispwUrl
    def String ispwRuntime
    def String ispwApplication
    def String ispwRelease
    def String ispwContainer
    def String ispwContainerType    
    def String applicationPathNum
    def String ispwOwner
    def String ispwTargetLevel


    def String mfSourceFolder

    def String hciConnId
    def String hciTokenId

    IspwHelper(steps, pConfig) 
    {

        this.steps              = steps
        this.ispwUrl            = pConfig.ispwUrl
        this.ispwRuntime        = pConfig.ispwRuntime
        this.ispwApplication    = pConfig.ispwApplication
        this.ispwRelease        = pConfig.ispwRelease        
        this.ispwContainer      = pConfig.ispwContainer
        this.ispwContainerType  = pConfig.ispwContainerType
        this.ispwOwner          = pConfig.ispwOwner
        this.ispwTargetLevel    = pConfig.ispwTargetLevel
        this.applicationPathNum = pConfig.applicationPathNum

        this.mfSourceFolder     = pConfig.mfSourceFolder

        this.hciConnId          = pConfig.hciConnId
        this.hciTokenId         = pConfig.hciTokenId
    }
```    
<a id="downloadSources"></a>
```groovy
    /* Download sources for the ISPW Set which triggered the current pipeline */
    def downloadSources()
    {
        steps.checkout([
            $class:             'IspwContainerConfiguration', 
            componentType:      '',                                 // optional filter for component types in ISPW
            connectionId:       "${hciConnId}",     
            credentialsId:      "${hciTokenId}",      
            containerName:      "${ispwContainer}",   
            containerType:      "${ispwContainerType}",     // 0-Assignment 1-Release 2-Set
            ispwDownloadAll:    true,                              // false will not download files that exist in the workspace and haven't previous changed
            serverConfig:       '',                                 // ISPW runtime config.  if blank ISPW will use the default runtime config
            serverLevel:        ''                                  // level to download the components from
        ])
    }

    /* Download copy books used in the downloaded sources  */
    /* Since copy books do not have to be part of the current set, the downloaded programs need to be parsed to determine copy books */
    /* Since the SCM downloader plugin does not provide the option to download specific members, */
    /* the required copy books will be copied from the ISPW libraries to a single PDS using an IEBCOPY job */
    /* Then this PDS will be downloaded */
    def downloadCopyBooks(String workspace)
    {
        /* Class JclSkeleton will allow using "JCL Skeletons" to generate the requires JCL */
        JclSkeleton jclSkeleton = new JclSkeleton(steps, workspace, ispwApplication, applicationPathNum)

        /* A Groovy idiosynchrasy prevents constructors to use methods, therefore class might require an additional "initialize" method to initialize the class */
        jclSkeleton.initialize()

        /* Method referencedCopyBooks will parse the downloaded sources and generate a list of required copy books */
        def copyBookList = referencedCopyBooks(workspace)  

        if(copyBookList.size() > 0)       
        {
            // Get a string with JCL to create a PDS with referenced Copybooks
            def pdsDatasetName  = 'HDDRXM0.DEVOPS.ISPW.COPY.PDS'   

            // The createIebcopyCopyBooksJcl will create the JCL for the IEBCOPY job */
            def processJcl      = jclSkeleton.createIebcopyCopyBooksJcl(pdsDatasetName, copyBookList)

            // Submit the JCL created to create a PDS with Copybooks
            steps.topazSubmitFreeFormJcl( 
                connectionId:       "${hciConnId}", 
                credentialsId:      "${hciTokenId}", 
                jcl:                processJcl, 
                maxConditionCode:   '4'
            )
                        
            // Download the generated PDS
            steps.checkout([
                $class:         'PdsConfiguration', 
                connectionId:   "${hciConnId}",
                credentialsId:  "${hciTokenId}",
                fileExtension:  'cpy',
                filterPattern:  "${pdsDatasetName}",
                targetFolder:   "${ispwApplication}/${mfSourceFolder}"
            ])
                                                                        
            // Delete the downloaded Dataset
            processJcl = jclSkeleton.createDeleteTempDsn(pdsDatasetName)

            steps.topazSubmitFreeFormJcl(
                connectionId:       "${hciConnId}",
                credentialsId:      "${hciTokenId}",
                jcl:                processJcl,
                maxConditionCode:   '4'
            )
        }
        else
        {
            steps.echo "No Copy Books to download"
        }
    }
```    
<a id="referencedCopyBooks"></a>
```groovy
    /* Parse downloaded sources and get a list of copy books */
    def List referencedCopyBooks(String workspace) 
    {

        steps.echo "Get all .cbl in current workspace"
        
        // findFiles method requires the "Pipeline Utilities Plugin"
        // Get all Cobol Sources in the MF_Source folder into an array 
        def listOfSources   = steps.findFiles(glob: "**/${ispwApplication}/${mfSourceFolder}/*.cbl")
        def listOfCopybooks = []
        def lines           = []
        def cbook           = /\bCOPY\b/
        def tokenItem       = ''
        def seventhChar     = ''
        def lineToken       = ''

        // Define a empty array for the list of programs
        listOfSources.each 
        {
            steps.echo "Scanning Program: ${it}"
            def cpyFile = "${workspace}\\${it}"

            File file = new File(cpyFile)

            if (file.exists()) 
            {
                lines = file.readLines().findAll({book -> book =~ /$cbook/})

                lines.each 
                {
                    lineToken   = it.toString().tokenize()
                    seventhChar = ""

                    if (lineToken.get(0).toString().length() >= 7) 
                    {
                        seventhChar = lineToken.get(0).toString()[6]
                    }
                        
                    for(int i=0;i<lineToken.size();i++) 
                    {
                        tokenItem = lineToken.get(i).toString()

                        if (tokenItem == "COPY" && seventhChar != "*" ) 
                        {
                            steps.echo "Copybook: ${lineToken.get(i+1)}"
                            tokenItem = lineToken.get(i+1).toString()
        
                            if (tokenItem.endsWith(".")) 
                            {
                                listOfCopybooks.add(tokenItem.substring(0,tokenItem.size()-1))
                            }
                            else 
                            {
                                listOfCopybooks.add(tokenItem)
                            }
                                
                        i = lineToken.size()
                        }
                    }    
                }
            }
        }

        return listOfCopybooks

    }      
```    
<a id="regressAssignmentList"></a>
```groovy
    /* Regress a list of assignments */
    def regressAssignmentList(assignmentList, cesToken)
    {
        for(int i = 0; i < assignmentList.size(); i++)
        {

            steps.echo "Regress Assignment ${assignmentList[0].toString()}, Level ${ispwTargetLevel}"

            regressAssignment(assignmentList[i], cesToken)

        }
            
    }
```    
<a id="regressAssignment"></a>
```groovy
    /* Regress one assigment */
    def regressAssignment(assignment, cesToken)
    {
        def requestBodyParm = '''{
            "runtimeConfiguration": "''' + ispwRuntime + '''"
        }'''

        steps.httpRequest(
                url:                    "${ispwUrl}/ispw/${ispwRuntime}/assignments/${assignment}/tasks/regress?level=${ispwTargetLevel}",
                httpMode:               'POST',
                consoleLogResponseBody: true,
                contentType:            'APPLICATION_JSON',
                requestBody:            requestBodyParm,
                customHeaders:          [[
                                        maskValue:  true, 
                                        name:       'authorization', 
                                        value:      "${cesToken}"
                                        ]]
            )
    }
}
```
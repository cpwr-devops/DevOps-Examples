String synchConfigFile         
String branchMappingString     
String ispwTargetLevel
String sonarCobolFolder        
String sonarCopybookFolder     
String sonarResultsFile   
String sonarResultsFileVt
String sonarResultsFileNvtBatch
String sonarResultsFileNvtCics
String sonarResultsFileList     
String sonarCodeCoverageFile   
String jUnitResultsFile
String skipReason

def ispwConfig
def synchConfig

def CC_TEST_ID_MAX_LEN
def CC_SYSTEM_ID_MAX_LEN

def executionFlags

def call(){

    //**********************************************************************
    // Start of Script 
    //**********************************************************************

    stage ('Initialize') {

        dir('./') {
            deleteDir()
        }

        unstash name: 'workspace'
        
        initialize(execParms)
    }

    stage('Load code to mainframe') {

        if(executionFlags.mainframeChanges) {

            echo "[Info] - Loading code to mainframe level " + ispwTargetLevel + "."

            runMainframeLoad()

        }
        else {

            echo skipReason + "\n[Info] - No code will be loaded to the mainframe."

        }
    }

    checkForBuildParams(synchConfig.ispw.automaticBuildFile)

    stage('Build mainframe code') {

        if(executionFlags.mainframeChanges){

            echo "[Info] - Building code at mainframe level " + ispwTargetLevel + "."

            runMainframeBuild()

        }
        else{

            echo skipReason + "\n[Info] - Skipping Mainframe Build."

        }
    }

    if(executionFlags.executeVt){

        stage('Execute Unit Tests') {           

            runUnitTests()

        }
    }

    if(executionFlags.executeNvt){

        stage('Execute Module Integration Tests') {

            runIntegrationTests()

        }
    }

    if(executionFlags.mainframeChanges){

        getCocoResults()

    }
        
    stage("SonarQube Scan") {

        runSonarScan()

    }   

    if(executionFlags.executeXlr){

        stage("Trigger Release") {

            triggerXlRelease()
        
        }
    }
}

def initialize(execParms){

    synchConfigFile             = './git2ispw/synchronization.yml'

    CC_TEST_ID_MAX_LEN          = 15
    CC_SYSTEM_ID_MAX_LEN        = 15

    executionFlags              = [
            mainframeChanges: true,
            executeVt:        true,
            executeNvt:       true,
            executeXlr:       true
        ]

    branchMappingString         = ''
    ispwTargetLevel             = ''    

    //*********************************************************************************
    // Read synchconfig.yml from Shared Library resources folder
    //*********************************************************************************
    def fileText                = libraryResource synchConfigFile
    tmpConfig                   = readYaml(text: fileText)

    // Determine which execution environment/configuration to use. If none is specified, "cwcc" is the default
    if(execParms.executionEnvironment == null){

        execParms.executionEnvironment = 'cwcc'

    }
    else{
        execParms.executionEnvironment = execParms.executionEnvironment.toLowerCase()
    }

    synchConfig                 = tmpConfig.executionEnvironments[execParms.executionEnvironment]

    //*********************************************************************************
    // Build paths to subfolders of the project root
    //*********************************************************************************

    ispwConfigFile              = synchConfig.ispw.configFile.folder    + '/' + synchConfig.ispw.configFile.name
    tttRootFolder               = synchConfig.ispw.mfProject.rootFolder + synchConfig.ttt.folders.root
    tttVtFolder                 = tttRootFolder                         + synchConfig.ttt.folders.virtualizedTests
    tttNvtFolder                = tttRootFolder                         + synchConfig.ttt.folders.nonVirtualizedTests
    ccSources                   = synchConfig.ispw.mfProject.rootFolder + synchConfig.ispw.mfProject.sourcesFolder
    sonarCobolFolder            = synchConfig.ispw.mfProject.rootFolder + synchConfig.ispw.mfProject.sourcesFolder
    sonarCopybookFolder         = synchConfig.ispw.mfProject.rootFolder + synchConfig.ispw.mfProject.sourcesFolder

    sonarResultsFolder          = synchConfig.ttt.results.sonar.folder
    sonarResultsFile            = synchConfig.ttt.results.sonar.origFile
    sonarResultsFileVt          = synchConfig.ttt.results.sonar.targetFiles.virtualized
    sonarResultsFileNvtBatch    = synchConfig.ttt.results.sonar.targetFiles.nonVirtualized.batch
    sonarResultsFileNvtCics     = synchConfig.ttt.results.sonar.targetFiles.nonVirtualized.cics
    sonarResultsFileList        = []        

    sonarCodeCoverageFile       = synchConfig.coco.results.sonar.folder + '/' + synchConfig.coco.results.sonar.file
    
    jUnitResultsFile            = synchConfig.ttt.results.jUnit.folder  + '/' + synchConfig.ttt.results.jUnit.file

    //*********************************************************************************
    // Read ispwconfig.yml
    // Strip the first line of ispwconfig.yml because readYaml can't handle the !! tag
    //*********************************************************************************
    def tmpText                 = readFile(file: ispwConfigFile)

    // remove the first line (i.e. use the the substring following the first carriage return '\n')
    tmpText                     = tmpText.substring(tmpText.indexOf('\n') + 1)

    // convert the text to yaml
    ispwConfig                  = readYaml(text: tmpText)

    determinePipelineBehavior(BRANCH_NAME, BUILD_NUMBER)

    processBranchInfo(synchConfig.ispw.branchInfo, ispwConfig.ispwApplication.application)

    //*********************************************************************************
    // If target level is empty the branch name could not be mapped
    //*********************************************************************************
    if(ispwTargetLevel == ''){

        error "No branch mapping for branch ${BRANCH_NAME} was found. Execution will be aborted.\n" +
            "Correct the branch name to reflect naming conventions."
    }

    buildCocoParms(BRANCH_NAME)
}

/* Determine execution type of the pipeline */
/* If it executes for the first time, i.e. the branch has just been created, only scan sources and don't execute any tests      */
/* Else, depending on the branch type or branch name (feature, development, fix or main) determine the type of tests to execute */
/* If main branch and mainfgrame changes have been applied, trigger XLR */
def determinePipelineBehavior(branchName, buildNumber){

    if (buildNumber == "1") {

        executionFlags.mainframeChanges = false
        executionFlags.executeVt        = false
        executionFlags.executeNvt       = false
        executionFlags.executeXlr       = false

        skipReason                      = "[Info] - First build for branch '${branchName}'. Only sources will be scanned by SonarQube"
    }    
    else if (BRANCH_NAME.contains("feature")) {

        executionFlags.mainframeChanges = true
        executionFlags.executeVt        = true
        executionFlags.executeNvt       = false
        executionFlags.executeXlr       = false

        skipReason                      = "[Info] - '${branchName}' is a feature branch."
    }
    else if (BRANCH_NAME.contains("bugfix")) {

        executionFlags.mainframeChanges = true
        executionFlags.executeVt        = true
        executionFlags.executeNvt       = false
        executionFlags.executeXlr       = false

        skipReason                      = "[Info] - Branch '${branchName}'."
    }
    else if (BRANCH_NAME.contains("development")) {

        executionFlags.mainframeChanges = true
        executionFlags.executeVt        = true
        executionFlags.executeNvt       = true
        executionFlags.executeXlr       = false

        skipReason                      = "[Info] - Branch '${branchName}'."
    }
    else if (BRANCH_NAME.contains("main")) {

        executionFlags.mainframeChanges = true
        executionFlags.executeVt        = false
        executionFlags.executeNvt       = true
        executionFlags.executeXlr       = true

        skipReason                      = "[Info] - Branch '${branchName}'."
    }
}

//*********************************************************************************
// Build branch mapping string to be used as parameter in the gitToIspwIntegration
// Build load library name from configuration, replacing application marker by actual name
//*********************************************************************************
def processBranchInfo(branchInfo, ispwApplication){

    branchInfo.each {

        branchMappingString = branchMappingString + it.key + '** => ' + it.value.ispwLevel + ',' + it.value.mapRule + '\n'

        /* Get target Level and load bib for VTs from branch Mapping info for current build cranch */
        if(BRANCH_NAME.contains(it.key)) {

            ispwTargetLevel     = it.value.ispwLevel
            
        }
    }
}

//*********************************************************************************
// Build Code Coverage System ID from current branch, System ID must not be longer than 15 characters
// Build Code Coverage Test ID from Build Number
//*********************************************************************************
def buildCocoParms(executionBranch){

    if(executionBranch.length() > CC_SYSTEM_ID_MAX_LEN) {
        ccSystemId  = executionBranch.substring(executionBranch.length() - CC_SYSTEM_ID_MAX_LEN)
    }
    else {
        ccSystemId  = executionBranch
    }
    
    ccTestId    = BUILD_NUMBER

}

def runMainframeLoad() {

    gitToIspwIntegration( 
        connectionId:       synchConfig.environment.hci.connectionId,                    
        credentialsId:      synchConfig.environment.hci.credentialsId,                     
        runtimeConfig:      ispwConfig.ispwApplication.runtimeConfig,
        stream:             ispwConfig.ispwApplication.stream,
        app:                ispwConfig.ispwApplication.application, 
        branchMapping:      branchMappingString,
        ispwConfigPath:     ispwConfigFile, 
        gitCredentialsId:   synchConfig.git.credentialsId, 
        gitRepoUrl:         synchConfig.git.repoUrl
    )

}

// If the automaticBuildParams.txt has not been created, it means no programs
// have been changed and the pipeline was triggered for other changes (e.g. in configuration files)
// These changes do not need to be "built".
def checkForBuildParams(automaticBuildFile){

    try {
        def automaticBuildInfo = readJSON(file: automaticBuildFile)
    }
    catch(Exception e) {

        echo "[Info] - No Automatic Build Params file was found.  Meaning, no mainframe sources have been changed.\n" +
        "[Info] - Mainframe Build and Test steps will be skipped. Sonar scan will be executed against code only."

        executionFlags.mainframeChanges = false
        executionFlags.executeVt        = false
        executionFlags.executeNvt       = false
        executionFlags.executeXlr       = false        

        skipReason                      = skipReason + "\n[Info] - No changes to mainframe code."

    }
}

/* Build mainframe code */
def runMainframeBuild(){

    ispwOperation(
        connectionId:           synchConfig.environment.hci.connectionId, 
        credentialsId:          synchConfig.ces.credentialsId,       
        consoleLogResponseBody: true, 
        ispwAction:             'BuildTask', 
        ispwRequestBody:        '''buildautomatically = true'''
    )
}

def runUnitTests() {

    if(executionFlags.executeVt){

        echo "[Info] - Execute Unit Tests at mainframe level " + ispwTargetLevel + "."

        totaltest(
            serverUrl:                          synchConfig.environment.ces.url, 
            serverCredentialsId:                synchConfig.environment.hci.credentialsId, 
            credentialsId:                      synchConfig.environment.hci.credentialsId, 
            environmentId:                      synchConfig.ttt.environmentIds.virtualized,
            localConfig:                        false, 
            folderPath:                         tttVtFolder, 
            recursive:                          true, 
            selectProgramsOption:               true, 
            jsonFile:                           synchConfig.ispw.changedProgramsFile,
            haltPipelineOnFailure:              false,                 
            stopIfTestFailsOrThresholdReached:  false,
            createJUnitReport:                  true, 
            createReport:                       true, 
            createResult:                       true, 
            createSonarReport:                  true,
            contextVariables:                   '"ispw_app=' + ispwConfig.ispwApplication.application + ',ispw_level=' + ispwTargetLevel + '"',
            collectCodeCoverage:                true,
            collectCCRepository:                synchConfig.coco.repo,
            collectCCSystem:                    ccSystemId,
            collectCCTestID:                    ccTestId,
            clearCodeCoverage:                  false,
            logLevel:                           'INFO'
        )

        secureResultsFile(sonarResultsFileVt, "Virtualized")

        junit allowEmptyResults: true, keepLongStdio: true, testResults: synchConfig.ttt.results.jUnit.folder + '/*.xml'
    }
    else{

        echo skipReason + "\n[Info] - Skipping Unit Tests."

    }
}

def runIntegrationTests(){

    if(executionFlags.executeNvt){

        echo "[Info] - Execute Module Integration Tests at mainframe level " + ispwTargetLevel + "."

        synchConfig.ttt.environmentIds.nonVirtualized.each {

            def envType     = it.key
            def envId       = it.value
            def targetFile  = synchConfig.ttt.results.sonar.targetFiles.nonVirtualized[envType]

            totaltest(
                connectionId:                       synchConfig.environment.hci.connectionId,
                credentialsId:                      synchConfig.environment.hci.credentialsId,             
                serverUrl:                          synchConfig.environment.ces.url, 
                serverCredentialsId:                synchConfig.environment.hci.credentialsId, 
                environmentId:                      envId, 
                localConfig:                        false,
                folderPath:                         tttNvtFolder, 
                recursive:                          true, 
                selectProgramsOption:               true, 
                jsonFile:                           synchConfig.ispw.changedProgramsFile,
                haltPipelineOnFailure:              false,                 
                stopIfTestFailsOrThresholdReached:  false,
                createJUnitReport:                  true, 
                createReport:                       true, 
                createResult:                       true, 
                createSonarReport:                  true,
                collectCodeCoverage:                true,
                collectCCRepository:                synchConfig.coco.repo,
                collectCCSystem:                    ccSystemId,
                collectCCTestID:                    ccTestId,
                clearCodeCoverage:                  false,
                logLevel:                           'INFO'
            )

            secureResultsFile(targetFile, "Non Virtualized " + envType.toUpperCase())

        }
    }
    else{

        echo skipReason + "\n[Info] - Skipping Integration Tests."

    }
}

// Each execution of totaltest will create a Sonar Results file with the same name (generated.cli.suite.sonar.xml)
// Therefore, if totaltest is being executed several time within one build, the corresponding result files need to be
// renamed to prevent overwriting
def secureResultsFile(resultsFileNameNew, resultsFileType) {

    try {

        bat label:  'Rename', 
            script: """
                cd ${sonarResultsFolder}
                ren ${sonarResultsFile} ${resultsFileNameNew}
            """

        echo "[Info] - A ${resultsFileType} results file was found.\n" + 
            "[Info] - ${resultsFileNameNew} will be processed."

        sonarResultsFileList.add(resultsFileNameNew)

    }
    catch(Exception e) {

        echo "[Warn] - No ${resultsFileType} Tests Results File could be found.\n" +
        "[Warn] - This may be because no matching test scenarios (environment or target program) were found.\n"
        "[Warn] - Refer to the Topaz for Total Test log output to determine if this is due to an issue or was to be expected.\n"
            
    }

    return
}

def getCocoResults() {

    step([
        $class:             'CodeCoverageBuilder', 
        connectionId:       synchConfig.environment.hci.connectionId, 
        credentialsId:      synchConfig.environment.hci.credentialsId,
        analysisProperties: """
            cc.sources=${ccSources}
            cc.repos=${synchConfig.coco.repo}
            cc.system=${ccSystemId}
            cc.test=${ccTestId}
        """
    ])
}

def runSonarScan() {

    def sonarBranchParm         = ''
    def sonarTestResults        = ''
    def sonarTestsParm          = ''
    def sonarTestReportsParm    = ''
    def sonarCodeCoverageParm   = ''
    def scannerHome             = tool synchConfig.environment.sonar.scanner            

    if(executionFlags.executeVt){

        sonarTestsParm          = ' -Dsonar.tests="' + tttRootFolder + '"'
        sonarTestReportsParm    = ' -Dsonar.testExecutionReportPaths="' + sonarResultsFolder + '/' + sonarResultsFileVt + '"'
        sonarCodeCoverageParm   = ' -Dsonar.coverageReportPaths=' + sonarCodeCoverageFile

    }

    withSonarQubeEnv(synchConfig.environment.sonar.server) {

        bat '"' + scannerHome + '/bin/sonar-scanner"' + 
            ' -Dsonar.branch.name=' + BRANCH_NAME +
            ' -Dsonar.projectKey=' + ispwConfig.ispwApplication.stream + '_' + ispwConfig.ispwApplication.application + 
            ' -Dsonar.projectName=' + ispwConfig.ispwApplication.stream + '_' + ispwConfig.ispwApplication.application +
            ' -Dsonar.projectVersion=1.0' +
            ' -Dsonar.sources=' + sonarCobolFolder + 
            ' -Dsonar.cobol.copy.directories=' + sonarCopybookFolder +
            ' -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,stub,result,scenario' + 
            ' -Dsonar.cobol.copy.suffixes=cpy' +
            sonarTestsParm +
            sonarTestReportsParm +
            sonarCodeCoverageParm +
            ' -Dsonar.ws.timeout=480' +
            ' -Dsonar.sourceEncoding=UTF-8'

    }
}

def getMainAssignmentId(automaticBuildFile){

    def automaticBuildFileContent = readJSON(file: automaticBuildFile)

    return automaticBuildFileContent.containerId
    
}

def triggerXlRelease(){

    def cesToken 
    def ownerId

    withCredentials(
        [
            string(
                credentialsId:  synchConfig.ces.credentialsId, 
                variable:       'cesTokenTemp'
            )
        ]
    ) 
    {

        cesToken = cesTokenTemp

    }

    withCredentials(
        [
            usernamePassword(
                credentialsId:      synchConfig.environment.hci.credentialsId, 
                passwordVariable:   'pw', 
                usernameVariable:   'ownerIdTemp'
            )
        ]
    ) 
    {

        ownerId = ownerIdTemp

    }

    def assignmentId = getMainAssignmentId(synchConfig.ispw.automaticBuildFile)

    xlrCreateRelease(
        releaseTitle:       "GITDEMO - Release for ${ispwConfig.ispwApplication.application}", 
        serverCredentials:  'admin', 
        startRelease:       true, 
        template:           synchConfig.environment.xlr.template, 
        variables: [
            [
                propertyName:   'ISPW_Application', 
                propertyValue:  ispwConfig.ispwApplication.application
            ], 
            [
                propertyName:   'ISPW_Assignment', 
                propertyValue:  assignmentId
            ], 
            [
                propertyName:   'Owner_Id', 
                propertyValue:  ownerId
            ],
            [
                propertyName:   'CES_Token', 
                propertyValue:  cesToken
            ], 
            [
                propertyName: 'Jenkins_CES_Credentials', 
                propertyValue: synchConfig.ces.credentialsId
            ],
            [
                propertyName: 'Jenkins_Git_Credentials', 
                propertyValue: synchConfig.git.credentialsId
            ] 
        ]
    )
}
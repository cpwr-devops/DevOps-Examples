package com.compuware.devops.util

/**
 Wrapper around the Git Plugin's Checkout Method
 @param URL - URL for the git server
 @param Branch - The branch that will be checked out of git
 @param Credentials - Jenkins credentials for logging into git
 @param Folder - Folder relative to the workspace that git will check out files into
*/
class SonarHelper implements Serializable {

    def script
    def steps
    def scannerHome
    def pConfig

    SonarHelper(script, steps, pConfig) 
    {
        this.script     = script
        this.steps      = steps
        this.pConfig    = pConfig
    }

    /* A Groovy idiosynchrasy prevents constructors to use methods, therefore class might require an additional "initialize" method to initialize the class */
    def initialize()
    {
        this.scannerHome    = steps.tool "${pConfig.sqScannerName}";
    }

    def scan()
    {
        def testResults = determineUtResultPath()

        runScan(testResults, script.JOB_NAME)
    }

    def scan(pipelineType)
    {
        def project
        def testPath
        def resultPath
        def coveragePath

        switch(pipelineType)
        {
            case "UT":
                project         = determineUtProjectName()
                testPath        = 'tests'
                resultPath      = determineUtResultPath()
                coveragePath    = "Coverage/CodeCoverage.xml"
                break;
            case "FT":
                project         = determineFtProjectName()
                testPath        = '"tests\\' + pConfig.ispwStream + '_' + pConfig.ispwApplication + '_Functional_Tests\\Functional Test"'
                resultPath      = 'TestResults\\SonarTestReport.xml'
                coveragePath    = ''
                break;
            default:
                steps.echo "SonarHelper.scan received wrong pipelineType: " + pipelineType
                steps.echo "Valid types are 'UT' or FT"
                break;
        }

        runScan(testPath, resultPath, coveragePath, project)
    }

    String checkQualityGate()
    {
        String result

        // Wait for the results of the SonarQube Quality Gate
        steps.timeout(time: 2, unit: 'MINUTES') 
        {                
            // Wait for webhook call back from SonarQube.  SonarQube webhook for callback to Jenkins must be configured on the SonarQube server.
            def sonarGate = steps.waitForQualityGate()

            result = sonarGate.status
        }

        return result
    }

    private String determineUtProjectName()
    {
        return pConfig.ispwOwner + '_' + pConfig.ispwStream + '_' + pConfig.ispwApplication
    }

    private String determineFtProjectName()
    {
        return pConfig.ispwStream + '_' + pConfig.ispwApplication
    }


    private String determineUtResultPath()
    {
        // Finds all of the Total Test results files that will be submitted to SonarQube
        def tttListOfResults    = steps.findFiles(glob: 'TTTSonar/*.xml')   // Total Test SonarQube result files are stored in TTTSonar directory

        // Build the sonar testExecutionReportsPaths property
        // Start empty
        def testResults         = ""    

        // Loop through each result Total Test results file found
        tttListOfResults.each 
        {
            testResults         = testResults + "TTTSonar/" + it.name +  ',' // Append the results file to the property
        }

        return testResults
    }

    private runScan(testPath, testResultPath, coveragePath, projectName)
    {
        steps.withSonarQubeEnv("${pConfig.sqServerName}")       // 'localhost' is the name of the SonarQube server defined in Jenkins / Configure Systems / SonarQube server section
        {
            // Test and Coverage results
            def sqScannerProperties   = ' -Dsonar.tests=' + testPath

            sqScannerProperties       = sqScannerProperties + " -Dsonar.testExecutionReportPaths=${testResultPath}"

            if(coveragePath != '')
            {
                sqScannerProperties       = sqScannerProperties + " -Dsonar.coverageReportPaths=${coveragePath}"
            }

            // SonarQube project to load results into
            sqScannerProperties       = sqScannerProperties + " -Dsonar.projectKey=${projectName} -Dsonar.projectName=${projectName} -Dsonar.projectVersion=1.0"
            // Location of the Cobol Source Code to scan
            sqScannerProperties       = sqScannerProperties + " -Dsonar.sources=${pConfig.ispwApplication}\\${pConfig.mfSourceFolder}"
            // Location of the Cobol copybooks to scan
            sqScannerProperties       = sqScannerProperties + " -Dsonar.cobol.copy.directories=${pConfig.ispwApplication}\\${pConfig.mfSourceFolder}"  
            // File extensions for Cobol and Copybook files.  The Total Test files need that contain tests need to be defined as cobol for SonarQube to process the results
            sqScannerProperties       = sqScannerProperties + " -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,stub -Dsonar.cobol.copy.suffixes=cpy -Dsonar.sourceEncoding=UTF-8"
            
            // Call the SonarQube Scanner with properties defined above
            steps.bat "${scannerHome}/bin/sonar-scanner" + sqScannerProperties
        }
    }
}
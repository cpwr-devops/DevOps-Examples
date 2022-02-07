param(
    [string]$workspaceRoot,
    [string]$sonarScannerRoot,
    [string]$sonarServer,
    [string]$sonarProjectName,
    [string]$sourceFolder,
    [string]$testFolder,
    [string]$testResultFolder,
    [string]$codeCoverageReportFile
)

cmd.exe /c "$sonarScannerRoot\sonar-scanner -Dsonar.host.url=$sonarServer -Dsonar.projectBaseDir=$workspaceRoot -Dsonar.ws.timeout=180 -Dsonar.projectKey=$sonarProjectName -Dsonar.projectName=$sonarProjectName -Dsonar.projectVersion=1.0 -Dsonar.sources=$sourceFolder -Dsonar.cobol.copy.directories=$sourceFolder -Dsonar.cobol.copy.suffixes=cpy -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,stub,results,scenario,context -Dsonar.tests=$testFolder -Dsonar.testExecutionReportPaths=$testResultFolder -Dsonar.coverageReportPaths=$codeCoverageReportFile"
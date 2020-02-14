param(
    [string]$workspaceRoot,
    [string]$ispwApplication,
    [string]$sonarProjectName,
    [string]$sonarSources
)

[string]$sonarScannerRoot = 'C:\Users\cwde-rnuesse.EMEA\Software\Sonar\Scanner\bin'
[string]$testPath = 'tests'
[string]$codeCoverageResults = 'Coverage/CodeCoverage.xml'

$pathList = @()

Get-ChildItem -Path $workspaceRoot\$sonarSources `
    -Include *.cbl `
    -Recurse `
    -ErrorAction SilentlyContinue `
    -File `
    | `
    ForEach-Object {`
        $pathList = $pathList + $_.FullName`
    }

$cobolSources = @()

ForEach($path in $pathList) {
    $cobolSources = $cobolSources + $path.SubString($path.LastIndexOf('\') + 1, $path.LastIndexOf('.') - $path.LastIndexOf('\') - 1)
}

[string]$testResultsPath

ForEach ($programName in $cobolSources) {
    
    $searchPath = $workspaceRoot + "\TTTSonar\"

    Get-ChildItem -Path $searchPath `
        -Include $programName*.xml `
        -Recurse `
        -ErrorAction SilentlyContinue `
        -File `
        | `
        ForEach-Object {
            $testResultsPath = $testResultsPath + $_.FullName.Substring($_.FullName.IndexOf('\TTTSonar') + 1) + ','
        }
}

$testResultsPath

#cmd.exe /c "$sonarScannerRoot\sonar-scanner -Dsonar.projectBaseDir=$workspaceRoot -Dsonar.ws.timeout=180 -Dsonar.projectKey=$sonarProjectName -Dsonar.projectName=$sonarProjectName -Dsonar.projectVersion=1.0 -Dsonar.sources=$sonarSources -Dsonar.cobol.copy.directories=$sonarSources -Dsonar.cobol.copy.suffixes=cpy -Dsonar.cobol.file.suffixes=cbl,testsuite,testscenario,cpy,stub -Dsonar.tests=$testPath -Dsonar.testExecutionReportPaths=$testResultsPath -Dsonar.coverageReportPaths=$codeCoverageResults"
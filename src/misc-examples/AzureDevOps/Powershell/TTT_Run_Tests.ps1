param(
    [string]$workspaceRoot,
    [string]$hostUri,
    [string]$hostPort,
    [string]$hostUser,
    [string]$hostPassword,
    [string]$hostCodePage,
    [string]$ispwApplication,
    [string]$ispwLevel,
    [string]$ccRepo,
    [string]$ccSystem,
    [string]$ccTestid,
    [string]$cliPath
 )

[string]$cliWorkpace = ".\TopazCliWkspc"
[string]$projectFolderIdentifier = "_Unit_Tests"
[string]$tttJcl = 'Runner_PATH' + $ispwLevel.Substring($ispwLevel.Length -1, 1) + '.jcl'
[string]$tttSonarResults = $workspaceRoot + "\TTTSonar"

cmd.exe /c md $tttSonarResults

# Determine names of downloaded cobol sources
$pathList = @()

Get-ChildItem -Path $workspaceRoot\$ispwApplication\MF_Source `    -Include *.cbl `    -Recurse `    -ErrorAction SilentlyContinue `    -File `    | `    ForEach-Object {`        $pathList = $pathList + $_.FullName`    }

$cobolSources = @()

ForEach($path in $pathList) {
    $cobolSources = $cobolSources + $path.SubString($path.LastIndexOf('\') + 1, $path.LastIndexOf('.') - $path.LastIndexOf('\') - 1)
}

# Determine names downloaded testscenarios
$pathList = @()

Get-ChildItem -Path $workspaceRoot\tests `    -Include *.testscenario `    -Recurse -ErrorAction `    SilentlyContinue -File `    | `    ForEach-Object {`        $pathList = $pathList + $_.FullName`    }

$testScenarioNames = @()

# Loop through scenarios
# If the names of target match the name of a downloaded source
# Execute the scenario
ForEach($path in $pathList) {
    $testScenarioName = $path.SubString($path.LastIndexOf('\') + 1, $path.LastIndexOf('.') - $path.LastIndexOf('\') - 1)
    $testScenarioTarget = $testScenarioName.Substring(0, $testScenarioName.IndexOf('_'))
    
    if($cobolSources.Contains($testScenarioTarget)) {

        $tttProjectPath = $path.SubString(0, $path.LastIndexOf('_Unit_Tests') + 11)
        $tttResultsPath = $tttProjectPath + '\Unit Test\Output\Sonar'

        Remove-Item $tttResultsPath\* -Recurse -Force
        cmd.exe /c $cliPath\TotalTestCLI.bat `            -cmd runtest `            -h $hostUri `            -pt $hostPort `            -te $hostCodePage `            -encryptprotocol None `            -u $hostUser `            -pw $hostPassword `            -p $tttProjectPath `            -ts "$testScenarioName.testscenario" `            -jcl $tttJcl `            -usestubs true `            -deletetemp true `            -ccrepo $ccRepo `            -ccsystem $ccSystem `            -cctestid $ccTestid `            -cctype TOTALTEST `            -ccclearstats false `            -data $cliWorkpace

        $searchPath = $workspaceRoot + "\tests\" + $testScenarioTarget + "_Unit_Tests\" + 'Unit Test' + "\Output\Sonar"

        Get-ChildItem -Path $searchPath `
            -Include *.xml `
            -Recurse `
            -ErrorAction SilentlyContinue `
            -File `
            | `
            ForEach-Object {
                $xmlName = $_.FullName.Substring($_.FullName.LastIndexOf('\') + 1)
                $targetName = "$tttSonarResults\$xmlName"
                $targetName
                Copy-Item -Path $_.FullName -Destination $targetName
            }
    }
}
param(
    [string]$workspaceRoot,
    [string]$tttEnvironment,
    [string]$cesUri,
    [string]$hostUser,
    [string]$hostPassword,
    [string]$hostCodePage,
    [string]$ispwApplication,
    [string]$ispwLevel,
    [string]$testFolder,
    [string]$ccRepo,
    [string]$ccSystem,
    [string]$ccTestid,
    [string]$cliPath
 )

[string]$cliWorkpace = ".\TopazCliWkspc"
[string]$contextVars = '"ispw_app=' + $ispwApplication + ',ispw_level=' + $ispwLevel + '"'

#[string]$tttSonarResults = $workspaceRoot + "\TTTSonar"
#cmd.exe /c md $tttSonarResults

# Determine names of downloaded cobol sources
CD $workspaceRootcmd.exe /c $cliPath\TotalTestFTCLI.bat `    -e $tttEnvironment `    -u $hostUser `    -p $hostPassword `    -s http://$cesUri/totaltestapi/ `    -cesu $hostUser `    -cesp $hostPassword `    -f $testFolder `    -pnf changedPrograms.json `    -ccrepo $ccRepo `    -ccsys $ccSystem `    -cctid $ccTestid `    -ccclear true `    -ctxvars $contextVars `    -R `    -G `    -v 6 `    -l jenkins `    -loglevel INFO `    -data $cliWorkpace
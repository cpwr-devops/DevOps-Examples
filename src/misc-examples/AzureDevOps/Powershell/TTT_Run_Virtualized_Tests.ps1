﻿param(
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

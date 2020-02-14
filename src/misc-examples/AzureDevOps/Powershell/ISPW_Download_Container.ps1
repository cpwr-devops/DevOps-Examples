param(
    [string]$workspaceRoot,
    [string]$hostUri,
    [string]$hostPort,
    [string]$hostUser,
    [string]$hostPassword,
    [string]$hostCodePage,
    [string]$ispwConfig,
    [string]$ispwContainerName,
    [string]$ispwContainerType,
    [string]$ispwDownloadLevel,
    [string]$cliPath
 )

[string]$cliWorkpace = ".\TopazCliWkspc"

CD $workspaceRoot

cmd.exe /c $cliPath\SCMDownloaderCLI.bat `    -host $hostUri `    -port $hostPort `    -id $hostUser `
    -pass $hostPassword `    -protocol None `    -code $hostCodePage `    -timeout 0 `    -targetFolder .\ `
    -data $cliWorkpace `    -ispwServerConfig $ispwConfig `    -scm ispwc `    -ispwContainerName $ispwContainerName `    -ispwContainerType $ispwContainerType `    -ispwServerLevel $ispwDownloadLevel `    -ispwDownloadAll true
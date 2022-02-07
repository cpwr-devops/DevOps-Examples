param(
    [string]$workspaceRoot,
    [string]$hostUri,
    [string]$hostPort,
    [string]$hostUser,
    [string]$hostPassword,
    [string]$hostCodePage,
    [string]$ispwApplication,
    [string]$ccRepo,
    [string]$ccSystem,
    [string]$ccTestid,
    [string]$ccDdio,
    [string]$cliPath
 )

[string]$cliWorkspace = ".\TopazCliWkspc"
[string]$ccSourcesFolder = $ispwApplication + "\MF_Source"

cmd.exe /c "$cliPath\CodeCoverageCLI.bat -host $hostUri -port $hostPort -id $hostUser -pass $hostPassword -code $hostCodePage -targetFolder $workspaceRoot -data $cliWorkspace -cc.repos $ccRepo -cc.test $ccTestid -cc.system $ccSystem -cc.sources $ccSourcesFolder -timeout 0"
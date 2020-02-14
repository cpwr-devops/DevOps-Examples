param(
    [string]$hostPdsName = "HDDRXM0.DEMO.COB.BATCH",
    [string]$extension = 'cbl' 
 )

[string]$cliPath = "C:\Users\cwde-rnuesse.EMEA\Software\Compuware\TopazCLI\"
[string]$downloadType = "pds"
[string]$hostUri = "cwcc.compuware.com"
[string]$hostPort = "16196"
[string]$hostCodePage = "1047"
[string]$hostUser = "hddrxm0"
[string]$hostPassword = "cpwr2002"

CD C:\Users\cwde-rnuesse.EMEA\Software\Azure_DevOps_Workspace
C:\Users\cwde-rnuesse.EMEA\Software\Compuware\TopazCLI\SCMDownloaderCLI.bat -host $hostUri -port $hostPort -id $hostUser -pass $hostPassword -code $hostCodePage -timeout 0 -scm $downloadType -targetFolder .\MF_Sources -data .\workspace\Test\TopazCliWkspc -filter $hostPdsName -ext $extension
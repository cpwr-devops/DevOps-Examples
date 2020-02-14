param(
    [string]$sonarServer,
    [string]$sonarProjectName,
    [string]$sonarAuthorization
)

$uri = $sonarServer + "/api/qualitygates/project_status?projectKey=" + $sonarProjectName

$headers = @{}
$headers.Add("Authorization", $sonarAuthorization)
$headers.Add("Content-Type", "application/json")

$response = Invoke-RestMethod -Uri $uri -method GET -headers $hdrs

$status = $response.projectStatus.status

Write-Host "Checked Sonar Quality Gate status for Project $sonarProjectName."
Write-Host "Status is $status."

if($status -ne "OK"){
    $LASTEXITCODE = 1
}
else
{
    $LASTEXITCODE = 0
}
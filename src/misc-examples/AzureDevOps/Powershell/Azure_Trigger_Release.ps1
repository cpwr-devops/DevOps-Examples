param(
    [string]$serverAddress,
    [string]$serverOrganization,
    [string]$serverProject,
    [string]$serverAuthorization,
    [string]$ispwApplication,
    [string]$ispwContainerName,
    [string]$ispwContainerType,
    [string]$ispwLevel,
    [string]$releaseDefinitionId
)

$uri = "$serverAddress/$serverOrganization/$serverProject/_apis/release/releases?api-version=5.1"

Write-Host $uri
Write-Host "Authorization: $serverAuthorization"

$headers = @{}
$headers.Add("Authorization", "$serverAuthorization")
$headers.Add("Content-Type", "application/json")

Write-Host $releaseDefinitionId

$requestBody = '{
  "definitionId": "' + $releaseDefinitionId + '",
  "variables": {
    "ispwApplication": {
      "value": "' + $ispwApplication + '"
    },
    "ispwContainerName": {
      "value": "' + $ispwContainerName + '"
    },
    "ispwContainerType": {
      "value": "' + $ispwContainerType + '"
    },
    "ispwLevel": {
      "value": "' + $ispwLevel + '"
    } 
  }
}'

$response = Invoke-RestMethod -Uri $uri -method POST -headers $headers -body $requestBody

Write-Host $response
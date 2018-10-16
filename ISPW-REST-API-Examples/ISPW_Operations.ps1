#this script requires powershell 5.1 or higher to run.  Powershell 5.1 can be found here->https://www.microsoft.com/en-us/download/details.aspx?id=54616

param(
 [string]$ISPWFunction = "TaskLoad",              #ContainerCreate, ContainerOperation,TaskLoad
 [string]$ces = "cwca:2077",                                #dtw-pmsonarqube.nasa.cpwr.corp:2020
 [string]$runtimeconfig = "ISPW",
 [string]$containerType = "releases",                    #assignments, releases
 [string]$operation = "deploy",                            #generate, promote, deploy, regress
 [string]$container = "BAW1000004",
 [string]$level = "DEV1",
 [string]$token = "hjghg2aa5-5ee9-4d95-8368-7a5effd6e79f",   
 [string]$stream = "FTSDEMO",
 [string]$application = "BAW1",
 [string]$ISPWServer = "ispw",
 [string]$description = "Created from the API",             #for create container
 [string]$prefix = "PLAY",                                  #for create container
 [string]$refnumber = "CWE-1001",                           #for create container
 [string]$usertag = "API",                                  #for create container
 [string]$owner = "PXHDAE-",                                #for create container
 [string]$moduleName = "CWBWCOBX",                          #for task load
 [string]$moduleType = "COB",                               #for task load
 [string]$type = "program",                                 #for task load
 [string]$dpenvlst = "CW01QA CW02QA"                        #for deploy CWCCQA CW01QA CW02QA CW40QA
 )

if ($ISPWFunction -eq "ContainerOperation"){
    $ISPWRelease = [ISPW_API_Request]::new($ces, $ISPWServer, $containerType, $stream, $application, $level, $token, $container, $runtimeconfig, $operation, $dpenvlst)
    $ISPWRelease.ContainerOperation()
}
elseif ($ISPWFunction -eq "ContainerCreate"){
    $ISPWRelease = [ISPW_API_Request]::new($ces, $ISPWServer, $containerType, $stream, $application, $level, $token, $container, $description, $prefix, $refnumber, $usertag, $owner)
    $ISPWRelease.ContainerCreate()
}
elseif ($ISPWFunction -eq "TaskLoad"){
    $ISPWRelease = [ISPW_API_Request]::new($ces, $ISPWServer, $containerType, $stream, $application, $level, $token, $container, $runtimeconfig, $operation)
    $ISPWRelease.TaskLoad($moduleName, $moduleType, $type)
}
else{
    Write-Host "Please supply an ISPW Operation to perform.  Valid Operations are ContainerCreate, ContainterOperation"
}

Class ISPW_API_Request{
    #required
    [string]$ces 
    [string]$ISPWServer
    [string]$containerType 
    [string]$stream
    [string]$application
    [string]$level 
    [string]$token
    [string]$container 

    #used for container operations
    [string]$runtimeconfig
    [string]$operation
    [string]$changeType
    [string]$executionStatus
    [string]$dpenvlst

    #for container create
    [string]$description
    [string]$prefix
    [string]$refnumber
    [string]$usertag
    [string]$owner

    #internal
    [string]$status
    [string]$message
    [string]$httpstatus
    [int]$ApiVersion

    #constructor for Container Operation
    ISPW_API_Request ([string] $ces, $ISPWServer, $containerType, $stream, $application, $level, $token, $container, $runtimeconfig, $operation, $dpenvlst){
        $this.ces = $ces
        $this.ISPWServer = $ISPWServer
        $this.containerType = $containerType
        $this.stream = $stream
        $this.application = $application
        $this.level = $level
        $this.token = $token
        $this.container = $container
        $this.runtimeconfig = $runtimeconfig
        $this.operation = $operation
        $this.changeType = "S"
        $this.executionStatus = "I"
        $this.dpenvlst = $dpenvlst
    }
    #constructor for Container Create
    ISPW_API_Request ([string] $ces, $ISPWServer, $containerType, $stream, $application, $level, $token, $container, $description, $prefix, $refnumber, $usertag, $owner){
        $this.ces = $ces
        $this.ISPWServer = $ISPWServer
        $this.containerType = $containerType
        $this.stream = $stream
        $this.application = $application
        $this.level = $level
        $this.token = $token
        $this.container = $container
        $this.description = $description
        $this.prefix = $prefix
        $this.refnumber = $refnumber
        $this.usertag = $usertag
        $this.owner = $owner
    }

    [void]TaskLoad([string] $moduleName, $moduleType, $type){
    try{
        #check the version of the REST apis
        $this.ApiVersionCheck()

        if ($this.ApiVersion -lt 0){
            Write-Host "ISPW: Task load operation is not supported in the" $this.ApiVersion "release"
            exit 1
        }
        elseif ($this.containerType -eq "releases") {
            Write-Host "ISPW: Task load operation is not supported for" $this.containerType "containers"
            exit 1
        }

        Write-Host "ISPW: Task load operation triggered for" $this.container "for" $moduleName "of type" $moduleType "with" $type "option"
        #build the URL, header and body for REST API
        $uri = "http://" + $this.ces + "/ispw/" + $this.ISPWServer + "/" + $this.containerType + "/" +$this.container + "/tasks"

        #build request body
        $hash = @{}
        $hash.add("application", $this.application)
        $hash.add("moduleName", $moduleName)
        $hash.add("moduleType", $moduleType)
        $hash.add("stream", $this.stream)
        $hash.add("currentLevel", $this.level)
        $hash.add("startingLevel", $this.level)

        #determine what type of flags to load for the task
        if ($type -eq "program"){
            $hash.add("program", ([System.Convert]::ToBoolean("True")))
        }
        elseif($type -eq "sql"){
            $hash.add("sql", ([System.Convert]::ToBoolean("True")))
        }
        elseif($type -eq "ims"){
            $hash.add("ims", ([System.Convert]::ToBoolean("True")))
        }
        elseif($type -eq "cics"){
            $hash.add("cics", ([System.Convert]::ToBoolean("True")))
        }
        $body = ConvertTo-Json($hash)

        #Write-host "json =" $body

        #build request headers
        $hdrs = @{}
        $hdrs.Add("Authorization", $this.token)
        $hdrs.Add("Content-Type", "application/json")

        Write-Host "ISPW: API Request:" $uri

        #Execute the REST API
        $response = Invoke-RestMethod -Uri $uri -method POST -headers $hdrs -body $body
        $loadurl = $response.url
        $loadmessage = $response.message

        Write-Host "ISPW: Task load returned message:" $loadmessage ": url to loaded task" $loadurl
    }
        catch {
            # Dig into the exception to get the Response details.

            try{
                $result = $_.Exception.Response.GetResponseStream()
                $Reader = New-Object System.IO.StreamReader($result)
                $ResponseBody = $Reader.ReadToEnd() | ConvertFrom-Json
                $this.message = $ResponseBody.message
                $this.httpstatus = $_.Exception.Response.StatusCode.value__
            	if($this.message -match "Unauthorized"){
                	Write-Host "ISPW: Please check that your CES Personal access token is valid and you are using a valid ISPW Runtime Configuration"
		}
            }
            catch {
                Write-Host "ISPW: A connection problem occured at the HTTP Level.  Please ensure the Jenkins server has access to connect to" $this.ces
                exit 1
            }

            Write-Host "ISPW: Request failed"
            Write-Host "ISPW: Server returned:"$this.message
            Write-Host "ISPW: StatusCode:" $_.Exception.Response.StatusCode.value__ 
            Write-Host "ISPW: StatusDescription:" $_.Exception.Response.StatusDescription   
            exit 1
        }

    }

    [string]ContainerCreate(){
        try {
            #check the version of the REST apis
            $this.ApiVersionCheck()

            #build the URL, header and body for REST API
       	    $uri = "http://" + $this.ces + "/ispw/" + $this.ISPWServer + "/" + $this.containerType + "/"

            #build request body
            $hash = @{}
            $hash.add("stream", $this.stream)
            $hash.add("application", $this.application)
            $hash.add("defaultPath", $this.level)
            $hash.add("releaseId", $this.container)
            $hash.add("release", $this.container)
            $hash.add("description", $this.description)
            $hash.add("assignmentPrefix", $this.prefix)
            $hash.add("refNumber", $this.refnumber)
            $hash.add("referenceNumber", $this.refnumber)
            $hash.add("userTag", $this.usertag)
            $hash.add("owner", $this.owner)
            $body = ConvertTo-Json($hash)

            #build request headers
            $hdrs = @{}
            $hdrs.Add("Authorization", $this.token)
            $hdrs.Add("Content-Type", "application/json")

            Write-Host "ISPW: API Request:" $uri

            #Execute the REST API
            $response = Invoke-RestMethod -Uri $uri -method POST -headers $hdrs -body $body

            #retrieve the tracking info for the request from the ISPW reponse
            if ($this.containerType -eq "assignments"){
                $id = $response.assignmentID
            }
            else{
                $id = $response.releaseID
            }
            $url = $response.url

            #for debugging purposes
            #$response[0] | format-list
            Write-Host "ISPW:" $this.containerType "created.  Tracking info: " $id " : url " $url

            #now retrieve the info on the container that was just created
            $response = Invoke-RestMethod -Uri $url -method GET -headers $hdrs
            Write-Host "ISPW: details for " $id  ($response | Out-String)
            return $id

        } 
        #Exception handling for REST API calls
        catch {
            # Dig into the exception to get the Response details.

            try{
                $result = $_.Exception.Response.GetResponseStream()
                $Reader = New-Object System.IO.StreamReader($result)
                $ResponseBody = $Reader.ReadToEnd() | ConvertFrom-Json
                $this.message = $ResponseBody.message
                $this.httpstatus = $_.Exception.Response.StatusCode.value__
            	if($this.message -match "Unauthorized"){
                	Write-Host "ISPW: Please check that your CES Personal access token is valid and you are using a valid ISPW Runtime Configuration"
		}
            }
            catch {
                Write-Host "ISPW: A connection problem occured at the HTTP Level.  Please ensure the Jenkins server has access to connect to" $this.ces
                exit 1
            }

            Write-Host "ISPW: Request failed"
            Write-Host "ISPW: Server returned:"$this.message
            Write-Host "ISPW: StatusCode:" $_.Exception.Response.StatusCode.value__ 
            Write-Host "ISPW: StatusDescription:" $_.Exception.Response.StatusDescription   
            exit 1
        }
    }

    [void]ContainerOperation(){
        try {
            #check the version of the REST apis
            $this.ApiVersionCheck()

            #build the URL, header and body for REST API
    	    $uri = "http://" + $this.ces + "/ispw/" + $this.ISPWServer + "/" + $this.containerType + "/" + $this.container + "/tasks/" 
            $hash = @{}
            $hash.add("changeType", $this.changeType)
            $hash.add("executionStatus", $this.executionStatus)
            $hash.add("runtimeConfiguration", $this.runtimeconfig)

            #if a deploy env is specified, then add it to the deploy request
            if ($this.dpenvlst -ne " ")
            {
                $hash.add("dpenvlst", $this.dpenvlst)
            }

            $body = ConvertTo-Json($hash)
            $hdrs = @{}
            $hdrs.Add("Authorization", $this.token)
            $hdrs.Add("Content-Type", "application/json")


            $operationurl = "http://"+ $this.ces + '/ispw/' + $this.ISPWServer + "/" + $this.containerType + "/" + $this.container + "/tasks/" + $this.operation + "?level=" + $this.level

            Write-Host "ISPW:"$this.operation "received for" $this.container " Url" $operationurl

            #handle task list not being supported in July
            if(($this.containerType -eq "assignments" ) -and ($this.ApiVersion -lt 2)){

                #retrieve the container task list
                $response = Invoke-RestMethod -Uri $uri -method GET -headers $hdrs 

                #Display the tasks in the container
                $str = $response.tasks

                Write-Host "ISPW:" $this.container "tasks before" $this.operation "operation"
                foreach ($item in $str) {
                         Write-Host "ISPW: -----> Taskid" $item.taskId "Module Name:" $item.moduleName "Type:" $item.moduleType  "Level:" $item.level "Status:" $item.status "Message:" $item.message 
                }
                Write-Host "ISPW:" 
            }
            #Execute the ISPW Container Operation
            $uri = $uri + $this.operation + "?level=" + $this.level
            $response = Invoke-RestMethod -Uri $uri -method POST -headers $hdrs -body $body

            #retrieve the tracking info for the request from the ISPW reponse
            $setID = $response.setID
            $infourl = $response.url                #url to get info on the set

            Write-Host "ISPW: Set" $setID "created. Url" $infourl

            #Enter a loop while ISPW executes the operation
            $strQuit = "N"
            $counter = 0
            Do{
                #retrieve set status from ISPW
                $inforesponse = Invoke-RestMethod -Uri $infourl -method GET -headers $hdrs

                #set the state of the ISPW_API_REQUEST object to the status of the set generated by the operation
                $this.status = $inforesponse.state

                if(($this.status -eq "Executing") -or ($this.status -eq "Dispatched") -or ($this.status -eq "Ready")){
                    #Set is executing normally
                    Write-Host "ISPW: Set" $setID "status is" $this.status
                }
                elseif(($this.status -eq "Closed") -or ($this.status -eq "Completed"))  {
                    #Set completed processing
                    $strQuit = "Y"
                    Write-Host "ISPW: Set" $setID "has completed successfully.  Set status is" $this.status
                }
                else{
                    #Unknown status, exit with error
                    $strQuit = "Y"
                    Write-Host "ISPW: Problem was encountered while processing set" $setID "Set status is" $this.status
                    exit 1
                }

                #wait before checking again
                Start-Sleep -s 5
                $counter = $counter + 5
            } 
            Until (($strQuit -eq "Y") -or ($counter -eq 100))

            #Display the tasks in the container
            #Loop through tasks
            Write-Host "ISPW:"$this.container "operation complete"


            #retrieve the container task list
            if($this.ApiVersion -gt 1){
                Write-Host "ISPW:" $this.container "tasks after" $this.operation "operation:"
                #retrieve the container task list
                $uri = "http://" + $this.ces + "/ispw/" + $this.ISPWServer + "/" + $this.containerType + "/" + $this.container + "/tasks/" 
                $response = Invoke-RestMethod -Uri $uri -method GET -headers $hdrs 
                $str = $response.tasks

                #Display the tasks in the container
                foreach ($item in $str) {
                    Write-Host "ISPW: -----> Taskid" $item.taskId "Module Name:" $item.moduleName "Type:" $item.moduleType  "Level:" $item.level "Status:" $item.status "Message:" $item.message
                }
            }
            Write-Host "ISPW:" 
            if(($this.operation -eq "deploy" ) -and ($this.ApiVersion -gt 1)){
                Write-Host "ISPW: Items Deployed"
                #retrieve the container task list
                $uri = "http://" + $this.ces + "/ispw/" + $this.ISPWServer + "/" + "sets" + "/" + $setID + "/deployment" 
                Write-Host "ISPW: Url" $uri
                $response = Invoke-RestMethod -Uri $uri -method GET -headers $hdrs 
                $str = $response.deployments

                #Display the tasks in the container
                foreach ($item in $str) {
                    Write-Host "ISPW: -----> RequestID" $item.requestId "Environment:" $item.environment "Status:" $item.status 
                }
            }
            Write-Host "ISPW:" 
        } 
        #Exception handling for REST API calls
        catch {
            # Dig into the exception to get the Response details.
            try{
                $result = $_.Exception.Response.GetResponseStream()
                $Reader = New-Object System.IO.StreamReader($result)
                $ResponseBody = $Reader.ReadToEnd() | ConvertFrom-Json
                $this.message = $ResponseBody.message
                $this.httpstatus = $_.Exception.Response.StatusCode.value__
            }
            catch {
                #not able to get the response from the REST call
                Write-Host "ISPW: A connection problem occured at the HTTP Level.  Please ensure the Jenkins server has access to connect to" $this.ces
                exit 1
            }
            Write-Host "ISPW: Request failed"
            Write-Host "ISPW: Server returned:"$this.message

            #Known exceptions that occur when ISPW container has tasks in invalid state for operation
            if($this.message -match "Unauthorized"){
                Write-Host "ISPW: Please check that your CES Personal access token is valid and you are using a valid ISPW Runtime Configuration"
            }

            if($this.message -match "conflict"){
                Write-Host "ISPW: Conflicting processing - Check tasks in the conatiner"
            }
            if($this.message -match "must contain tasks"){
                Write-Host "ISPW: No tasks were eliable for promotion at" $this.level
            }
            #Unknown exceptions
            else {
                Write-Host "ISPW: Unexpected Error during ISPW REST API call"
                Write-Host "ISPW: StatusCode:" $_.Exception.Response.StatusCode.value__ 
                Write-Host "ISPW: StatusDescription:" $_.Exception.Response.StatusDescription   
            }
            exit 1
        }
    }
    [void]ApiVersionCheck(){
            #version check for CES to determine API compatability
            $hdrs = @{}
            $hdrs.Add("Content-Type", "application/json")
            $uri = "http://" + $this.ces + "/compuware/ws/Config/version"
            Write-Host "ISPW: Performing version check" $uri

            $response = Invoke-RestMethod -Uri $uri -method Get -header $hdrs

            #retrieve the version from CES
            $version = ($response."CesVersionResponse"."productVersion"."version")
            Write-Host "ISPW: REST API version" $version

            #set API version
            if ($version -match "18.2.2")
            {
                $this.ApiVersion = 2
            }
            elseif ($version -match "18.2.1")
            {
                 $this.ApiVersion = 1
                 if (($this.operation -eq "generations") -and ($this.containerType -eq "releases")){
                     Write-Host "ISPW: Generate on release containers is not supported in this version of ISPW's REST API"
                     exit 1
                 }
            }
            elseif ($version -match "18.2.0")
            {
                 $this.ApiVersion = 0
            }
            elseif ($version -match "17.2") {
                 $this.ApiVersion = -1
            }
            else{
                Write-Host "ISPW: REST API Version supports all functionality"
                $this.ApiVersion = 10
            }
    }
}
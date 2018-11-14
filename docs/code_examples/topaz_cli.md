---
layout: code_examples
---
# <a id="Topaz CLI"></a> Topaz Workbench Command Line Interface

The Topaz Workbench Command Line Interface (CLI) is distributed via the full Topaz Workbench installation media and may be used to execute a set of Topaz and ISPW related functions from a batch/shell interface. After [installation](../tool_configuration/Compuware_configurations.html), the command line interface folder contains a set of `.bat` files that can be used to execute the required functions. These are

`.bat` file | description
----------- | -----------
`CodeCoverageCLI.bat` | Allows downloading results from a Xpediter Code Coverage repository by specifying the repository name, system name and test ID.
`SCMDownloaderCLI.bat` | Allows interaction with the ISPW downloader to download sources from ISPW repositories, Endevor repositories or PDS 
`SubmitJclCLI.bat` | Allows submitting JCL on the mainframe and retrieving the return code
`TotalTestCLI.bat` | Allows execution of Topaz for Total Test scenarios and suites. The Topaz for Total Test CLI is also documented in the Topaz online help

## Workspace
The Topaz Workbench CLI will want create a workspace to use during execution. Also, it will want to write to the `configuration` folder. Therefore, the user executing the Topaz Workbench CLI will 
- either need write access to the installation folders (containing the `configuration` folder) and the default workspace
- or will have to specify a workspace folder using the `-data` parameter during execution

E.g. 

```bat
CodeCoverageCLI.bat -data C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace
```

will create a workspace in the user's profile, copy the `configuration` folder into that workspace and use that workspace and it's content for execution. All `.bat` files may use the same workspace, once that has been created by any one of the files.

## Getting "online" help
Executing any of the `.bat` files without parameters, or with just the `-data` parameter, or with the `-help` parameter will return the help pages for the respective file. E.g. using the previous command will result in the following output:

```
Code Coverage CLI started, version = 19.02.01. 
Program arguments were not passed in the ALL_ARGS environment variable, will use the Eclipse command line to parse. 
usage: Code Coverage CLI
    --cc.ddio.overrides <arg>   comma separated list of DDIO overrides
    --cc.repos <arg>            the code coverage repository (dataset
                                name)
    --cc.sources <arg>          comma separated list of source folders
                                where the source has been downloaded
    --cc.system <arg>           the code coverage system name
    --cc.test <arg>             the code coverage test name
 -code <arg>                    the code page for the connection
 -help                          print help
 -host <arg>                    the host name or IP to be connected
 -id <arg>                      the user name
 -pass <arg>                    the user password
 -port <arg>                    the port to be connected
 -targetFolder <arg>            the target folder where the source will be
                                downloaded
 -timeout <arg>                 the timeout (in minutes) for the
                                connection
The host parameter is not specified. 
Code Coverage CLI ended with status = 4. 
```

## Common parameters
Several parameters are common between the `.bat` files. These are:

Parameter | Description
--------- | -----------
`-code <arg>` | the code page for the connection
`-help` | print help
`-host <arg>` | the host name or IP to be connected
`-id <arg>` | the user name
`-pass <arg>` | the user password (in clear text)
`-port <arg>` | the port to be connected
`-timeout <arg>` | the timeout (in minutes) for the connection

## Specific parameters
In addition to the above parameters, the `.bat` files use parameters specfic to each individual file.

### CodeCoverageCLI.bat

Parameter | Description
--------- | -----------
`-cc.ddio.overrides <arg>` | comma separated list of DDIO overrides
`-cc.repos <arg>` | the code coverage repository (dataset name)
`-cc.sources <arg>` | comma separated list of source folders where the source has been downloaded to
`-cc.system <arg>` | the code coverage system name
`-cc.test <arg>` | the code coverage test name
`-targetFolder <arg>` | the target folder where the data will be downloaded to

This example will download the Code Coverage results from repository `'HDDRXM0.DEMO.COCO.REPOS'`, using the system `RXN3`, and test ID `646`. All resources reside on host `my.mainframe.host`, communicating on port `16196`. The results will be downloaded to the `Coverage` sub folder of the specified target folder. The sources to compare the Code Coverage results against are expected to reside in sub folder `RXN3\MF_Source`. (The latter requires that the sources of the programs in question have been downloaded already to the specified folder.)

```bat
@echo off

REM
REM Script to download CoCo results using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "targetFolder=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET "repo=HDDRXM0.DEMO.COCO.REPOS"
SET "test=646"
SET "system=RXN3"
SET "sources=RXN3\MF_Source"

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"CodeCoverageCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -targetFolder %targetFolder% -data %workspace% -cc.repos %repo% -cc.test %test% -cc.system %system% -cc.sources %sources%
```

### SCMDownloaderCLI.bat

Parameter | Description
--------- | -----------
`-ext <arg>` | the file extension for the downloaded source files
`-filter <arg>` | the filter patterns for the source location on the host
`-ispwComponentType <arg>` | the ISPW component type
`-ispwContainerName <arg>` | the ISPW container name
`-ispwContainerType <arg>` | the ISPW container type (0 - assignment, 1 - release, 2 - set)
`-ispwDownloadAll <arg>` | Whether to keep the workspace in sync
`-ispwFilterFiles <arg>` | the ISPW filter files checkbox
`-ispwFilterFolders <arg>` | the ISPW filter folders checkbox
`-ispwFolderName <arg>` | the ISPW folder name
`-ispwLevelOption <arg>` | the ISPW level option (0 - selected level only, 1 - level and above)
`-ispwServerApp <arg>` | the ISPW server application
`-ispwServerConfig <arg>` | the ISPW server config
`-ispwServerLevel <arg>` | the ISPW server level
`-ispwServerStream <arg>` | the ISPW server stream
`-scm <arg>` | the source code management type (ispw - repository downloader, ispwc - container downloader, endevor - Endevor downloader)

#### Example using the ISPW container downloader
This example will download all COBOL components and copybooks from ISPW assignment (`-ispwContainerType "0"`) `RXN3000007`, using the container downloader (`-scm "ispwc"`). Sources will be downloaded, regardless if they have been changed or not (`ispwDownloadAll "true"`)
The resources reside on host `my.mainframe.host`, communicating on port `16196`. 
The downloaded sources will end up in the sub folder `<application>/MF_Source` (in the example `RXN3\MF_Source`) of the specified target folder name.

```bat
@echo off

REM
REM Script to download sources from ISPW using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET "scm=ispwc"
SET "container=RXN3000007"
SET "contType=0"
SET "downloadAll=true"

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"SCMDownloaderCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -targetFolder %workspace% -data %workspace% -scm %scm% -ispwContainerName %container% -ispwContainerType %contType% -ispwDownloadAll %downloadAll%
```

#### Example using the ISPW repository downloader
This example will download all COBOL components and copybooks from ISPW stream `FTSDEMO`, application `RXN3`, from level `DEV1` only (`-ispwLevelOption "0"`), using the repository downloader (`-scm "ispw"`). Sources will be downloaded, regardless if they have been changed or not (`ispwDownloadAll "true"`)
The resources reside on host `my.mainframe.host`, communicating on port `16196`. 
The downloaded sources will end up in the sub folder `<application>/MF_Source` (in the example `RXN3\MF_Source`) of the specified target folder name.

```bat
@echo off

REM
REM Script to download sources from ISPW using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "targetFolder=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET "scm=ispw"
SET "stream=FTSDEMO"
SET "application=RXN3"
SET "level=DEV1"
SET "levelOption=0"
SET "filterFiles=true"
SET "filterFolders=false"
SET compType="COB,COPY"
SET "downloadAll=true"

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"SCMDownloaderCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -targetFolder %targetFolder% -data %workspace% -scm %scm% -ispwServerStream %stream% -ispwServerApp %application% -ispwServerLevel %level% -ispwLevelOption %levelOption% -ispwFilterFiles %filterFiles% -ispwFilterFolders %filterFolders% -ispwComponentType %compType% -ispwDownloadAll %downloadAll%
```

#### Example using the PDS downloader
This example will download all members from PDS `'SALESSUP.RXN3.DEV1.COB'`, using the PDS downloader (`-scm "ispw"`). Sources will be downloaded, regardless if they have been changed or not (`ispwDownloadAll "true"`)
The resources reside on host `my.mainframe.host`, communicating on port `16196`. The results will be downloaded to the workspace. 
The downloaded sources will end up in specified target folder in one sub folder per PDS in the list that contained members. 

E.g. 
```
<workspace-root>
+- RXN3
    +- MF_Source_PDS
        +- SALESSUP.RXN3.DEV1.COB
        +- SALESSUP.RXN3.PRD.COB
```
if there are no members in the `QA1` or `STG` PDS.

```bat
@echo off

REM
REM Script to download sources from PDS using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "targetFolder=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace\RXN3\MF_Source_PDS"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET "scm=pds"
SET filter="SALESSUP.RXN3.DEV1.COB,SALESSUP.RXN3.QA1.COB,SALESSUP.RXN3.STG.COB,SALESSUP.RXN3.PRD.COB"
SET extension=cbl

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"SCMDownloaderCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -targetFolder %targetFolder% -data %workspace% -scm %scm% -filter %filter% -ext %extension%
```

### SubmitJclCLI.bat

Parameter | Description
--------- | -----------
`-jcl <arg>` | a comma separated list of jcl lines to submit as a JCL job.
`-jcldsns <arg>` | a comma separated list of sequential datasets or PDS(MEMBER) names to submit as JCL jobs.
`-maxcc <arg>` | the maximum job condition code which will allow JCL submissions to continue.

#### Submit JCL residing on the mainframe
This example will submit two jobs on host `my.mainframe.host`, communicating on port `16196`. If the return code of any of the jobs is greater than `4` the subsequent jobs will not be submitted and the pipeline will fail with an `error`.

```bat
@echo off

REM
REM Script to download sources from PDS using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET maxcc=4
SET jclMems="HDDRXM0.DEMO.JCL(CWXTJCLC),HDDRXM0.DEMO.JCL(CWXTIMS)"

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"SubmitJclCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -data %workspace% -maxcc %maxcc% -jcldsns %jclMems%
```

#### Submit JCL residing locally in a file
This example will submit a JCL that resides locally in file `C:\temp\JCL.txt` on host `my.mainframe.host`, communicating on port `16196`. If the return code of any of the jobs is greater than `4` the pipeline will fail with an `error`.

```bat
@echo off

REM
REM Script to download sources from PDS using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET maxcc=4
SET "jclFile=c:\temp\JCL.txt"

SET "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101"

"%CLIPath%"SubmitJclCLI.bat -host %host% -port %port% -id %user% -pass %pw% -code %codepage% -timeout "0" -data %workspace% -maxcc %maxcc% -jcl %jclFile%
```

### TotalTestCLI.bat
The general syntax for using the `TotalTestCLI.bat` is `TotalTestCLI.bat -cmd=<command> options` with the following set of possible commands. The most important command is the `runtest` command, which will excute all of the other commands in sequence. Each command uses an individual set of options.

Command | Description
--------- | -----------
`build` | Parses a test suite or test scenario and creates the required binary files.
`upload` | Uploads the parsed local binary files to the target host system.
`submit` | Submit JCL to run a test.
`download` | Downloads the test results from the target host to a local file.
`parse` | Parses the local binary result file and updates the archive (history).
`resultscheck` | Parses the updated archive  (history), applying the check conditions to the output data and updating the archive accordingly.
`runtest` | Performs all the above commands (in the specified order).

A challenge with using the individual commands in sequence - instead of using the `runtest` command - is that using the `upload`, the CLI will 'randomly' generate names for the target binary files on the mainframe (To be more precise, the 'ID' qualifier is generated randomly - other than using Topaz for Total Test from the GUI, where these qualifiers will be increased in numerical sequence, if the files are not replaced anyway). Currently, there is no known, simple way to control these names or determine which names have been generated. Therefore, we will concentrate on the `runtest` command, and refer to the Topaz for Total Test online help for further information on using any of the other commands for now.

#### Prameters/options for `runtest`

Parameter/Option | Description
--------- | -----------
`-project` | The Total Test project folder.
`-testsuite` | The name of a test suite from the Suites folder or the name of a test scenario from the Scenario folder.
`-testsuitelist` | Specifies a comma delimted list of test scenarios/suites names to be run. Test scenarios/suites names can contain the wildcard characters asterisk (*) to indicate any characters or a question (?) to indicate a single character. 'All_Scenarios' can be used to run all scenarios. 'All_Suites' can be used to run all test suites.
`-jcl` | The name of a JCL file from the JCL folder.
`-jcldsn` | The name of a dataset containing the JCL to submit.
`-targetencoding` | The character encoding (charset) used on the target platform. Default is '1047'.
`-noreport` | If specified with the -p option, no report file will be created.
`-noresult` | If specified with the -p option, no result file will be created.
`-nojunit` | If specified with the -p option, no JUnit file will be created.
`-nosonar` | If specified with the -p option, no Sonar file will be created.
`-ccrepository` | The name of the Code Coverage repository dataset. Must be specified to enable Code Coverage.
`-ccsystem` | Code Coverage system. If not specified with '-ccrepo', defaults to the test suite or test scenario name.
`-cctestid` | Code Coverage test id. If not specified with '-ccrepo', defaults to the test suite or test scenario name.
`-ccstepname` | Specifies the Topaz for Total Test step name. Should be used if the Topaz for Total Test step is contained in a cataloged procedure.
`-ccprogramtype` | Specifies the main executable program (this is the program specified on the 'EXEC PGM=' JCL statement in runner*.JCL) for Code Coverage. Specify: (-cctype=DB2 when the main program is IKJEFT01 or IKJEFT1B for live Db2, -cctype=TOTALTEST when the main program is TTTRUNNR, -cctype=IMS when the main program is DFSRRC00 for live IMS)
`-ccclearstats` | Specifies whether the Code Coverage repository statistics should be cleared before running the test. Valid values are 'true' or 'false'. The default value is 'true'.
`-externaltoolsworkspace` | Specifies the workspace of an external tool. This argument requires the 'postruncommands' argument be specified.
`-postruncommands` | Specifies the commands to be run after test completion. Currently only 'CopyJUnit' and 'CopySonar' are supported. If both are specified, they should be separated by a comma. This command requires the 'externaltoolsworkspace' argument be specified. 'CopyJUnit' will copy the JUnit results to the directory TTTJUnit, in the location specified by the external tools workspace argument. 'CopySonar' will copy the Sonar results to the directory TTTSonar, in the location specified by the external tools workspace argument.

The followwing example will execute the suite `CWXTSUBC.testsuite` residing in Topaz for Total Test project `project=C:\Users\cwde-rnuesse\Compuware\Topaz\Workspace\Unit CWXTSUBC 1.0`, on host `my.mainframe.host`, communicating on port `16196`, using the `Runner.jcl` file for the job to submit.

```bat
@echo off

REM
REM Script to download sources from PDS using the Topaz CLI
REM 
REM **********************************************************
REM Configuration Variables for the Script
REM
REM Change according to your environment
REM 
REM **********************************************************
REM
REM CLIPath  Installation Path of your Topaz CLI 
REM
REM JAVA_HOME Installation Path of Java
 
SET "CLIPath=C:\Program Files\Compuware\Topaz Workbench CLI 1921\"
SET "workspace=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace"
SET "host=my.mainframe.host"
SET "port=16196"
SET "codepage=1047"
SET "user=++++++++"
SET "pw=********"

SET "project=C:\Users\cwde-rnuesse\Compuware\Topaz\Workspace\Unit CWXTSUBC 1.0"
SET "suite=CWXTSUBC"
SET "JCL=Runner.jcl"

"%CLIPath%"TotalTestCLI.bat -data=C:\Users\cwde-rnuesse\Compuware\Topaz\TopazCLIWorkspace -cmd=runtest -host=%host% -port=%port% -user=%user% -pw=%password% "-project=%project%" "-testsuite=%suite%.testsuite" -te=%codepage% "-j=%JCL%"
```
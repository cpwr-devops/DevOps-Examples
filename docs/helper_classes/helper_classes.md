--- 
title: Helper classes
layout: helper_classes
---
# <a id="Helper classes"></a> Helper classes
The helper classes primarily serve as wrapper classes for the use of the different methods used by the plugins. Other serve purposes like provided configuration data (`PipelineConfig`) or preparing `JCL` for one specific execution of a pipeline (`JclSkeleton`).

## <a id="FileHelper"></a> FileHelper
The [`FileHelper`](./FileHelper.html) class is used to read external files and provides the methods

[`FileHelper(steps)`](./FileHelper.html#FileHelper)

> The constructor recieves the `steps` from the pipeline to [allow use of pipeline step within the class code]().

[`readLines(String path)`](./FileHelper.html#readLines)

> reads the records of a file at location `path` and returns an `ArrayList` of the individual records.

## <a id="GitHelper"></a> GitHelper
The [`GitHelper`](./GitHelper.html) class serves as a wrapper around the Git SCM plugin and provides the methods

[`GitHelper(steps)`](./GitHelper.html#GitHelper)

> The constructor recieves the `steps` from the pipeline to [allow use of pipeline step within the class code]().

[`checkout(String gitUrl, String gitBranch, String gitCredentials, String tttFolder)`](./GitHelper.html#checkout)

> checks out the branch `gitBranch` in the Git(Hub) repository at `gitUrl`. It uses the `gitCredentials` to authenticate, and places the cloned Git repository into the folder `tttFolder` (within the Jenkins workspace).

[`checkoutPath(String gitUrl, String gitBranch, String path, String gitCredentials, String gitProject)`](./GitHelper.html#checkoutPath)

> performs a *sparse checkout*, and checks out path `path` in the branch `gitBranch` in the project `gitProject` in the Git(Hub) repository at `gitUrl`. It uses the `gitCredentials` to authenticate.

## <a id="IspwHelper"></a> IspwHelper
The [`IspwHelper`](./IspwHelper.html) class serves as a wrapper around the Compuware ISPW plugin and provides the methods

`IspwHelper(steps, pConfig)`

> The constructor recieves the `steps` from the pipeline to [allow use of pipeline step within the class code]() and a [`PipelineConfig`](./PipelineConfig.html) to make use of pipeline execution specific parameters.

[`downloadSources()`](./IspwHelper.html#downloadSources)

> downloads all sources (COBOL programs and copybooks) contained in [ISPW set](../pipelines/scenario/ISPW_scenario.html#The resulting set) triggering the pipeline

[`downloadCopyBooks(String workspace)`](./IspwHelper.html#downloadCopyBooks)

> - recieves the path to the `workspace` of the pipeline job
> - uses the `referencedCopyBooks` method to determine all copybooks used by the download COBOL programs
> - uses a [`JclSkeleton`](#JclSkeleton) object's `createIebcopyCopyBooksJcl` method to create an `IEBCOPY` job `JCL` that copies all required copybooks in the list from the ISPW libraries into a temporary PDS
> - submits this `JCL` using the [Topaz Utilities](https://wiki.jenkins.io/display/JENKINS/Compuware+Topaz+Utilities+Plugin) plugin
> - downloads the content of the temporary PDS, using the [ISPW PDS downloader](https://wiki.jenkins.io/display/JENKINS/Compuware+Source+Code+Download+for+Endevor,+PDS,+and+ISPW+Plugin)
> - uses the `JclSkeleton` method `jclSkeleton.createDeleteTempDsn` to create a `DELETE` job `JCL`
> - and submits that `JCL`

[`referencedCopyBooks(String workspace)`](./IspwHelper.html#referencedCopyBooks)

> - recieves the path to the `workspace` of the pipeline job
> - searches all `*.cbl` program sources in the folder containing all downloaded sources and builds a list of COBOL programs
> - for each program in the list it
    > - reads the source file
    > - scans the content for valid `COPY` statements (e.g. not comments)
    > - determines the referenced copybook 
    > - add each copybook to the list of copybooks
> - returns the resulting list of copybooks

[`regressAssignmentList(assignmentList, cesToken)`](./IspwHelper.html#regressAssignmentList)

> recieves a list of assignment IDs in `assignmentList`, the [CES Token]() in `cesToken` and calls method `regressAssignment` for each element of `assignmentList`

[`def regressAssignment(assignment, cesToken)`](./IspwHelper.html#regressAssignment)

> receives an Assignment ID in `assigment`, the [CES Token]() in `cesToken` and uses the ISPW REST API to regress the assignment

## <a id="JclSkeleton"></a> JclSkeleton
The [`JclSkeleton`](./JclSkeleton.html) allows the pipelines to customize pieces of `JCL` in certain, predefined ways. This allows changing e.g. `job cards`, `STEPLIB` concatenations and others during runtime. The `JCL` skeletons are read from folder ['./config/skels'](../config_files/Jcl_skeletons.html).



## <a id="PipelineConfig"></a> PipelineConfig

## <a id="SonarHelper"></a> SonarHelper

## <a id="TaskInfo"></a> TaskInfo

## <a id="TttAsset"></a> TttAsset

## <a id="TttHelper"></a> 
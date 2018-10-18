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

> ...

`downloadCopyBooks(String workspace)`

> ...

`referencedCopyBooks(String workspace)`

> ...

`regressAssignmentList(assignmentList, cesToken)`

> ...

`def regressAssignment(assignment, cesToken)`

> ...

## <a id="JclSkeleton"></a> JclSkeleton

## <a id="PipelineConfig"></a> PipelineConfig

## <a id="SonarHelper"></a> SonarHelper

## <a id="TaskInfo"></a> TaskInfo

## <a id="TttAsset"></a> TttAsset

## <a id="TttHelper"></a> 
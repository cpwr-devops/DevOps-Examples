--- 
title: Helper classes
layout: helper_classes
---
# <a id="Helper classes"></a> Helper classes
The helper classes primarily serve as wrapper classes for the use of the different methods used by the plugins. Other serve purposes like provided configuration data (`PipelineConfig`) or preparing `JCL` for one specific execution of a pipeline (`JclSkeleton`).

## <a id="FileHelper"></a> FileHelper
The `FileHelper` class is used to read external files and provides the methods

`readLines(String path)`

reads the records of a file at location `path` and returns an `ArrayList` of the individual records.

## <a id="GitHelper"></a> GitHelper
The `GitHelper` class is as wrapper around the git SCM plugin and provides the methods

`checkout(String gitUrl, String gitBranch, String gitCredentials, String tttFolder)`

checks out the branch `gitBranch` in the Git(Hub) repository at `gitUrl`. It uses the `gitCredentials` to authenticate, and places the cloned Git repository into the folder `tttFolder` (within the Jenkins workspace).

`checkoutPath(String gitUrl, String gitBranch, String path, String gitCredentials, String gitProject)`

performs a *sparse checkout*, and checks out path `path` in the branch `gitBranch` in the project `gitProject` in the Git(Hub) repository at `gitUrl`. It uses the `gitCredentials` to authenticate.

## <a id="IspwHelper"></a> IspwHelper

## <a id="JclSkeleton"></a> JclSkeleton

## <a id="PipelineConfig"></a> PipelineConfig

## <a id="SonarHelper"></a> SonarHelper

## <a id="TaskInfo"></a> TaskInfo

## <a id="TttAsset"></a> TttAsset

## <a id="TttHelper"></a> 
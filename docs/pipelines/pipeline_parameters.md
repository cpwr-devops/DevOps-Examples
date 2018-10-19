---
title: Pipelines
layout: primary_pipelines
---
# <a id="The pipeline parameters"></a> The pipeline parameters
The two primary pipelines - and some of the other code examples - use a set of parameters that are taken from several sources - both names and values. 
This table documents, the different names the parameters appear under, how and where to define them, where the parameters are read from, how to determine which value to specify as used in the `PipelineConfig`class and the [Mainframe_CI_Pipeline_from_Shared_Lib](../pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.html).

<table>
    <tr>
        <th>Parameter / Description</th>
        <th>Name in PipelineConfig class</th>
        <th>Alias</th>
        <th>Passed to pipeline via</th>
        <th>Defined where</th>
        <th>How to determine</th>
    </tr>    
    <tr>
        <th>Runner jcl to use for unit test execution</th>
        <th><code class="highlighter-rouge">tttJcl</code></th>
        <th>N/A</th>
        <th><a href="../tool_configuration/Config_Files.html#The email list">Email List configuration file <code class="highlighter-rouge">mailRecipient</code></a></th>
        <th><a href="../tool_configuration/tool_configuration.html#Managed Files"><code class="highlighter-rouge">Manage Jenkins</code> -> <code class="highlighter-rouge">Managed Files</code></a></th>
        <th>The email file contains TSO user : email address pairs. The owner of the ISPW set will be taken as lookup for the email address</code></th>
    </tr>
    <tr>
        <th>Recipient of emails sent by the pipeline informing the owner of the ISPW set about the results</th>
        <th><code class="highlighter-rouge">mailRecipient</code></th>
        <th>N/A</th>
        <th><a href="../tool_configuration/Config_Files.html#The email list">Email List configuration file <code class="highlighter-rouge">mailList.config</code></a></th>
        <th><a href="../tool_configuration/tool_configuration.html#Managed Files"><code class="highlighter-rouge">Manage Jenkins</code> -> <code class="highlighter-rouge">Managed Files</code></a></th>
        <th>The email file contains TSO user : email address pairs. The owner of the ISPW set will be taken as lookup for the email address</code></th>
    </tr>
    <tr>
        <th>Git project name of repository storing configuration files</th>
        <th><code class="highlighter-rouge">configGitProject</code></th>
        <th>N/A</th>
        <th>N/A</th>
        <th>Hardcoded in the class</th>
        <th>The configuration files are stored in the same Git project which stores the pipeline code itself.</th>
    </tr>
    <tr>
        <th>Git branch name of repository storing configuration files</th>
        <th><code class="highlighter-rouge">configGitBranch</code></th>
        <th><a href="../pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">Pipeline call</a></th>
        <th>Config_Git_Branch</th>
        <th>N/A</th>
        <th>The configuration files are stored in the same Git project which stores the pipeline code itself. You may use adifferent branch for the configuration files, though, if you want to store and use confiurations for e.g. different Jenkins instances</th>
    </tr>
    <tr>
        <th>Folder in the Git repository containing all configuration files</th>
        <th><code class="highlighter-rouge">configGitPath</code></th>
        <th>N/A</th>
        <th>N/A</th>
        <th>Hardcoded in the class</th>
        <th>The folder containing all configuration file is <code class="highlighter-rouge">(root)/config</code></th>
    </tr>
</table>

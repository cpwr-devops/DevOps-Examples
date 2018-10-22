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
        <td>Runner jcl to use for unit test execution</td>
        <td><code class="highlighter-rouge">tttJcl</code></td>
        <td>N/A</td>
        <td><a href="../tool_configuration/Config_Files.html#The email list">Email List configuration file <code class="highlighter-rouge">mailRecipient</code></a></td>
        <td><a href="../tool_configuration/tool_configuration.html#Managed Files"><code class="highlighter-rouge">Manage Jenkins</code> -> <code class="highlighter-rouge">Managed Files</code></a></td>
        <td>The email file contains TSO user : email address pairs. The owner of the ISPW set will be taken as lookup for the email address</code></td>
    </tr>
    <tr>
        <td>Recipient of emails sent by the pipeline informing the owner of the ISPW set about the results</td>
        <td><code class="highlighter-rouge">mailRecipient</code></td>
        <td>N/A</td>
        <td><a href="../tool_configuration/Config_Files.html#The email list">Email List configuration file <code class="highlighter-rouge">mailList.config</code></a></td>
        <td><a href="../tool_configuration/tool_configuration.html#Managed Files"><code class="highlighter-rouge">Manage Jenkins</code> -> <code class="highlighter-rouge">Managed Files</code></a></td>
        <td>The email file contains TSO user : email address pairs. The owner of the ISPW set will be taken as lookup for the email address</code></td>
    </tr>
    <tr>
        <td>Git project name of repository storing configuration files</td>
        <td><code class="highlighter-rouge">configGitProject</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The configuration files are stored in the same Git project which stores the pipeline code itself.</td>
    </tr>
    <tr>
        <td>Git branch name of repository storing configuration files</td>
        <td><code class="highlighter-rouge">configGitBranch</code></td>
        <td><a href="../pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">Pipeline call</a></td>
        <td>Config_Git_Branch</td>
        <td>N/A</td>
        <td>The configuration files are stored in the same Git project which stores the pipeline code itself. You may use adifferent branch for the configuration files, though, if you want to store and use confiurations for e.g. different Jenkins instances</td>
    </tr>
    <tr>
        <td>Folder in the Git repository containing all configuration files</td>
        <td><code class="highlighter-rouge">configGitPath</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The folder containing all configuration file is <code class="highlighter-rouge">(root)/config</code></td>
    </tr>
</table>

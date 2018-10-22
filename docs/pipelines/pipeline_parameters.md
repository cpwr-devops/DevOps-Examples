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
        <td>The path through the development life cycle which is in use by the set triggering the pipeline</td>
        <td><code class="highlighter-rouge">public String applicationPathNum</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Determined from the name of the level <code class="highlighter-rouge">ispwSrcLevel</code> the sources have been promoted from; the number of the levels <code class="highlighter-rouge">DEV1</code>, <code class="highlighter-rouge">DEV2</code>, or <code class="highlighter-rouge">DEV3</code></td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>Name of the GitHub project, used to store Topaz for Total Test projects</td>
        <td><code class="highlighter-rouge">public String gitProject</code></td>
        <td><code class="highlighter-rouge">Git_Project</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td>When setting up the GitHub repository</td>
        <td>In the full URL to e.g. https://github.com/ralphnuessecpwr/FTSDEMO_RXN3_Unit_Tests.git, "ralphnuessecpwr" would be the project name</td>
    </tr>
    <tr>
        <td>Jenkins credentials token to use to authenticate with GitHub</td>
        <td><code class="highlighter-rouge">public String gitCredentials</code></td>
        <td><code class="highlighter-rouge">Git_Credentials</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td><a href="../../tool_configuration/tool_configuration.html#Credentials">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a></td>
        <td>In the list at <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a> in column <code class="highlighter-rouge">ID</code></td>
    </tr>
    <tr>
        <td>The name of the GitHub project, storing the repository containing the Topaz for Total Test projects</td>
        <td><code class="highlighter-rouge">public String gitUrl</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Built as "https://github.com/${gitProject}" from <code class="highlighter-rouge">gitProject</code></td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The name of the GitHub repository containing the Topaz for Total Test projects</td>
        <td><code class="highlighter-rouge">public String gitTttRepo</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Built as "${ispwStream}_${ispw_application}_Unit_Tests.git" from <code class="highlighter-rouge">ispwStream</code> and <code class="highlighter-rouge">ispwApplication</code></td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The Jenkins credential token for the CES token as used by most Compuware plugins</td>
        <td><code class="highlighter-rouge">public String cesTokenId</code></td>
        <td><code class="highlighter-rouge">CES_Token</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td><a href="../../tool_configuration/tool_configuration.html#Credentials">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a></td>
        <td>In the list at <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a> in column <code class="highlighter-rouge">ID</code></td>
    </tr>
    <tr>
        <td>The connection configuration storing host name and port for the connection to the mainframe LPAR to cnnect to</td>
        <td><code class="highlighter-rouge">public String hciConnId</code></td>
        <td><code class="highlighter-rouge">HCI_Conn_ID</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td><a href="../../tool_configuration/Compuware_confugurations.html#Compuware Configurations">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Configure System</code> --&gt; <code class="highlighter-rouge">Compuware Configurations</code></a></td>
        <td>
            Use <code class="highlighter-rouge">Pipeline Syntax</code>, e.g. to define an ISPW container checkout and select the HCO connection from the <code class="highlighter-rouge">Host connection</code> dropdown
            <img src="./pipelines/images/Determine HCI Conn.png" alt="Determine HCI connection" />
        </td>
    </tr>
    <tr>
        <td>The user ID / password token for a valid logon to the required mainframe LPAR used by plugins that do not use the CES credentials token</td>
        <td><code class="highlighter-rouge">public String hciTokenId</code></td>
        <td><code class="highlighter-rouge">HCI_Token</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td><a href="../../tool_configuration/tool_configuration.html#Credentials">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a></td>
        <td>In the list at <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Credentials</code></a> in column <code class="highlighter-rouge">ID</code></td>
    </tr>
    <tr>
        <td>The Xpediter Code Coverage repository to use</td>
        <td><code class="highlighter-rouge">public String ccRepository</code></td>
        <td><code class="highlighter-rouge">CC_repository</code></td>
        <td><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></td>
        <td>The Xpediter Code Coverage repository is defined using Xpediter Code Coverage or Topaz Workbench.</td>
        <td>The administrator of Xpediter Code Coverage</td>
    </tr>
    <tr>
        <td>Runner jcl to use for unit test execution</td>
        <td><code class="highlighter-rouge">public String tttJcl</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Built as "Runner_PATH${applicationPathNum}.jcl" using <code class="highlighter-rouge">applicationPathNum</code></td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>Recipient of emails sent by the pipeline informing the owner of the ISPW set about the results</td>
        <td><code class="highlighter-rouge">public String mailRecipient</code></td>
        <td>N/A</td>
        <td><a href="../tool_configuration/Config_Files.html#The email list">Email List configuration file <code class="highlighter-rouge">mailList.config</code></a></td>
        <td><a href="../tool_configuration/tool_configuration.html#Managed Files"><code class="highlighter-rouge">Manage Jenkins</code> -> <code class="highlighter-rouge">Managed Files</code></a></td>
        <td>The email file contains TSO user : email address pairs. The owner of the ISPW set will be taken as lookup for the email address</td>
    </tr>
    <tr>
        <td>Git project name of repository storing configuration files</td>
        <td><code class="highlighter-rouge">private String configGitProject</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The configuration files are stored in the same Git project which stores the pipeline code itself.</td>
    </tr>
    <tr>
        <td>Git branch name of repository storing configuration files</td>
        <td><code class="highlighter-rouge">private String configGitBranch</code></td>
        <td><a href="../pipelines/Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">Pipeline call</a></td>
        <td>Config_Git_Branch</td>
        <td>N/A</td>
        <td>The configuration files are stored in the same Git project which stores the pipeline code itself. You may use adifferent branch for the configuration files, though, if you want to store and use confiurations for e.g. different Jenkins instances</td>
    </tr>
    <tr>
        <td>Folder in the Git repository containing all configuration files</td>
        <td><code class="highlighter-rouge">private String configGitPath</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The folder containing all configuration file is <code class="highlighter-rouge">(root)/config</code></td>
    </tr>
</table>

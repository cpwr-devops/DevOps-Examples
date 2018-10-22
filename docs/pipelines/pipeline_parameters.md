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
        <td>Path to the conifguration files after downloading from GitHub to the Jenkins workspace</td>
        <td><code class="highlighter-rouge">private String configPath</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The folder containing all configuration file is <code class="highlighter-rouge">(root)/config/pipeline</code></td>
    </tr>
    <tr>
        <td>Name of the config file for pipeline and environment specific settings</td>
        <td><code class="highlighter-rouge">private String pipelineConfigFile</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The file name is <code class="highlighter-rouge">pipeline.config</code></td>
    </tr>
    <tr>
        <td>Name of the config file for the GitHub repository storing Topaz for Total Test projects</td>
        <td><code class="highlighter-rouge">private String tttGitConfigFile</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Passed via the class constructor</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>Name of current Jenkins workspace</td>
        <td><code class="highlighter-rouge">private String workspace</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Hardcoded in the class</td>
        <td>The file name is <code class="highlighter-rouge">tttgit.config</code></td>
    </tr>
    <tr>
        <td>Git/GitHub branch to merge Topaz for Total Test unit tests into after successful execution of the pipeline.</td>
        <td><code class="highlighter-rouge">public String gitTargetBranch</code></td>
        <td><code class="highlighter-rouge">TTT_GIT_TARGET_BRANCH</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">tttgit.config</code></a></li></td>
        <td>Git/GitHub</td>
        <td>This is not used in the standard example pipeline. Some of the other code examples demonstrate how to use this. If not used, the value may remain empty</td>
    </tr>
    <tr>
        <td>Git/GitHub branch to use when downloading Topaz for Total Test unit tests</td>
        <td><code class="highlighter-rouge">public String gitBranch</code></td>
        <td><code class="highlighter-rouge">TTT_GIT_BRANCH</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">tttgit.config</code></a></li></td>
        <td>Git/GitHub</td>
        <td>Your project administrator</td>
    </tr>
    <tr>
        <td>SonarQube scanner configuration name within Jenkins</td>
        <td><code class="highlighter-rouge">public String sqScannerName</code></td>
        <td><code class="highlighter-rouge">SQ_SCANNER_NAME</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td><a href="../tool_configuration/SonarQube_server.html">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Global Tool Configuration</code></a> --&gt; <code class="highlighter-rouge">SonarQube Scanner</code></a></td>
        <td>As <code class="highlighter-rouge">Name</code></td>
    </tr>
    <tr>
        <td>SonarQube server name within Jenkins</td>
        <td><code class="highlighter-rouge">public String sqServerName</code></td>
        <td><code class="highlighter-rouge">SQ_SERVER_NAME</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td><a href="../tool_configuration/SonarQube_server.html">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Configure System</code></a> --&gt; <code class="highlighter-rouge">Sonar Qube servers</code></a></td>
        <td>As <code class="highlighter-rouge">Name</code></td>
    </tr>
    <tr>
        <td>URL of the SonarQube server</td>
        <td><code class="highlighter-rouge">public String sqServerUrl</code></td>
        <td><code class="highlighter-rouge">SQ_SERVER_URL</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>URL of the SonarQube server</td>
        <td>SonarQube administrator</td>
    </tr>
    <tr>
        <td>Folder in the application folder, containg sources downloaded via the ISPW downloader plugin</td>
        <td><code class="highlighter-rouge">public String mfSourceFolder</code></td>
        <td><code class="highlighter-rouge">MF_SOURCE_FOLDER</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>Determined by the ISPW plugin</td>
        <td>Will always be <code class="highlighter-rouge">MF_Source</code></td>
    </tr>
    <tr>
        <td>XL Release template to use for creation of a new XL Release release</td>
        <td><code class="highlighter-rouge">public String xlrTemplate</code></td>
        <td><code class="highlighter-rouge">XLR_TEMPLATE</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>As template in XL Release</td>
        <td>XL Release administrator</td>
    </tr>
    <tr>
        <td>XL Release credentials token to use for creation of a new XL Release release</td>
        <td><code class="highlighter-rouge">public String xlrUser</code></td>
        <td><code class="highlighter-rouge">XLR_USER</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td><a href="../tool_configuration/XLR_config.html">In <code class="highlighter-rouge">Manage Jenkins</code> --&gt; <code class="highlighter-rouge">Configure System</code></a> --&gt; <code class="highlighter-rouge">XL Release</code></a></td>
        <td>As <code class="highlighter-rouge">Credentials name</code></td>
    </tr>
    <tr>
        <td>Folder in the Jenkins workspace that will contain the Topaz for Total Test project fodlers after downloading from GitHub</td>
        <td><code class="highlighter-rouge">public String tttFolder</code></td>
        <td><code class="highlighter-rouge">TTT_FOLDER</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>Free choice. The examples use <code class="highlighter-rouge">tests</code></td>
        <td>Free choice</td>
    </tr>
    <tr>
        <td>CES URL to use when executing raw http requests against the ISPW REST API</td>
        <td><code class="highlighter-rouge">public String ispwUrl</code></td>
        <td><code class="highlighter-rouge">ISPW_URL</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>URL of the CES</td>
        <td>CES/ISPW administrator</td>
    </tr>
    <tr>
        <td>The ISPW runtime being used by the ISPW installtion</td>
        <td><code class="highlighter-rouge">public String ispwRuntime</code></td>
        <td><code class="highlighter-rouge">ISPW_RUNTIME</code></td>
        <td><a href="">As parameter in the config file <code class="highlighter-rouge">pipeline.config</code></a></li></td>
        <td>During ISPW configuration</td>
        <td>ISPW administrator. The default is "ispw"</td>
    </tr>
    <tr>
        <td>The ISPW stream that contains the tasks that were promoted</td>
        <td><code class="highlighter-rouge">public String ispwStream</code></td>
        <td><code class="highlighter-rouge">ISPW_Stream</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Stream</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as value "<code class="highlighter-rouge">FTSDEMO</code>"</a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The ISPW application that contains the tasks that were promoted</td>
        <td><code class="highlighter-rouge">public String ispwApplication</code></td>
        <td><code class="highlighter-rouge">ISPW_Application</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Application</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$application$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The ISPW release that contains the tasks that were promoted</td>
        <td><code class="highlighter-rouge">public String ispwRelease</code></td>
        <td><code class="highlighter-rouge">ISPW_Release</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Release</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$release$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The ISPW assignment that contains the tasks that were promoted</td>
        <td><code class="highlighter-rouge">public String ispwAssignment</code></td>
        <td><code class="highlighter-rouge">ISPW_Assignment</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Assignment</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$assignment$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The internal ID of the container/set triggering the pipeline</td>
        <td><code class="highlighter-rouge">public String ispwContainer</code></td>
        <td><code class="highlighter-rouge">ISPW_Container</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Container</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$container$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The type of container being passed by the ISPW webhook</td>
        <td><code class="highlighter-rouge">public String ispwContainerType</code></td>
        <td><code class="highlighter-rouge">ISPW_Container_Type</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Container_Type</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as value 2: The pipeline expects a set container.</li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The level in the ISPW life cycle, the sources were promoted from</td>
        <td><code class="highlighter-rouge">public String ispwSrcLevel</code></td>
        <td><code class="highlighter-rouge">ISPW_Src_Level</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Src_Level</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$level$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The level in the ISPW life cycle, the sources were promoted to</td>
        <td><code class="highlighter-rouge">public String ispwTargetLevel</code></td>
        <td>N/A</td>
        <td>N/A</td>
        <td>Built as "QA${applicationPathNum}" from <code class="highlighter-rouge">applicationPathNum</code></td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The TSO user id of the user promoting the sources and thus triggering the pipeline</td>
        <td><code class="highlighter-rouge">public String ispwOwner</code></td>
        <td><code class="highlighter-rouge">ISPW_Owner</code></td>
        <td>
            <ul>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Loading the script from a shared library">As parameter in the call of the pipeline</a></li>
                <li><a href="./Mainframe_CI_Pipeline_from_Shared_Lib.html#Setting up the pipeline job">As parameter <code class="highlighter-rouge">ISPW_Owner</code> for the pipeline</a></li>
                <li><a href="../tool_configuration/webhook_setup.html#URL">Via the ISPW Webhook as parameter <code class="highlighter-rouge">$$owner$$</code></a></li>
            </ul>
        </td>
        <td>N/A</td>
        <td>N/A</td>
    </tr>
    <tr>
        <td>The number of the path through the development life cycle which is in use by the set triggering the pipeline</td>
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

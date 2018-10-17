# <a id=""></a> Pipelines
These examples of Jenkins pipelines make use of Compuware's and other plugins to implement the following process and scenario. They may be seen a kind of **standard approach** for implementing a CI process for maingframe development. The scenario(s) and code are based on request and requirements from customers who already have started implementing their own pipeline and asked Compuware for advice. The code itself shows our solution to these, while the respective solutions as implemented by our customers are - in parts considerably - different.

The code reflects common patterns that we see emerging at different customers. In addition, the examples are supposed to help mainframe developers to familiarize with [Groovy](http://groovy-lang.org/documentation.html) and its concepts, as well as demonstrate some of the [ideosynchrasies of the Jenkins Groovy dialect](./Jenkins_Groovy_Ideosynchrasies.html) that we stumbled across.

## <a id=""></a> Scenario Outside Jenkins
- A developer checks out a set of sources, copybooks and other components required to fulfill a specific requirement. These are stored and administered in [ISPW](../scenarios/ISPW_scenario.html#Checking out code to an assignment)
- The developer creates or modifies a set of unit tests using [Topaz for Total Test](../scenarios/TTT_scenario.html#Adding new test cases). In order to share the unit tests between development teams and to use them in Jenkins, the Topaz for Total Test projects are stored and administered using [Git/GitHub](../scenarios/TTT_in_Git.html) in the case of these examples, to be more precise.
- Once the developer has finished working on a set of pieces of code that are stored in a mainframe SCM -  [ISPW](../scenarios/ISPW_scenario.html#Promoting the code changes) in our examples - they promote their changes to the next level in the application's life cycle. 
- In our example this activity creates an [ISPW set container](../scenarios/ISPW_scenario.html#Promoting the code changes#The resulting set) containing all components that are part of this specific promotion.

## <a id=""></a> Inside Jenkins - the two primary pipelines
The [ISPW webhooks](../tool_configuration/webhhok_setup.html) will then trigger one of the following pipeline jobs
- [Mainframe-CI-Example-pipeline_config](./Mainframe-CI-Example-pipeline.html)
- [Mainframe_CI_Pipeline_from_Shared_Lib](./Mainframe_CI_Pipeline_from_Shared_Lib.html)
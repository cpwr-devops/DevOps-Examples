---
title: Pipelines
layout: primary_pipelines
---
# <a id=""></a> Pipelines
These examples of Jenkins pipelines make use of Compuware's and other plugins to implement the following process and scenario. They may be seen a kind of **standard approach** for implementing a CI process for maingframe development. The scenario(s) and code are based on request and requirements from customers who already have started implementing their own pipeline and asked Compuware for advice. The code itself shows our solution to these, while the respective solutions as implemented by our customers are - in parts considerably - different.

The code reflects common patterns that we see emerging at different customers. In addition, the examples are supposed to help mainframe developers to familiarize with [Groovy](http://groovy-lang.org/documentation.html) and its concepts, as well as demonstrate some of the [ideosynchrasies of the Jenkins Groovy dialect](./Jenkins_Groovy_Ideosynchrasies.html) that we stumbled across.

## <a id=""></a> Scenario Outside Jenkins
- A developer checks out a set of sources, copybooks and other components required to fulfill a specific requirement. In our example, these are stored and administered in [ISPW](./scenario/ISPW_scenario.html)
- The developer creates or modifies a set of unit tests using [Topaz for Total Test](./scenario/TTT_scenario.html). In order to share the unit tests between development teams and to use them in Jenkins, the Topaz for Total Test projects are stored and administered using [Git/GitHub](./scenario/TTT_in_Git.html) in the case of these examples, to be more precise.
- Once the developer has finished working on the code, they `promote` their changes to the next level in the application's life cycle. 
- In our example this activity creates an [ISPW set container](./scenario/ISPW_scenario.html#The resulting set) containing all components that are part of this specific promotion.

## <a id=""></a> Inside Jenkins - the two primary pipelines
The [ISPW webhooks](../tool_configuration/webhhok_setup.html) will then trigger one of the following pipeline jobs
- [Mainframe-CI-Example-pipeline](./Mainframe-CI-Example-pipeline.html)
- [Mainframe_CI_Pipeline_from_Shared_Lib](./Mainframe_CI_Pipeline_from_Shared_Lib.html)
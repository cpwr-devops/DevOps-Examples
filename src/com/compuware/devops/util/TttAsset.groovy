package com.compuware.devops.util

import hudson.model.*
import hudson.EnvVars

/**
 Object to store information about a TTT Asset
*/
class TttAsset
{
    String tttScenarioPath
    String tttProjectName
    String tttScenarioFullName
    String tttScenarioName
    String tttScenarioTarget

    TttAsset(file) 
    {        
    this.tttScenarioPath        = file.path // Fully qualified name of the Total Test Scenario file
    this.tttProjectName         = file.path.trim().split("\\\\")[0] + "\\"+ file.path.trim().split("\\\\")[1]  // Total Test Project name is the root folder of the full path to the testscenario 
    this.tttScenarioFullName    = file.name  // Get the full name of the testscenario file i.e. "name.testscenario"
    this.tttScenarioName        = file.name.trim().split("\\.")[0]  // Get the name of the scenario file without ".testscenario"
    this.tttScenarioTarget      = tttScenarioName.split("\\_")[0]  // Target Program will be the first part of the scenario name (convention)
    }
}
package com.compuware.devops.util

/**
 Wrapper around the Git Plugin's Checkout Method
 @param URL - URL for the git server
 @param Branch - The branch that will be checked out of git
 @param Credentials - Jenkins credentials for logging into git
 @param Folder - Folder relative to the workspace that git will check out files into
*/
class GitHelper implements Serializable {

    def steps

    GitHelper(steps) 
    {
        this.steps = steps
    }

    def checkout(String gitUrl, String gitBranch, String gitCredentials, String tttFolder)
    {
        steps.checkout(
            changelog:  false, 
            poll:       false, 
            scm:        [
                        $class:                                 'GitSCM', 
                            branches:                           [[name: "*/${gitBranch}"]], 
                            doGenerateSubmoduleConfigurations:  false, 
                            extensions:                         [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${tttFolder}"]], 
                            submoduleCfg:                       [], 
                            userRemoteConfigs:                  [[credentialsId: "${gitCredentials}", name: 'origin', url: "${gitUrl}"]]
                        ]
        )
    }

    def checkoutPath(String gitUrl, String gitBranch, String path, String gitCredentials, String gitProject)
    {
        steps.checkout(
        changelog: false, 
        poll: false, 
        scm: [
                $class: 'GitSCM', 
                branches: [[name: "*/${gitBranch}"]], 
                doGenerateSubmoduleConfigurations: false, 
                extensions: [[
                    $class: 'SparseCheckoutPaths', 
                    sparseCheckoutPaths: [[path: "${path}/*"]]
                ]], 
                submoduleCfg: [], 
                userRemoteConfigs: [[
                    credentialsId: "${gitCredentials}", 
                    url: "${gitUrl}/${gitProject}.git"
                ]]
            ]
        )
    }
}
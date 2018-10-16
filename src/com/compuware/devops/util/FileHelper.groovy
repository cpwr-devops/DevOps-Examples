package com.compuware.devops.util

/**
 Wrapper around the Git Plugin's Checkout Method
 @param URL - URL for the git server
 @param Branch - The branch that will be checked out of git
 @param Credentials - Jenkins credentials for logging into git
 @param Folder - Folder relative to the workspace that git will check out files into
*/
class FileHelper implements Serializable {

    def steps

    FileHelper(steps) 
    {
        this.steps = steps
    }

    def readLines(String path)
    {        
        
        File configFile = new File(path)

        if(!configFile.exists())
        {
            steps.error "File - ${path} - not found! \n Aborting Pipeline"
        }

        return configFile.readLines()

    }
}
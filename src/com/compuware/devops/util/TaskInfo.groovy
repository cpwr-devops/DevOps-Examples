package com.compuware.devops.util

/**
 Object to store information about an ISPW program task
*/
class TaskInfo
{
    public String programName
    public String baseVersion
    public String targetVersion
    public String ispwTaskId

    TaskInfo() 
    {        
        this.programName    = ''
        this.baseVersion    = '0'
        this.targetVersion  = '0'
        this.ispwTaskId     = ''
    }
}
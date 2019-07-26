package com.compuware.devops.util

/**
 Static Class to contain different JCL Skeletons
*/
class JclSkeleton implements Serializable {

    def steps

    private String skeletonPath         = 'skels'               // Path containing JCL "skeletons" after downloading them from Git Hub Repository 'config\\skels'
    private String jobCardSkel          = 'JobCard.jcl'         // Skeleton for job cards
    private String iebcopySkel          = 'iebcopy.skel'        // Skeleton for IEBCOPY job
    private String iebcopyInDdSkel      = 'iebcopyInDd.skel'    // Skeleton for input DDs for IEBCOPY job
    private String deleteDsSkel         = 'deleteDs.skel'       // Skeleton for deleting the PDS after downloading copy books
    private String cleanUpCcRepoSkel    = 'cleanUpCcRepo.skel'  // Skeleton for deleting the PDS after downloading copy books

    private String workspace

    String jobCardJcl
    String iebcopyCopyBooksJclSkel
    String cleanUpDatasetJclSkel
    String cleanUpCcRepoJclSkel
    String ispwApplication
    String ispwPathNum

    JclSkeleton(steps, String workspace, String ispwApplication, String ispwPathNum) 
    {
        this.steps              = steps
        this.workspace          = workspace
        this.ispwApplication    = ispwApplication
        this.ispwPathNum        = ispwPathNum
    }

    /* A Groovy idiosynchrasy prevents constructors to use methods, therefore class might require an additional "initialize" method to initialize the class */
    def initialize()
    {
        this.jobCardJcl                 = readSkelFile(jobCardSkel).join("\n")

        this.cleanUpDatasetJclSkel      = readSkelFile(deleteDsSkel).join("\n")

        this.iebcopyCopyBooksJclSkel    = buildIebcopySkel()

        this.cleanUpCcRepoJclSkel       = readSkelFile(cleanUpCcRepoSkel).join("\n")
    }

    def String buildIebcopySkel()
    {

        def jclSkel                 = readSkelFile(iebcopySkel).join("\n")
        
        def tempInputDdStatements   = readSkelFile(iebcopyInDdSkel)

        def copyDdStatements        = []

        for(int i=0; i < tempInputDdStatements.size(); i++)
        {                        
            copyDdStatements.add ("       INDD=IN${i+1}")
        }

        def inputDdJcl      = tempInputDdStatements.join("\n")

        def inputCopyJcl    = copyDdStatements.join("\n")

        jclSkel             = jclSkel.replace("<source_copy_pds_list>", inputDdJcl)
        jclSkel             = jclSkel.replace("<source_input_dd_list>", inputCopyJcl)
        jclSkel             = jclSkel.replace("<ispw_application>", ispwApplication)
        jclSkel             = jclSkel.replace("<ispw_path>", ispwPathNum)

        return jclSkel

    }

    def String createIebcopyCopyBooksJcl(String targetDsn, List copyMembers)
    {

        def selectStatements    = []

        copyMembers.each 
        {
            selectStatements.add("  SELECT MEMBER=${it}")
        }

        def selectJcl       = selectStatements.join("\n")  

        def iebcopyCopyBooksJcl =   buildFinalJcl(jobCardJcl,
                                        iebcopyCopyBooksJclSkel,
                                        [
                                            [
                                                parmName:   "<target_dsn>",
                                                parmValue:  targetDsn
                                            ],
                                            [
                                                parmName:   "<select_list>",
                                                parmValue:  selectJcl
                                            ]
                                        ]
                                    )
                                    
        return iebcopyCopyBooksJcl
    }

    def String createDeleteTempDsn(String targetDsn)
    {
        def deleteJcl   =   buildFinalJcl(jobCardJcl, 
                                cleanUpDatasetJclSkel,
                                [
                                    [
                                        parmName:      "<clean_dsn>", 
                                        parmValue:     targetDsn
                                    ]
                                ]
                            )

        return deleteJcl
    }

    def String createCleanUpCcRepo(String systemName, String testId)
    {
        def cleanUpJcl  =   buildFinalJcl(jobCardJcl, 
                                cleanUpCcRepoJclSkel,
                                [
                                    [
                                        parmName:      "<cc_sysname>", 
                                        parmValue:     systemName
                                    ],
                                    [
                                        parmName:      "<cc_test_id>", 
                                        parmValue:     testId                                    
                                    ]
                                ]
                            )

        return cleanUpJcl
    }

    private String buildFinalJcl(jobCard, jclSkel, parametersMap)
    {
        String finalJcl

        finalJcl    = jobCard
        finalJcl    = finalJcl + "\n" + jclSkel

        parametersMap.each
        {
            finalJcl    = finalJcl.replace(it.parmName, it.parmValue)
        }

        return finalJcl
    }

    def readSkelFile(String fileName)
    {
        def jclStatements   = []
        def skelFilePath    = "${skeletonPath}\\${fileName}"
        def fileText        = steps.libraryResource skelFilePath
        def lines           = fileText.tokenize("\n")
        
        lines.each
        {
            jclStatements.add(it.toString())
        }

        return jclStatements
    }
}
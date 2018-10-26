# <a id="JCL skeletons> JCL Skeletons
JCL skeletons in ISPF allow building mainframe jobs based on user input without the user having to code the complete JCL. They provide templates containing variables. At 'runtime' these variables get substituted by the input provided by the user. This principle is mimicked here. 

The skeleton JCL in our examples uses strings in brackets '<>' to identify placeholders which get substituted at runtime. Currently, these are fixed names and only these 'variables' can be used to subsitute placeholders by concrete values.

In total there are three pieces of JCL that get generated during runtime.

## <a id="JobCard.jcl"> A job card JobCard.jcl
The file `JobCard.jcl` contains a job card that will be used for jobs that get submitted on the mainframe from the pipeline. This way job that get executed by pipeline automation can be distinguished (and executed under different rights) than the normal "user related" job JCL that gets stored with the Topaz for Total test projects.
The current version of the `JobCard.jcl` does not provide any pipeline specific variable subsitution. Any valid JCL specific variable (e.g. `&SYSUID`) may still be used. 

## <a id="deleteDs.skel"> Delete temporary Dataset deleteDs.skel
The purpose of this JCL is to submit a job that deletes a dataset. In the context of the pipelines this dataset is supposed to be temporary in use (and contains copybook members that need to be downloaded). The skeleton looks like this
```
//CLEAN   EXEC PGM=IEFBR14
//DELETE DD DISP=(SHR,DELETE,DELETE),DSN=<clean_dsn>
```

The placeholder `<clean_dsn>` will be replaced by a concrete dataset name during runtime.

## <a id="iebcopy.skel"> IEBCOPY job iebcopy.skel
The purpose of this JCL is to copy all members that have been identified a copybooks from the ISPW libraries to a temporary PDS. (These will then be downloaded by the ISPW PDS downloader.) The skeleton looks like this
```
//COPY    EXEC PGM=IEBCOPY
//SYSPRINT DD SYSOUT=*
//SYSUT3   DD UNIT=SYSDA,SPACE=(TRK,(10,10))
//SYSUT4   DD UNIT=SYSDA,SPACE=(TRK,(10,10))
<source_copy_pds_list>
//OUT      DD DISP=(,CATLG,DELETE),
//            DSN=<target_dsn>,
//            UNIT=SYSDA,
//            SPACE=(TRK,(10,20,130)),
//            DCB=(RECFM=FB,LRECL=80)
//SYSIN DD *
  COPY OUTDD=OUT
<source_input_dd_list>
<select_list>
```

The placeholders are 
- `<source_copy_pds_list>` will be replaced by a list of `//INx` DD statements pointing to the copy library concatenation list that is to be used to search the copybooks. The libraries will be concatenated according to the list defined by the next skeleton `iebcopyInDd.skel`.
- `<target_dsn>` will be replaced by the temporary PDS to receive the copybook members. The name is taken from variable ``
- `<source_input_dd_list>` will be replaced by a set of `INDD=INx` statements for IEBCOPY. The set corresponds with the set of `//INx` DD statements defined by the `iebcopyInDd.skel`.
- `<select_list>` will be replaced by a set of `SELECT` statements for IEBCOPY. The set is determined from the list of copybook members to be downloaded.

## <a id="iebcopyInDd.skel"> List in IN DD statements iebcopyInDd.skel
The purpose of this file is to provide a list of datasets to use a input datasets for the `iebcopy.skel` JCL. The skeleton looks like this and corresponds with the libraries used to story copybooks in the ISPW environment used for these examples

```
//IN1      DD DISP=SHR,DSN=SALESSUP.<ispw_application>.QA<ispw_path>.CPY
//IN2      DD DISP=SHR,DSN=SALESSUP.<ispw_application>.STG.CPY
//IN3      DD DISP=SHR,DSN=SALESSUP.<ispw_application>.PRD.CPY
```

The placeholders are 
- `<ispw_application>` will be replaced by the ISPW application being passed to the pipeline.
- `<ispw_path>` will be replaced by the path through the life cycle to be used.
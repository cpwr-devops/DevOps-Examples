package com.compuware.devops.util

/**
 Wrapper around the XLR Plugin
*/
class XlrHelper implements Serializable {

    def steps
    def pConfig

    XlrHelper(steps, pConfig) 
    {
        this.steps      = steps
        this.pConfig    = pConfig
    }

    def triggerRelease()
    {
        // Trigger XL Release Jenkins Plugin to kickoff a Release
        xlrCreateRelease(
            releaseTitle:       'A Release for $BUILD_TAG',
            serverCredentials:  "${pConfig.xlrUser}",
            startRelease:       true,
            template:           "${pConfig.xlrTemplate}",
            variables:          [
                                    [propertyName:  'ISPW_Dev_level',   propertyValue: "${pConfig.ispwTargetLevel}"], // Level in ISPW that the Code resides currently
                                    [propertyName:  'ISPW_RELEASE_ID',  propertyValue: "${pConfig.ispwRelease}"],     // ISPW Release value from the ISPW Webhook
                                    [propertyName:  'CES_Token',        propertyValue: "${pConfig.cesTokenId}"]
                                ]
        )

    }
}
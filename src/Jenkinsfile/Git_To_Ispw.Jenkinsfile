@Library('Shared_Lib@master') _
node {

    stage ('Checkout') {

        dir('./') {
            deleteDir()
        }

        checkout scm
    }

    stash name: 'workspace', includes: '**', useDefaultExcludes: false

    parallel(

        mfCode: {
            node {
                Git_MainframeCode_Pipeline()
            }
        },
        javaCode: {
            node {
                Git_JavaCode_Pipeline()
            }
        },
        failFast: true

    )
}
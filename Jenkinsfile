#!/usr/bin/env groovy

/*
node {
    deleteDir()
    sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git pipelines"
    load './pipelines/s4sdk-pipeline.groovy'
} */

//final def pipelineSdkVersion = 'mta'

pipeline {
    agent any
    options {
        timeout(time: 120, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        skipDefaultCheckout()
    }
    stages {
        stage('Init') {
            steps {
                library "s4sdk-pipeline-library"
                stageInitS4sdkPipeline script: this
                abortOldBuilds script: this
            }
        }

        stage('Build') {
            parallel {
		// needs definiton in pom.xml that no unit tests are being run
                stage("Backend") { steps { stageBuildBackend script: this } }
            }
        }



/*
        stage('Local Tests') {
            parallel {

               stage("Static Code Checks") { steps { stageStaticCodeChecks script: this } }
               stage("Backend Unit Tests") { steps { stageUnitTests script: this } }
               stage("Backend Integration Tests") { steps { stageIntegrationTests script: this } }
              //Frontend Unit Tests as script in frontend module!
                stage("Frontend Unit Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.FRONT_END_TESTS } }
                   steps { stageFrontendUnitTests script: this }
                }
               // NSP.log is not being found
                stage("Node Security Platform Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.NODE_SECURITY_SCAN } }
                    steps { stageNodeSecurityPlatform script: this }
                }

			}
        }


        stage('Remote Tests') {
            when { expression { commonPipelineEnvironment.configuration.skipping.REMOTE_TESTS } }
            parallel {
                stage("End to End Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.E2E_TESTS } }
                    steps { stageEndToEndTestsMTA script: this }
                }
                stage("Performance Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.PERFORMANCE_TESTS } }
                    steps { stagePerformanceTests script: this }
                }
            }
        }
*/

        // Config to specifiy scan modules
         //Problem: integration test work (find artifact from main module) to generate audit logs that can be analayzed here
     stage('Quality Checks') {
            steps { stageS4SdkQualityChecks script: this }
        }

  /*      stage('Third-party Checks') {
            when { expression { commonPipelineEnvironment.configuration.skipping.THIRD_PARTY_CHECKS } }
            parallel {
                // Untested ->  need to Setup Checkmarx Server
                 stage("Checkmarx Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.CHECKMARX_SCAN } }
                    steps { stageCheckmarxScan script: this }
                }

                // Untested
                 stage("WhiteSource Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.WHITESOURCE_SCAN } }
                    steps { stageWhitesourceScan script: this }
                }

                // Need Account and PPMS ID -  ask Real Spend . Executes Script underneath -> neeeds pom & npm in root? -> maybe also cd into dir!
                 stage("SourceClear Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.SOURCE_CLEAR_SCAN } }
                    steps { stageSourceClearScan script: this }
                    }

            }

        }




        stage('Artifact Deployment') {
            //when { expression { commonPipelineEnvironment.configuration.skipping.ARTIFACT_DEPLOYMENT } }
            steps { stageArtifactDeployment script: this }
        }


        stage('Production Deployment') {
            //when { expression { commonPipelineEnvironment.configuration.skipping.PRODUCTION_DEPLOYMENT } }
            steps { stageProductionDeployment script: this }
        }
*/

    }
    post {
        always {
            script {
                if (commonPipelineEnvironment.configuration.skipping.SEND_NOTIFICATION) {
                    postActionSendNotification script: this
                }
            }
        }
        failure { deleteDir() }
    }
}

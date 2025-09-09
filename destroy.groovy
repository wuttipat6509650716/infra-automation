#!/usr/bin/env groovy
@Library('my-shared-lib-intern-terraform') _

properties([
    buildDiscarder(logRotator(numToKeepStr: '10')),
    parameters([
        choice(name: "DataCenterName", choices: ['southeastasia']),
        string(name: 'TargetTerraformModule', defaultValue: '', description: 'e.g. create-azure-vm-servicename', trim: true),       
        choice(name: "TargetEnv", choices: ["dev"])
    ])
])

initial([
    GitBranch: "main",
    GitProjectMetadataRepo: "git@github.com:wuttipat6509650716/project-test-infra-automation.git",
    //GitUser: "wuttipat6509650716",
    TargetTerraformModule: params.TargetTerraformModule,
    DataCenterName: params.DataCenterName,
    TargetEnv: params.TargetEnv,
    Destroy: "Yes"
]) { context ->
    stagePrepareAndCheckout(context)
    stageDestroying(context)
    stagePushTerraformOutput(context)
}
#!/usr/bin/env groovy
@Library('my-shared-lib-intern-terraform') _

properties([
    buildDiscarder(logRotator(numToKeepStr: '10')),
    parameters([
        choice(name: "CloudProvider", choices: ['azure']),
        string(name: 'TerraformName', defaultValue: '', description: 'e.g. create-azure-vm-servicename', trim: true),
        choice(name: "DataCenterName", choices: ['southeastasia']),
        choice(name: "TargetEnv", choices: ["dev"]),
        booleanParam(name: 'SKIP_APPROVAL', description: 'skip approval when run infra provisioning', defaultValue: false),
    ])
])

initial([
    GitBranch: "main",
    GitProjectMetadataRepo: "git@github.com:wuttipat6509650716/project-test-infra-automation.git",
    //GitUser: "wuttipat6509650716",
    TerraformName: params.TerraformName,
    CloudProvider: params.CloudProvider,
    DataCenterName: params.DataCenterName,
    TargetEnv: params.TargetEnv,
    SKIP_APPROVAL: params.SKIP_APPROVAL,
    Provision: "Yes"
]) { context ->
    stagePrepareAndCheckout(context)
    stageProvisioning(context)
    stagePushTerraformOutput(context)
}
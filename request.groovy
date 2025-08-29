#!/usr/bin/env groovy
@Library('my-shared-lib-intern-terraform') _

properties([
    buildDiscarder(logRotator(numToKeepStr: '10')),
    parameters([
        choice(name: "CloudProvider", choices: ['azure']),
        string(name: 'ServiceName', defaultValue: '', description: 'e.g. servicename', trim: true),
        choice(name: "DataCenterName", choices: ['southeastasia']),
        choice(name: "TerraformModule", choices: ['create-azure-vm', 'create-azure-aks'])
    ])
])

initial([
    GitBranch: "main",
    GitProjectMetadataRepo: "https://github.com/wuttipat6509650716/project-test-infra-automation.git",
    //GitUser: "wuttipat6509650716",
    TemplateRepo: "https://github.com/wuttipat6509650716/infra-automation.git"
    TerraformModule: params.TerraformModule,
    ServiceName: params.ServiceName
]) { context ->
    stageMoveTerraformModule(context)
}
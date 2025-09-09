// vars/stagePushTerraformOutput.groovy

def call(Map args) {
    stage("PushTerraformOutput") {
        container("terraform") {
            withCredentials([
                azureServicePrincipal(
                    credentialsId: 'azure-service-principal',
                    subscriptionIdVariable: 'ARM_SUBSCRIPTION_ID',
                    clientIdVariable: 'ARM_CLIENT_ID',
                    clientSecretVariable: 'ARM_CLIENT_SECRET',
                    tenantIdVariable: 'ARM_TENANT_ID'
                )
            ]) {
                sh """
                    cd project/terraform-module/${args.TerraformName}
                    make output > ../../terraform-output/${args.TerraformName}/output.json
                    cat ../../terraform-output/${args.TerraformName}/output.json
                """
            }
        }

        container("git") {
            withCredentials([sshUserPrivateKey(credentialsId: 'github-ssh-jenkins',
                                                keyFileVariable: 'SSH_KEY',
                                                usernameVariable: 'SSH_USER')]) {
                dir('project') {
                    sh """
                        set -e
                        export GIT_SSH_COMMAND='ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no'
                        git config user.email "jenkins@local"
                        git config user.name "Jenkins"
                        git add -A
                        git commit -m "Add output.json" || echo "No changes to commit"
                        git push origin ${args.GitBranch}
                    """
                }
            }
        }
    }
}

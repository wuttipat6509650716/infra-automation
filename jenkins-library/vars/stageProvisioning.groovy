// vars/stageProvisioning.groovy
def call(Map args) {
  stage('Provisioning') {
    container('terraform') {
      sh 'apk add --no-cache make bash'

      withCredentials([
        azureServicePrincipal(
          credentialsId:            'azure-service-principal',
          subscriptionIdVariable:   'ARM_SUBSCRIPTION_ID',
          clientIdVariable:         'ARM_CLIENT_ID',
          clientSecretVariable:     'ARM_CLIENT_SECRET',
          tenantIdVariable:         'ARM_TENANT_ID'
        )
      ]) {
        sh """
          set -e
          cd project/terraform-module/${args.TerraformName}
          make init
          make plan CONFIG_FILE=../../terraform-configuration/${args.TerraformName}/config.json
        """

        if (!args.SKIP_APPROVAL) {
          timeout(time: 15, unit: 'MINUTES') {
            def userChoice = input(
              id: 'TerraformApproval',
              message: 'Approve Terraform Apply?',
              parameters: [
                choice(
                  name:        'ACTION',
                  choices:     ['Apply', 'Abort'],
                  description: 'Select action'
                )
              ]
            )
            if (userChoice == 'Abort') {
              error('User aborted the Terraform Apply.')
            }
          }
        }

        sh """
          set -e
          cd project/terraform-module/${args.TerraformName}
          make apply AUTO_APPROVE=true
        """
      }
    }
  }
}

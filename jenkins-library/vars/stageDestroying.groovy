// vars/stageDestroying.groovy
def call(Map args) {
  stage('Destroying') {
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
          cd project/terraform-module/${args.TargetTerraformModule}
          make init
          make plan-destroy CONFIG_FILE=../../terraform-configuration/${args.TargetTerraformModule}/config.json
        """

        if (!args.SKIP_APPROVAL) {
          timeout(time: 15, unit: 'MINUTES') {
            def userChoice = input(
              id: 'TerraformDestroy',
              message: 'Approve Terraform Destroy?',
              parameters: [
                choice(
                  name:        'ACTION',
                  choices:     ['Destroy'],
                  description: 'Select action'
                )
              ]
            )
            if (userChoice == 'Abort') {
              error('User aborted the Terraform Destroy.')
            }
          }
        }

        sh """
          set -e
          cd project/terraform-module/${args.TargetTerraformModule}
          make destroy AUTO_APPROVE=true
        """
      }
    }
  }
}

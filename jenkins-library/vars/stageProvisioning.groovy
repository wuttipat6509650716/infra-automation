// vars/stageProvisioning.groovy

def call(Map args) {
  stage("Provisioning") {
    container('terraform') {
      sh "apk add --no-cache make bash"

      withCredentials([azureServicePrincipal(
        credentialsId: 'azure-service-principal',
        subscriptionIdVariable: 'ARM_SUBSCRIPTION_ID',
        clientIdVariable:       'ARM_CLIENT_ID',
        clientSecretVariable:   'ARM_CLIENT_SECRET',
        tenantIdVariable:       'ARM_TENANT_ID'
        )]) {
          sh '''
            cd project/terraform-module
            make plan
          '''
          if(SKIP_APPROVAL == true){
            timeout(time: 15, unit: 'MINUTES') {
                input message: "Approve Terraform Apply?", ok: "Apply Now"
            }
          }

          sh '''
            cd project/terraform-module
            make apply AUTO_APPROVE=true
          '''
        }
      }
    }
  }
}


// 
// withAzureCredentials(bindings: [azureServicePrincipal('azure-service-principal')]) {
//   container('terraform') {
//     sh '''
//       set -e
//       export ARM_CLIENT_ID="$AZURE_CLIENT_ID"
//       export ARM_CLIENT_SECRET="$AZURE_CLIENT_SECRET"
//       export ARM_TENANT_ID="$AZURE_TENANT_ID"
//       export ARM_SUBSCRIPTION_ID="$AZURE_SUBSCRIPTION_ID"

//       terraform -version
//       terraform init
//       terraform apply -auto-approve
//     '''
//   }
// }

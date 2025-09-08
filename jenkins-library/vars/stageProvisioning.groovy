// vars/stageProvisioning.groovy

def call(Map args) {
  stage("Provisioning") {
    container('terraform') {
      sh "apk add --no-cache make bash"

      withCredentials([usernamePassword(credentialsId: 'azure-service-principal', usernameVariable: 'ARM_CLIENT_ID', passwordVariable: 'ARM_CLIENT_SECRET')]) {
        withEnv([
          "ARM_CLIENT_ID=$ARM_CLIENT_ID",
          "ARM_CLIENT_SECRET=$ARM_CLIENT_SECRET",
          "ARM_TENANT_ID=$AZURE_SP_TENANT_ID",
          "ARM_SUBSCRIPTION_ID=$AZURE_SP_SUBSCRIPTION_ID"
        ]) {
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

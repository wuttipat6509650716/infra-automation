// vars/stageMoveTerraformModule.groovy
def call(Map args) {
  stage("MoveTerraformModule") {
    container('git') {
      sh """
        set -e
        for folder in terraform-configuration terraform-output terraform-module; do
          [ -d project/\$folder ] || mkdir -p project/\$folder
        done
      """

      def subdir = "${args.TerraformModule}-${args.ServiceName}"
      sh """
        set -e
        for folder in terraform-configuration terraform-output terraform-module; do
          if [ -d project/\$folder/${subdir} ]; then
            echo "Servicename :${args.ServiceName} already exists. Exiting..."
            exit 1
          fi
          [ -d project/\$folder/${subdir} ] || mkdir -p project/\$folder/${subdir}
        done
      """

      sh """
        set -e

        cp template/terraform/${args.TerraformModule}/config.json project/terraform-configuration/${subdir}/
        echo "Copied config.json"

        echo "nothing" > project/terraform-output/${subdir}/output.json

        for f in backend.tf main.tf Makefile provider.tf variables.tf output.tf; do
          cp template/terraform/${args.TerraformModule}/\$f project/terraform-module/${subdir}/
          echo "Copied \$f"
        done

        ls -alR project
      """
      
      //edit backend.tf
      sh """
        set -e
        sed -i 's/servicename.tfstate/\$subdir.tfstate/' project/terraform-module/${subdir}/backend.tf
      """

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
              git commit -m "Move Terraform module: ${subdir}" || echo "No changes to commit"
              git push origin ${args.GitBranch}
              """
          }
      }
    }
  }
}

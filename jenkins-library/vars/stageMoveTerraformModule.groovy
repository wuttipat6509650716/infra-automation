// vars/stageMoveTerraformModule.groovy
def call(Map args) {
  stage("MoveTerraformModule") {
    container('git') {
      sh "rm -rf template project && mkdir -p template project"

      sh """
        set -e
        mkdir -p ~/.ssh
        chmod 700 ~/.ssh
        ssh-keyscan -t rsa,ecdsa,ed25519 github.com >> ~/.ssh/known_hosts
        chmod 644 ~/.ssh/known_hosts
      """

      withCredentials([sshUserPrivateKey(credentialsId: 'github-ssh-jenkins',
                                   keyFileVariable: 'SSH_KEY',
                                   usernameVariable: 'SSH_USER')]) {
            sh """
                set -e
                export GIT_SSH_COMMAND='ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no'
                cd template
                git clone --branch main ${args.TemplateRepo} .
                ls -al
            """
            sh """
                set -e
                export GIT_SSH_COMMAND='ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no'
                cd project
                git clone --branch ${args.GitBranch} ${args.GitProjectMetadataRepo} .
                ls -al
            """
        }


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
          [ -d project/\$folder/${subdir} ] || mkdir -p project/\$folder/${subdir}
        done
      """

      sh """
        set -e

        if [ -f template/${args.TerraformModule}/config.json ]; then
          cp template/${args.TerraformModule}/config.json project/terraform-configuration/${subdir}/
          echo "Copied config.json"
        fi

        if [ -f template/${args.TerraformModule}/output.tf ]; then
          cp template/${args.TerraformModule}/output.tf project/terraform-output/${subdir}/
          echo "Copied output.tf"
        fi

        for f in backend.tf main.tf Makefile provider.tf variables.tf; do
          if [ -f template/${args.TerraformModule}/\$f ]; then
            cp template/${args.TerraformModule}/\$f project/terraform-module/${subdir}/
            echo "Copied \$f"
          fi
        done

        ls -alR project
      """

      dir('project') {
        sh """
          set -e
          git remote set-url origin ${args.GitProjectMetadataRepo}
          git remote -v
        """
      }

      withCredentials([sshUserPrivateKey(credentialsId: 'github-ssh-jenkins',
                                   keyFileVariable: 'SSH_KEY',
                                   usernameVariable: 'SSH_USER')]) {
            dir('project') {
                sh """
                set -e
                export GIT_SSH_COMMAND='ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no'
                git config user.email "jenkins@local"
                git config user.name "Jenkins CI"
                git add -A
                git commit -m "Move Terraform module: ${subdir}" || echo "No changes to commit"
                git push origin ${args.GitBranch}
                """
            }
        }

    }
  }
}

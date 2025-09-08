// vars/stagePrepareAndCheckout.groovy
def call(Map args) {
    stage("PrepareAndCheckout") {
        container("git") {
            if (args.Request == "Yes") {
                sh """
                    rm -rf template project
                    mkdir -p template project
                """

                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'github-ssh-jenkins',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
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
            } else if (args.Provision == "Yes"){
                sh """
                    rm -rf project
                    mkdir -p project
                """

                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'github-ssh-jenkins',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    sh """
                        set -e
                        export GIT_SSH_COMMAND='ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no'
                        cd project
                        git clone --branch ${args.GitBranch} ${args.GitProjectMetadataRepo} .
                        ls -al
                    """
                }
            }
        }
    }
}

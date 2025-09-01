// vars/stageMoveTerraformModule.groovy
def call(Map args) {
    stage("MoveTerraformModule") {
        container('git') {
            sh "rm -rf template project && mkdir -p template project"

            try {
                dir('template') {
                    git url: args.TemplateRepo, branch: 'main'
                    echo "template content:"
                    sh "ls -al"
                }
            } catch (err) {
                error "Git clone (template) failed: ${err.message}"
            }

            try {
                dir('project') {
                    git url: args.GitProjectMetadataRepo, branch: args.GitBranch
                    echo "project content:"
                    sh "ls -al"
                }
            } catch (err) {
                error "Git clone (project) failed: ${err.message}"
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

            withCredentials([string(credentialsId: 'initialpipeline-gitcreaterepo', variable: 'GITHUB_TOKEN')]) {
                def newOrigin = args.GitProjectMetadataRepo.replaceFirst('https://', "https://${env.GITHUB_TOKEN}@")

                sh """
                    set -e
                    test -d project/.git || { echo "ERROR: project/.git not found"; exit 1; }

                    git -C project config user.name  "Jenkins"
                    git -C project config user.email "jenkins@example.com"

                    git -C project checkout -B ${args.GitBranch}

                    git -C project remote set-url origin "${newOrigin}"

                    git -C project add -A
                    git -C project diff --cached --quiet || git -C project commit -m "chore: update terraform module ${args.TerraformModule}-${args.ServiceName}"
                    git -C project push -u origin ${args.GitBranch}
                """
            }

        }
    }
}

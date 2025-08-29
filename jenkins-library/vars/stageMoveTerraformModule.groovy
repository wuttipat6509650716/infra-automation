// vars/stageMoveTerraformModule.groovy

def call(Map args) {
    stage("MoveTerraformModule") {
        sh "mkdir -p template"
        sh "mkdir -p project"

        try {
            dir('template') {
                container('git') {
                    git url: args.TemplateRepo, branch: 'main'
                }
                echo "dir template"
                sh "ls -al"
            }
        } catch (err) {
            error "Git clone failed: ${err.message}"
        }

        try {
            dir('project') {
                container('git') {
                    git url: args.GitProjectMetadataRepo, branch: args.GitBranch
                }
                echo "dir project"
                sh "ls -al"
            }
        } catch (err) {
            error "Git clone failed: ${err.message}"
        }

        dir('project') {
            sh '''
                for folder in terraform-configuration terraform-output terraform-module
                do
                  if [ ! -d "$folder" ]; then
                    echo "Folder $folder not found, creating..."
                    mkdir -p "$folder"
                  else
                    echo "Folder $folder already exists"
                  fi
                done
            '''
        }
        def subdir = "${args.TerraformModule}-${args.ServiceName}"
        dir('project') {
            sh """
                for folder in terraform-configuration terraform-output terraform-module
                do
                  if [ ! -d "\$folder/${subdir}" ]; then
                    echo "Subfolder \$folder/${subdir} not found, creating"
                    mkdir -p "\$folder/${subdir}"
                  else
                    echo "Subfolder \$folder/${subdir} already exists"
                  fi
                done
            """
        }
        sh """
            if [ -f template/${args.TerraformModule}/config.json ]; then
                cp template/${args.TerraformModule}/config.json project/terraform-configuration/\${subdir}
                echo "Copied config.json"
            fi

            if [ -f template/${args.TerraformModule}/output.tf ]; then
                cp template/${args.TerraformModule}/output.tf project/terraform-output/\${subdir}
                echo "Copied output.tf"
            fi

            for f in backend.tf main.tf Makefile provider.tf variables.tf
            do
                if [ -f template/${args.TerraformModule}/\$f ]; then
                    cp template/${args.TerraformModule}/\$f project/terraform-module/\${subdir}
                    echo "Copied \$f"
                fi
            done
            ls -alR project
        """

        container('git') {
            dir('project') {
                withCredentials([string(credentialsId: 'initialpipeline-gitcreaterepo', variable: 'GITHUB_TOKEN')]) {
                    sh '''
                        git status
                        git config user.email "jenkins"
                        git config user.name "Jenkins User"
                        git add .
                        if ! git diff --cached --quiet; then
                            git commit -m "Update terraform files for ${subdir}"
                            git push https://x-access-token:${GITHUB_TOKEN}@github.com/wuttipat6509650716/project-test-infra-automation.git HEAD:${args.GitBranch}
                        else
                            echo "No changes to commit"
                        fi
                    '''
                }
            }
        }
    }
}

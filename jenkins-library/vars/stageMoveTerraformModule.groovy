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
                ls -al
            '''
        }
        def subdir = "${args.TerraformModule}-${args.Servicename}"
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
            if [ -f template/config.json ]; then
                cp template/config.json project/terraform-configuration/\${subdir}/config/
                echo "Copied config.json"
            fi

            if [ -f template/output.tf ]; then
                cp template/output.tf project/terraform-output/\${subdir}/output/
                echo "Copied output.tf"
            fi

            for f in backend.tf main.tf Makefile provider.tf variable.tf
            do
                if [ -f template/\$f ]; then
                    cp template/\$f project/terraform-module/\${subdir}/module/
                    echo "Copied \$f"
                fi
            done
        """

        dir('project') {
            withCredentials([string(credentialsId: 'initialpipeline-gitcreaterepo', variable: 'GITHUB_TOKEN')]) {
            container('git') {
                sh """
                    git config user.email "jenkins"
                    git config user.name "Jenkins User"

                    git add .
                    if ! git diff --cached --quiet; then
                        git commit -m "Update terraform files for ${args.subdir}"
                        git push
                    else
                        echo "No changes to commit"
                    fi
                """
                }
            }
        }
    }
}

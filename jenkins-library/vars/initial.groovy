// vars/initial.groovy

def call(Map args = [:], Closure body = null) {

    // Create pod for slave

    if (args.Request == "Yes") {
        def alpineYaml = libraryResource('slaves/request.yaml')
        podTemplate(yaml: alpineYaml) {
            node(POD_LABEL) {
                if (body != null) {
                    body.call(args)
                }
            }
        }
    } else if (args.Provision == "Yes"){
        def provisionYaml = libraryResource('slaves/provision.yaml')
        podTemplate(yaml: provisionYaml) {
            node(POD_LABEL) {
                if (body != null) {
                    body.call(args)
                }
            }
        }
    }
}
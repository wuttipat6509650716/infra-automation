// vars/initial.groovy

def call(Map args = [:], Closure body = null) {

    // Create pod for slave
    def alpineYaml = libraryResource('slaves/slave.yaml')
    podTemplate(yaml: alpineYaml) {
        node(POD_LABEL) {
            if (body != null) {
                body.call(args)
            }
        }
    }
}
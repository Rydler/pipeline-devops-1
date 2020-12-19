def call(){
    //Estructura de Pipeline Declarativo
    pipeline {
        agent any

        parameters { choice(name: 'TECNOLOGIA', choices: ['maven', 'gradle'], description: '') }

        stages {
            stage('Pipeline') {
                //Estructura de Stages Maven y Gradle
                steps {
                    script {

                        env.TAREA = ''

                        /*
                        wrap([$class: 'BuildUser']) {
                            def user_first_name = env.BUILD_USER_FIRST_NAME
                            def user = env.BUILD_USER
                        }
                        */
                        
                         //Invocar Archivo dependiendo el parametro de Entrada
                        switch(params.TECNOLOGIA) {
                            case 'maven':
                                maven.call()
                                result = "maven"
                            break
                            case 'gradle':
                                //def externalMethod = load("gradle.groovy")
                                //externalMethod.call()
                                gradle.call()
                                result = "gradle"
                            break

                        }
                        echo "${result}"
                
                        /*
                        //Invocar Archivo dependiendo el parametro de Entrada
                        switch(params.TECNOLOGIA) {
                            case 'maven':
                                def externalMethod = load("maven.groovy")
                                externalMethod.call()
                                result = "maven"
                            break
                            case 'gradle':
                                //def externalMethod = load("gradle.groovy")
                                //externalMethod.call()
                               
                                result = "gradle"
                            break

                        }
                        echo "${result}"
                        */
                    }
                }
            }//END Pipeline Stage
        }
        post {

            failure {
                slackSend channel: 'U01DD0LGZLJ', color: 'danger', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ] Ejecución fallida en stage ${env.TAREA} . ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
            success {
                slackSend channel: 'U01DD0LGZLJ', color: 'good', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ] Ejecucion Exitosa. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
        }//fin post
    }//fin pipeline {}
}
return this;
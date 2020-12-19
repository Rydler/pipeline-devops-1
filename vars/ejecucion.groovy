def call(){
    //Estructura de Pipeline Declarativo
    pipeline {
        agent any

        parameters { 
            choice(name: 'TECNOLOGIA', choices: ['maven', 'gradle'], description: 'Elección de herramienta de empaquetamiento')

            string(name: 'STAGE', defaultValue: '', description: 'Elección de cual etapa Pipeline')
        }


        stages {
            stage('ValidacionParametros'){
                steps{
                    script{
                            try {
                                println('Hola Mundo')
                                println(params.TECNOLOGIA + params.STAGE)
                                echo "Hello ${params.TECNOLOGIA}"
                                echo "Hello ${params.STAGE}"
                            } catch(Exception e) {
                                println('ERRRRRROOOOOR')
                                error ('Ha ocurrido el siguiente error: ' + e)
                            }
                        }
                    }//fin steps validacionParametros
                }// fin stage validacionParametros
            }
            /*
            stage('Pipeline') {
                //Estructura de Stages Maven y Gradle
                steps {
                    script {

                        env.TAREA = ''
                        
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
                    }
                }
            }//END Pipeline Stage
            */
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
def call(){
    //Estructura de Pipeline Declarativo
    pipeline {
        agent any

        parameters { choice(name: 'TECNOLOGIA', choices: ['maven', 'gradle'], description: 'Elección de herramienta de empaquetamiento') }
        parameters { choice(name: 'STAGE', defaultValue: '', description: 'Elección de cual etapa Pipeline') }

        stages {
            stage('ValidacionParametros'){
                script{
                       try {
                           println('Hola Mundo')
                           /*
                           def funciones   = new Funciones()
                           stage('Todos'){
                               println 'Inicio'
                               println 'String 1: ' + param1
                               println 'String 2: ' + param2
                               gradle.call()
                           }
                           stage('1'){
                               println 'Union de 2 Strings: ' + funciones.unirDosStrings(param1, param2)          
                           }
                           stage('2'){
                               println 'Nombre obtenido desde Json: ' + funciones.mostrarNombre()
                           }
                           */
                       } catch(Exception e) {
                            println('Hola Mundo Error')
                           error ('Ha ocurrido el siguiente error: ' + e)
                       }
                   }

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
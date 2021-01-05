
def call(){
    
    //Estructura de Pipeline Declarativo
    pipeline {
        agent any

        parameters { 
            choice(name: 'TECNOLOGIA', choices: ['GRADLE', 'MAVEN'], description: 'Elección Herramienta de automatización de construcción de código de software')

            string(name: 'STAGE_PIPELINE', defaultValue: '', description: 'Elección de cual etapa elegir a procesar dentro del Pipeline')
        }
        
        //lo que debe hacer
        //1. Parametro vacio = todos los pasos
        //2. Parametro 1 o N = Puede ser 1 o Varios pasos separados por ";"
        //3. Validar que el STAGE ingresado ësta disponible dentro del pipeline y la herramienta a utilizar gradle o maven
        //4. Dar aviso si se ingreso un paso no existe

         stages {  
            stage('Pipeline'){
                //when { branch "feature-*" }
                when { branch "develop" }
                steps {
                    script {
                        println 'Herramienta seleccionada : ' + params.TECNOLOGIA 

                        println(env.GIT_BRANCH)
                        println(env.BRANCH_NAME)
                    
                        /*
                        stage('Etapa de CI'){
                            when { anyOf { branch 'feature-*'; branch 'develop' } }
                            steps{
                                sh 'CI'
                            }
                        }
                        stage('Etapa de CD'){
                            when {
                                {
                                    branch 'release-*'
                                }
                            }
                            steps{
                                 sh 'CD'
                            }
                        }
                        */
                        
                        /*
                        //Paso la etapa de validar que son existentes para ejecutarse
                        switch(params.TECNOLOGIA) {
                            case 'GRADLE':
                               gradle "${params.STAGE_PIPELINE.toUpperCase()}"
                            break
                            case 'MAVEN':
                               maven "${params.STAGE_PIPELINE.toUpperCase()}"
                            break

                        }
                        */
                        
                    }
                }
                when { 
                    not  { 
                        anyOf { branch 'feature-*'; branch 'develop'; branch 'release-v*' } 
                    } 
                }
                steps {
                    println 'Skipped full build.'
                }
            }
        }
        post {

            failure {
                println('Failure')
                //slackSend channel: 'U01DD0LGZLJ', color: 'danger', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecución fallida en stage ${env.Tarea}\n ${env.MensajeErrorSlack}. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
            success {
                 println('Sucess')
                //slackSend channel: 'U01DD0LGZLJ', color: 'good', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecucion Exitosa. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
        }//fin post
    }//fin pipeline {}
}
return this;

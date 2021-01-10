
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
            stage('Branch CI/CD No a Procesar'){
                when { 
                    not  { 
                        anyOf { branch 'feature-*'; branch 'develop'; branch 'release-v*' } // Este es cuando este funcionando todo
                        //anyOf { branch 'develop'; branch 'release-v*' } // Modo pruebas como ejecuto feature con el comentario anterior , entra acá
                    } 
                }
                steps {
                    println 'Se procesan ramas solo con formato de inicio feature- , develop, realease-v.'
                }
            }
            stage('Branch CI'){
                when { anyOf { branch 'feature-*'; branch 'develop' } }
                //when { branch "develop" } // Para generar el Skipped 
                steps {
                    script {
                        println 'Herramienta seleccionada : ' + params.TECNOLOGIA          
                        figlet params.TECNOLOGIA

                        
                        switch(params.TECNOLOGIA) {
                            case 'GRADLE':
                               ci_gradle "${params.STAGE_PIPELINE.toUpperCase()}"
                            break
                            case 'MAVEN':
                               //ci_maven "${params.STAGE_PIPELINE.toUpperCase()}"
                               sh 'No Soportado Aun.'
                            break
                        } 
                        
                        
                    }
                }
            }
            stage('Branch CD'){
                when { branch "release-v*" }
                steps {
                    script {
                        println 'Herramienta seleccionada : ' + params.TECNOLOGIA   
                        figlet params.TECNOLOGIA       
                        
                        //Paso la etapa de validar que son existentes para ejecutarse
                        
                        switch(params.TECNOLOGIA) {
                            case 'GRADLE':
                               cd_gradle "${params.STAGE_PIPELINE.toUpperCase()}"
                            break
                            case 'MAVEN':
                               //cd_maven "${params.STAGE_PIPELINE.toUpperCase()}"
                               sh 'No Soportado Aun.'
                            break
                        }
                        
                        
                    }
                }
            }
        }
        post {

            failure {
                println('Failure')
                slackSend channel: 'U01DD0LGZLJ', color: 'danger', message: " [ Grupo 5 ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecución fallida en stage ${env.Tarea}\n ${env.MensajeErrorSlack}. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
                //slackSend channel: 'U01DD0LGZLJ', color: 'danger', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecución fallida en stage ${env.Tarea}\n ${env.MensajeErrorSlack}. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
            success {
                 println('Sucess')
                 slackSend channel: 'U01DD0LGZLJ', color: 'good', message: " [ Grupo 5 ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecucion Exitosa. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
                //slackSend channel: 'U01DD0LGZLJ', color: 'good', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecucion Exitosa. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
        }//fin post
    }//fin pipeline {}
}
return this;

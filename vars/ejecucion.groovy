def call(){
    //Estructura de Pipeline Declarativo
    pipeline {
        agent any

        parameters { 
            choice(name: 'TECNOLOGIA', choices: ['gradle', 'maven'], description: 'Elección Herramienta de automatización de construcción de código de software')

            string(name: 'STAGE', defaultValue: '', description: 'Elección de cual etapa elegir a procesar dentro del Pipeline')
        }

        //lo que debe hacer
        //1. Parametro vacio = todos los pasos
        //2. Parametro 1 o N = Puede ser 1 o Varios pasos separados por ";"
        //3. Validar que el STAGE ingresado ësta disponible dentro del pipeline y la herramienta a utilizar gradle o maven
        //4. Dar aviso si se ingreso un paso no existe


        stages {
            stage('ValidacionParametros'){
                steps{
                    script{
                            try {
                                String tec = params.TECNOLOGIA.toUpperCase()
                                String stage = params.STAGE.toUpperCase()
                                String[] etapas;
                                //Defino Arreglo de Pasos Existentes por Tecnologia
                                def gradle_pasos = ['BUILD', 'TEST', 'SONAR', 'INICIAR','TEST_REST','NEXUS']; 
                                def maven_pasos = ['BUILD', 'TEST','JAR_CODE', 'SONAR', 'INICIAR','TEST_REST'];
                                echo "TEC ingreso: ${params.TECNOLOGIA}" 
                                echo "TEC despues: ${tec}" 
                                //Reviso si los pasos ingresados corresponden a los existentes, si no envio error
                                if (stage.le)
                                resultado = stage.length()>0 ? true : false
                                //Comprobe que se ingresaron etapas
                                if(resultado){
                                    etapas = stage.split(';');

                                    switch(tec) {
                                        case 'gradle':
                                            for( String _et : etapas )  
                                                gradle_pasos.contains(_et) ? println('etapas : ' + _et) : error ('La etapa : ' + _et + ' no es valida, favor ingrese una existente, dentro de los valores son : BUILD\nTEST\nSONAR\nINICIAR\nTEST?REST\nNEXUS ')
                                            }
                                        break
                                        case 'maven':
                                          
                                        break

                                    }
      
                                }
                              
                                //println('Hola Mundo') // y
                                //println(params.TECNOLOGIA + params.STAGE) // y
                                //echo "Hello ${params.TECNOLOGIA}" // y
                                //echo "Hello ${params.STAGE}" // y
                            } catch(Exception e) {
                                error ('Ha ocurrido un error inesperado en ValidacionPametros: ' + e)
                            }
                        }
                    }//fin steps validacionParametros
            }// fin stage validacionParametros
            


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
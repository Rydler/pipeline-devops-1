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
                                env.TAREA = ''
                                String tec = params.TECNOLOGIA.toUpperCase()
                                String stage = params.STAGE.toUpperCase()
                                String[] etapas;
                                //Defino Arreglo de Pasos Existentes por Tecnologia
                                def gradle_pasos = ['BUILD', 'TEST', 'SONAR', 'INICIAR','TEST_REST','NEXUS']; 
                                def maven_pasos = ['BUILD', 'TEST','JAR_CODE', 'SONAR', 'INICIAR','TEST_REST'];

                                //Variables
                                echo "TEC : ${tec}" 
                                echo "Stage : ${stage}" 
                               
                                //Reviso si los pasos ingresados corresponden a los existentes, si no envio error
                                resultado = stage.length()>0 ? true : false                   
                                echo "Se ingresaron etapas ?: ${resultado}" 

                                //Compruebo que se ingresaron etapas
                                if(resultado){
                                    etapas = stage.split(';');
                                    for( String _et : etapas )  {
                                        println('etapas : ' + _et)
                                        existe_etapa = gradle_pasos.contains(_et); 
                                        if(existe_etapa == false){
                                            error ('La etapa : ' + _et + ' no es valida, favor ingrese una existente, dentro de los valores son : BUILD\nTEST\nSONAR\nINICIAR\nTEST?REST\nNEXUS ')
                                        }
                                        //!(gradle_pasos.contains(_et)) {
                                            //Imprimo mensaje si no hay una etapa valida
                                            //println('etapas NO EXISTENTE : ' + _et)
                                            //error ('La etapa : ' + _et + ' no es valida, favor ingrese una existente, dentro de los valores son : BUILD\nTEST\nSONAR\nINICIAR\nTEST?REST\nNEXUS ')
                                        //}
                                        
                                    }
                                   
                                }
                              
                            } catch(Exception e) {
                                error ('Ha ocurrido un error inesperado en ValidacionParametros: ' + e)
                            }
                        }
                    }//fin steps validacionParametros
            }// fin stage validacionParametros
            
            /*
             //Paso la etapa de validar que son existentes para ejecutarse
                                    / *
                                    switch(tec) {
                                        case 'gradle':
                                            for( String _et : etapas )  {
                                               println('etapas : ' + _et)
                                            }
                                        break
                                        case 'maven':
                                           echo 'FALTA CODIGO'
                                        break

                                    }
                                    * /
            else {
                                    //Ejecutar todos los pasos
                                    if(tec == 'gradle'){
                                        //gradle.todos_los_pasos()
                                        echo 'GRADLE TODOS LOS PASOS !!!'
                                    }else{
                                        //maven.todos_los_pasos()
                                        echo 'MAVEN TODOS LOS PASOS !!!'
                                    }
                                }
        
                                //println('Hola Mundo') // y
                                //println(params.TECNOLOGIA + params.STAGE) // y
                                //echo "Hello ${params.TECNOLOGIA}" // y
                                //echo "Hello ${params.STAGE}" // y
            */


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
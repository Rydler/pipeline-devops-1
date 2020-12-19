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
            stage('ValidacionParametros'){
                steps{
                    script{
                            try {
                                env.Tarea = 'ValidacionParametros'
                                env.MensajeErrorSlack = ''
                                env.Etapa = params.STAGE_PIPELINE.toUpperCase()
                                String[] etapas

                                //Defino Arreglo de Pasos Existentes por Tecnologia
                                def gradle_pasos = ['BUILD', 'TEST', 'SONAR', 'INICIAR','TEST_REST','NEXUS']; 
                                def maven_pasos = ['BUILD', 'TEST','JAR_CODE', 'SONAR', 'INICIAR','TEST_REST'];

                                //Variables
                                echo "Tecnologia : ${params.TECNOLOGIA}" 
                                echo "Etapa : ${env.Etapa}" 
                               
                                //Reviso si los pasos ingresados corresponden a los existentes, si no envio error
                                resultado = env.Etapa.length()>0 ? true : false                   
                                echo "Se ingresaron etapas ?: ${resultado}" 

                                //Compruebo que se ingresaron etapas y valido que sean todas validas para el siguiente Stage
                                if(resultado){
                                    etapas = env.Etapa.split(';');

                                    //Paso la etapa de validar que son existentes para ejecutarse
                                    switch(params.TECNOLOGIA) {
                                        case 'GRADLE':
                                            for( String _et : etapas )  {
                                                println('etapas : ' + _et)
                                                existe_etapa = gradle_pasos.contains(_et); 
                                                if(existe_etapa == false){
                                                    env.MensajeErrorSlack = 'La etapa : ' + _et + ' no es valida, favor ingrese una dentro de estos parametos\n BUILD\nTEST\nSONAR\nINICIAR\nTEST_REST\nNEXUS'
                                                    error (env.MensajeErrorSlack)
                                                }
                                            }
                                        break
                                        case 'MAVEN':
                                            for( String _et : etapas )  {
                                                println('etapas : ' + _et)
                                                existe_etapa = maven_pasos.contains(_et); 
                                                if(existe_etapa == false){
                                                    env.MensajeErrorSlack = 'La etapa : ' + _et + ' no es valida, favor ingrese una dentro de estos parametos\n BUILD\nTEST\nJAR_CODE\nSONAR\nINICIAR\nTEST_REST'
                                                    error (env.MensajeErrorSlack)
                                                }
                                            }
                                        break

                                    }
                                   
                                   
                                }
                              
                            } catch(Exception e) {
                                error ('Ha ocurrido un error en ValidacionParametros: ' + e)
                            }
                        }
                    }//fin steps validacionParametros
            }// fin stage validacionParametros
            stage('Pipeline') {
                //Estructura de Stages Maven y Gradle
                steps {
                    script {

                        env.Tarea = 'Pipeline'
                        env.MensajeErrorSlack = ''
                        String[] etapas

                        echo "Tecnologia: ${params.TECNOLOGIA}" 

                        //Invocar Archivo dependiendo el parametro de Entrada
                        switch(params.TECNOLOGIA) {
                            case 'GRADLE':
                                //Valido que si es vacio todos los procesos , de lo contrario solo los escogidos
                                if(env.Tarea == ''){
                                //gradle.todos_los_pasos()
                                echo 'GRADLE TODOS LOS PASOS !!!'
                                }else{ // Ejecutar Cada Paso Previamente Validado como Existente
                                    etapas = env.Etapa.split(';');
                                    for( String _et : etapas )  {
                                        println('Etapa : ' + _et)
                                        println("gradle.${_et}()")
                                        //sh "gradle.${_et}()"
                                        sh 'echo hola'
                                        //sh 'gradle.build()'
                                        //gradle.build() //works
                                        gradle."${_et}"()
                                    }
                                } // fin if env.Tarea
                              
                            break
                            case 'MAVEN':
                                for( String _et : etapas )  {
                                    println('etapas a Procesar : ' + _et)
                                  
                                }
                            break
                        }//fin switch
                    } //fin script Pipeline
                }// fin steps
            }//END Pipeline Stage
            
           
        }
        post {

            failure {
                slackSend channel: 'U01DD0LGZLJ', color: 'danger', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecución fallida en stage ${env.Tarea}\n ${env.MensajeErrorSlack}. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
            success {
                slackSend channel: 'U01DD0LGZLJ', color: 'good', message: " [ Alexander Sanhueza ][ ${env.JOB_NAME} ][ ${params.TECNOLOGIA} ]\nEjecucion Exitosa. ", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'slack-diplomado-asc'
            }
        }//fin post
    }//fin pipeline {}
}
return this;
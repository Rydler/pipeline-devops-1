package pipeline.utilidades

    def sayHi() {
        echo "Hi from Funciones!"
    }

    def sayHello(str) {
        echo "Hello ${str}"
    }

    def validarEtapasValidas(String etapasEscogidas, ArrayList pipelineEtapas){
        def etapas = []
        def etapasError = []

        if(etapasEscogidas?.trim().toUpperCase()){

            etapasEscogidas.split(';').each{
                if(it in pipelineEtapas){
                    etapas.add(it)
                }else {
                    etapasError.add(it)
                }
            }

            //Existen Etapas con Error
            if(etapasError.size() > 0){
                env.MensajeErrorSlack = " Estos stages ingresados no existen : ${etapasError}.\nStages disponibles para ejecutar:\n${pipelineEtapas}"
                error env.MensajeErrorSlack
            }
            println "Validación de stages correcta.\nSe ejecutarán los siguientes stages en orden :\n${etapas}"
        }else{
            etapas = pipelineEtapas
            println "Parámetro de stages vacio.\nSe ejecutarán todas los stages en el siguiente orden :\n${etapas}"
        }

        return etapas


    }

    def obtenerValoresArchivoPOM(String nombreArchivoPom){
        def pomFile = readFile(nombreArchivoPom)
        def pom = new XmlParser().parseText(pomFile)
        //def gavMap = [:]
        //gavMap['groupId'] =  pom['groupId'].text().trim()
        //gavMap['artifactId'] =  pom['artifactId'].text().trim()
        //gavMap['version'] =  pom['version'].text().trim()

        env.ProyectoGrupoID     = pom['groupId'].text().trim()
        env.ProyectoArtefactoID =  pom['artifactId'].text().trim()
        env.ProyectoVersion     =  pom['version'].text().trim()
    }

    def validarNombreRepositorioGit(){
     
        env.NombreRepositorioGit = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
        //Validar que el repositorio es un MS
        if(!(env.NombreRepositorioGit.contains("ms") || nombreRepositorioGit.contains("MS"))){
            env.MensajeErrorSlack = " El nombre del repositorio Git (${etapasError}) , no es un Micro Servicio."
            error env.MensajeErrorSlack
        }
        println "Validación de Nombre Repositori Git es MS correcta.\n"

    }

    def validarArchivosGradleoMaven(){

        def archivo = (params.TECNOLOGIA == 'GRADLE') ? 'build.gradle' : 'pom.xml'

        if(!(fileExists(archivo))){
            env.MensajeErrorSlack = " No se encontro el Archivo Base para la Tecnlogia : ${paramsTECNOLOGIA} , favor revisar."
            error env.MensajeErrorSlack
        }

        println "Validación de Archivos Gradle / Maven correcta.\n"

    }

    def validarFormatoTAG(){
        try
        {   
            if("5.2.0.123".findAll(/^[0-9]+(\-[0-9]+)?(\-[0-9]+)?/)[0]) {
                 println "success 1"
            }else{
                println "error 1"
            }

            if("5-2-0".findAll(/^[0-9]+(\-[0-9]+)?(\-[0-9]+)?/)[0]) {
                 println "success 2"
            }else{
                println "error 2"
            }
        }
        catch(Exception e){ echo e
        }
    }

return this;
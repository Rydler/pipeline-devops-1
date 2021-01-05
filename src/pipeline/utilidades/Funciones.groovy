package pipeline.utilidades

    public static final String GroupIDProject
    public static final String ArtifactIDProject
    public static final String VersionProject

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
        def gavMap = [:]
        gavMap['groupId'] =  pom['groupId'].text().trim()
        gavMap['artifactId'] =  pom['artifactId'].text().trim()
        gavMap['version'] =  pom['version'].text().trim()

        GroupIDProject = gavMap['groupId']
        ArtifactIDProject = gavMap['artifactId']
        VersionProject = gavMap['version']

        env.ProyectoGrupoID = pom['groupId'].text().trim()
    }

return this;
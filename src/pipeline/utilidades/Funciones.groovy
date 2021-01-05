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

    //def obtenerValoresArchivoPOM(){
        
    //}

return this;
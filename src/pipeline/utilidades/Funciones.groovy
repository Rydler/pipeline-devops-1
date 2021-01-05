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
                //env.MensajeErrorSlack = ''
                error " Estos stages ingresados no existen : ${etapasError} . Stages disponibles para ejecutar: ${pipelineEtapas}"
            }
            println "Validación de stages correcta. Se ejecutarán los siguientes stages en orden : ${etapas}"
        }else{
            etapas = pipelineEtapas
            println "Parámetro de stages vacio. Se ejecutarán todas los stages en el siguiente orden : ${etapas}"
        }

        return etapas


    }

return this;
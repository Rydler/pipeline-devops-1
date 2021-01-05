import pipeline.utilidades.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    def ci_maven_pasos = ['HOLA'];

    env.Tarea = 'Maven CI Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, ci_maven_pasos)

    etapas.each{
        stage(it){
            try{
                //Llamado dinamico
                "${it.toLowerCase()}"()
            }catch(Exception e) {
                env.MensajeErrorSlack = "Stage ${it.toLowerCase()} tiene problemas : ${e}"
                error env.MensajeErrorSlack
            }
        }
    }
   
}


def hola(){
    script{
        env.Tarea = 'Hola'
        figlet env.Tarea
    }
    sh 'echo Hola Maven CI'
}



return this;
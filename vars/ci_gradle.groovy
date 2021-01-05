import pipeline.utilidades.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia en CI
    def ci_gradle_pasos = ['HOLA'];

    env.Tarea = 'Gradle CI Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, ci_gradle_pasos)

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
    sh "buildAndTest,sonar,runJar,rest,nexusCI"
}

return this;
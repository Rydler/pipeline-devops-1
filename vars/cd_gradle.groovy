import pipeline.utilidades.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    def cd_gradle_pasos = ['DOWNLOADNEXUS','INICIARDOWNLOADJAR','TEST_REST','NEXUSUPLOAD'];

    env.Tarea = 'Gradle CD Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, cd_gradle_pasos)

    //Setear Variables ENV de Proyecto a Ejecutar
    funciones.obtenerValoresArchivoPOM('pom.xml')

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


def downloadnexus(){
    script{
        env.Tarea = 'DownloadNexus'
        figlet env.Tarea
        println(${env.ProyectoGrupoID}.replace('.','/'))
    }
    //sh  "curl -X GET -u admin:admin http://localhost:8081/repository/ci-nexus/${env.ProyectoGrupoID.replace()}/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
    sh 'echo hola'
}


return this;
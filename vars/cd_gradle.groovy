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
    }
    sh  "curl -X GET -u admin:admin http://localhost:8081/repository/ci-nexus/" + env.ProyectoGrupoID.replace('.','/') + "/${env.ProyectoArtefactoID}/${env.ProyectoVersion}/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar -O"
    //EN el Log Running on Jenkins  in XXX , para saber la ruta
    //sh 'echo DownloadNexus'
}

def iniciardownloadjar(){
    script{
        env.Tarea = 'IniciarDownloadJar'
        figlet env.Tarea
    }
    sh "nohup java -jar ${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar & >/dev/null"
    sh "sleep 20"
}

def test_rest(){
    script{
        env.Tarea = 'test_rest'
        figlet env.Tarea
    }
    sleep 20
    sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    //sh 'echo rest'
}


def nexusupload(){
    script{
        env.Tarea = 'NexusUpload'
        figlet env.Tarea
    }
   nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'cd-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "${WORKSPACE}/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar"]], mavenCoordinate: [artifactId: "${env.ProyectoArtefactoID}", groupId: "${env.ProyectoGrupoID}", packaging: 'jar', version: "${env.ProyectoVersion}"]]]
   //sh 'echo nexusUpload'
}


return this;
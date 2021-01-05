import pipeline.utilidades.*

interface POMXML {                                         
    void artefacto(String name)                                 
}

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    def ci_gradle_pasos = ['BUILDANDTEST','SONAR'];

    env.Tarea = 'Gradle CI Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, ci_gradle_pasos)

    //Setear Variables Globales de Proyecto a Ejecutar
    funciones.obtenerValoresArchivoPOM('pom.xml')
    println(funciones.GroupIDProject)

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


def buildandtest(){
    script{
        env.Tarea = 'BuildAndTest'
        figlet env.Tarea
    }
    //sh 'gradle clean build' 
    sh 'echo buildandtest'
}

def sonar(){
    script{
        env.Tarea = 'Sonar'
        figlet env.Tarea
        println(env.ProyectoGrupoID + '' + ProyectoArtefactoID + '' + ProyectoVersion)
    }
        
    //SonnarScanner
    //def scannerHome = tool 'sonar-scanner';
    //Sonnar Server
    //withSonarQubeEnv('sonar'){
        //sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${repository}-${BRANCH_NAME}-${BUILD_NUMBER} -Dsonar.java.binaries=build"
    //}
   
    sh 'echo Hola Sonar'
}
def iniciar(){
    script{
        env.Tarea = 'run'
        figlet env.Tarea
    }
    sh 'nohup gradle bootRun &'
    sleep 20
    //sh 'echo run'
}

def test_rest(){
    script{
        env.Tarea = 'rest'
        figlet env.Tarea
    }
    sleep 20
    sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    //sh 'echo rest'
}

def nexus(){
    script{
        env.Tarea = 'nexus'
        figlet env.Tarea
    }
    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/kuroi/.jenkins/workspace/_multibranch_feature-dir-inicial/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    //sh 'echo nexus'
}

return this;
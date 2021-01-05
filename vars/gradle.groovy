import pipeline.utilidades.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    def gradle_pasos = ['BUILD', 'TEST', 'SONAR', 'INICIAR','TEST_REST','NEXUS'];

    env.Tarea = 'Gradle Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, gradle_pasos)

    etapas.each{
        stage(it){
            try{
                //Llamado dinamico
                "${it.toLowerCase()}"()
            }catch(Exception e) {
                env.MensajeErrorSlack = "Stage ${it} tiene problemas : ${e}"
                error env.MensajeErrorSlack
            }
        }
    }
   
}

def build(){
    script{
        env.Tarea = 'build!!!!'
        figlet env.Tarea
    }
    figlet 'build method'
    sh 'gradle clean build' 
    //sh 'echo build'
}

def test(){
    env.TAREA = 'test'
    sh 'gradle test' 
    //sh 'echo test'
}

def sonar(){
    env.TAREA = 'sonar'
        
    //SonnarScanner
    def scannerHome = tool 'sonar-scanner';
    //Sonnar Server
    withSonarQubeEnv('sonar'){
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
    }
        
    //sh 'echo sonar'
}
def iniciar(){
    env.TAREA = 'run'
    sh 'nohup gradle bootRun &'
    sleep 20
    //sh 'echo run'
}

def test_rest(){
    env.TAREA = 'rest'
    sleep 20
    sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    //sh 'echo rest'
}

def nexus(){
    env.TAREA = 'nexus'
    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/kuroi/.jenkins/workspace/_multibranch_feature-dir-inicial/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    //sh 'echo nexus'
}

return this;
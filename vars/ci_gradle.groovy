import pipeline.utilidades.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    def ci_gradle_pasos = ['BUILDANDTEST','SONAR','INICIAR','TEST_REST','NEXUS'];

    env.Tarea = 'Gradle CI Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, ci_gradle_pasos)

    //Setear Variables ENV de Proyecto a Ejecutar
    funciones.obtenerValoresArchivoPOM('pom.xml')

    funciones.obtenerNombreRepositorioGit()

    println "Nombre Repositorio:" + env.NombreRepositorioGit

    
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
    sh 'gradle clean build' 
    //sh 'env'
}

def sonar(){
    script{
        env.Tarea = 'Sonar'
        figlet env.Tarea
    }
        
    //SonnarScanner
    def scannerHome = tool 'sonar-scanner';
    //Sonnar Server
    withSonarQubeEnv('sonar'){
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${env.ProyectoArtefactoID}-${GIT_BRANCH}-${env.ProyectoVersion} -Dsonar.java.binaries=build"
    }
   
    //sh 'env'
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
        //println("${WORKSPACE}/build/libs/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar")
    }
    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'ci-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "${WORKSPACE}/build/libs/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar"]], mavenCoordinate: [artifactId: "${env.ProyectoArtefactoID}", groupId: "${env.ProyectoGrupoID}", packaging: 'jar', version: "${env.ProyectoVersion}"]]]
    //sh 'echo nexus'
}

def gitCreateRelease(){
    //Paso la etapa de validar que son existentes para ejecutarse
    def git = new GitMetodos()
    //Dinamico la version
    if(git.checkIfBranchExists('release-v1-0-0')){
        if(git.isBranchUpdated(env.GIT_BRANCH,'release-v1-0-0')){
            println 'La rama esta creada y actualizada contra ' + env.GIT_BRANCH
        }else{
            git.deleteBranch('release-v1-0-0')
            git.createBranch('release-v1-0-0', env.GIT_BRANCH)
        }
    }else {
        git.createBranch('release-v1-0-0', env.GIT_BRANCH)
    }
}

return this;
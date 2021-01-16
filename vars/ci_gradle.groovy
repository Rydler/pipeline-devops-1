import pipeline.utilidades.*
import pipeline.git.*

def call(String etapasEscogidas){

    //Defino Arreglo de Pasos Existentes por Tecnologia
    //def ci_gradle_pasos = (GIT_BRANCH.contains("develop") ? ['BUILDANDTEST','SONAR','INICIAR','TEST_REST','NEXUS','GITCREATERELEASE'] : ['BUILDANDTEST','SONAR','INICIAR','TEST_REST','NEXUS'])
    def ci_gradle_pasos = (GIT_BRANCH.contains("develop") ? ['buildandtest','sonar','nexusUpload','gitCreateRelease'] : ['buildandtest','sonar','nexusUpload'])


    env.Tarea = 'Gradle CI Pipeline'
    figlet env.Tarea

    def funciones   = new Funciones()
    def etapas      = funciones.validarEtapasValidas(etapasEscogidas, ci_gradle_pasos)

    //Setear Variables ENV de Proyecto a Ejecutar
    funciones.obtenerValoresArchivoPOM('pom.xml')

    funciones.validarNombreRepositorioGit()

    funciones.validarArchivosGradleoMaven()

    funciones.validarFormatoTAG()

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
    sh './gradlew clean build'
    //sh 'env'
}

def sonar(){
    script{
        env.Tarea = 'Sonar'
        figlet env.Tarea
    }
        
    //SonnarScanner
    def scannerHome = tool 'sonar';
    //Sonnar Server
    withSonarQubeEnv('sonar'){
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${env.NombreRepositorioGit}-${GIT_BRANCH}-${BUILD_ID} -Dsonar.java.binaries=build"
        //sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${env.ProyectoArtefactoID}-${GIT_BRANCH}-${env.ProyectoVersion} -Dsonar.java.binaries=build"
    }
}

/*
def iniciar(){
    script{
        env.Tarea = 'run'
        figlet env.Tarea
    }
    sh 'nohup bash gradlew  bootRun &'
    sleep 20
    //sh 'echo run'
}

def test_rest(){
    script{
        env.Tarea = 'rest'
        figlet env.Tarea
    }
    sleep 20
    sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
    //sh 'echo rest'
}
*/

def nexus(){
    script{
        env.Tarea = 'nexus'
        figlet env.Tarea
        //println("${WORKSPACE}/build/libs/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar")
    }
    nexusPublisher nexusInstanceId: 'Nexus_server_local', nexusRepositoryId: 'ci-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "${WORKSPACE}/build/libs/${env.ProyectoArtefactoID}-${env.ProyectoVersion}.jar"]], mavenCoordinate: [artifactId: "${env.ProyectoArtefactoID}", groupId: "${env.ProyectoGrupoID}", packaging: 'jar', version: "${env.ProyectoVersion}"]]]
    //sh 'echo nexus'
}

def gitcreaterelease(){
    //Paso la etapa de validar que son existentes para ejecutarse
    def git = new GitMetodos()
    //Viene con el Formato v1-0-0
    ramaTmp = "release-"+env.VersionTag
    if(git.checkIfBranchExists(ramaTmp)){
        println "SI Existe Rama "
        if(git.isBranchUpdated(env.GIT_BRANCH,ramaTmp)){
            println 'La rama esta creada y actualizada contra ' + env.GIT_BRANCH
        }else{
            println 'La rama NO esta creada y actualizada contra ' + env.GIT_BRANCH
            git.deleteBranch(ramaTmp)
            git.createBranch(ramaTmp, env.GIT_BRANCH)
        }
    }else {
        println "No Existe Rama"
        git.createBranch(ramaTmp, env.GIT_BRANCH)
    }
}

return this;
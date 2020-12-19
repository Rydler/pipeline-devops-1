/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
    //Escribir directamente el código del stage, sin agregarle otra clausula de Jenkins.
    stage('build & test'){
         env.TAREA = 'build & test'
        sh 'gradle clean build' //gradlew es para que se ejecute con wrapper
    } //end build
    stage('sonar'){
         env.TAREA = 'sonar'
        //SonnarScanner
        def scannerHome = tool 'sonar-scanner';
        //Sonnar Server
        withSonarQubeEnv('sonar'){
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
        }
        
    }//end test
    stage('run'){
        env.TAREA = 'run'
        sh 'nohup gradle bootRun &'
        sleep 20
    }//end run
    stage('rest'){
        env.TAREA = 'rest'
        sleep 20
        sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    }//end run
    stage('nexus'){
        env.TAREA = 'nexus'
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/kuroi/.jenkins/workspace/_multibranch_feature-dir-inicial/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }//end nexus

}

return this;
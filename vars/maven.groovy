/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
  stage('Compilar') {
    env.TAREA = 'Compilar'
    sh './mvnw clean compile -e'
    //echo 'Compilar'
  }
  stage('Test') {
    env.TAREA = 'Test'
    sh './mvnw clean test -e'
    //echo 'Test'
  }
  stage('JarCode') {
    env.TAREA = 'JarCode'
    sh './mvnw clean package -e'
    //echo 'JarCode'
  }
  stage('SonarQube') {
    env.TAREA = 'SonarQube'
    withSonarQubeEnv('sonar') //Nombre del SonarQube Server de Configurar Sistema en Jenkins 
    { // You can override the credential to be used
        sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
    }
    //echo 'sonarQube'
  }
  stage('RunJar'){
    env.TAREA = 'RunJar'
    sh 'nohup bash mvnw spring-boot:run &'
    sleep 20
    //echo 'RunJar'
  }
  stage('Curl') {
    env.TAREA = 'Curl'
    sleep 20
    sh 'curl -X GET "http://localhost:8082/rest/mscovid/test?msg=testing" '
    //echo 'Curl'
  }
}

return this;
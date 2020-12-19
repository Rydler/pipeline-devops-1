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

def build(){
    stage('Compilar'){
        env.TAREA = 'compilar'
        sh './mvnw clean compile -e'
        //sh 'echo compilar'
    } //end 
}

def test(){
    stage('Test'){
        env.TAREA = 'test'
        sh './mvnw clean test -e'
        //sh 'echo test'
    } //end 
}

def jar_code(){
    stage('JarCode'){
        env.TAREA = 'jarcode'
        sh './mvnw clean package -e'
        //sh 'echo jarcode'
    } //end 
}

def sonar(){
    stage('SonarQube'){
        env.TAREA = 'sonarqube'
        
        withSonarQubeEnv('sonar') //Nombre del SonarQube Server de Configurar Sistema en Jenkins 
        { // You can override the credential to be used
            sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
        }
        //sh 'echo sonarqube'
    } //end 
}

def iniciar(){
    stage('RunJar'){
        env.TAREA = 'runjar'
        sh 'nohup bash mvnw spring-boot:run &'
        sleep 20
        //sh 'echo runjar'
    } //end 
}

def test_rest(){
    stage('Curl'){
        env.TAREA = 'curl'
        sleep 20
        sh 'curl -X GET "http://localhost:8082/rest/mscovid/test?msg=testing" '
        //sh 'echo curl'
    } //end 
}


return this;
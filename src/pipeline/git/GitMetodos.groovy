package pipeline.git

def checkIfBranchExists(String rama){

    def output = sh (script : "git pull ; git ls-remote --heads origin ${rama}", returnStdout: true)
    //println "output:" + output
    def respuesta = (output?.trim().contains("refs/heads/${rama}")) ? true : false
    //println "respuesta:" + respuesta
    return respuesta
}

def isBranchUpdated(String ramaOrigen, String ramaDestino){

    sh "git checkout ${ramaOrigen}; git pull"
    sh "git checkout ${ramaDestino}; git pull"
    //Comando local
    def output =  sh (script :"git log ${ramaOrigen}..${ramaDestino}" , returnStdout: true)
    //println "output:" + output
    //Si el output es vacio = sin cambios
    //si no es vacio = Con cambios
    def respuesta = (output?.trim()) ? false : true //output?.trim() => valida si existe un output 
    //println "respuesta:" + respuesta
    return respuesta
}

def deleteBranch(String rama){
    sh "git pull ; git push origin --delete ${rama}; git branch -D ${rama}"
}

def createBranch(String ramaDestino, String ramaOrigen){
  sh "echo Crear Rama"
  /*sh """
      git reset --hard HEAD
      git pull
      git checkout ${ramaOrigen}
      git checkout -b ${ramaDestino}
      git push origin ${ramaDestino}
    """
    */
  
  sh "echo $USER"
    def output =  sh (script :"git reset --hard HEAD" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git pull" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git checkout ${ramaOrigen}" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git checkout -b ${ramaDestino}" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git push origin ${ramaDestino}" , returnStdout: true)
    println "output:" + output
}

def deployToMain(){
    sh "git checkout ${GIT_BRANCH}"
    output =  sh (script :"git status" , returnStdout: true)
    println "output:" + output
    def output =  sh (script :"git pull" , returnStdout: true)
    println "output:" + output
    sh "git config --replace-all remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'"
    output =  sh (script :"git pull" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git checkout main" , returnStdout: true)
    cc
    output =  sh (script :"git pull" , returnStdout: true)
    output =  sh (script :"git merge ${GIT_BRANCH} --no-ff" , returnStdout: true)
    println "output:" + output
    output =  sh (script :"git pull" , returnStdout: true)
    output =  sh (script :"git push" , returnStdout: true)
    println "output:" + output
}

def deployToDevelop(){
    output =  sh (script :"git pull" , returnStdout: true)
    println "output:" + output
    sh "git checkout develop"
    sh "git merge ${GIT_BRANCH} --no-ff"
}

def tagMain(){
    def outputLocal = sh (script : "git tag -l ${env.VersionTag}", returnStdout: true)
    println "outputLocal:" + outputLocal
    def respuestaLocal = (outputLocal?.trim().contains("${env.VersionTag}")) ? true : false
    println "respuestaLocal:" + respuestaLocal
    if(respuestaLocal){
        //Borrar Tag Local
        sh "git tag -d ${env.VersionTag}"
    }

    def outputRemoto = sh (script : "git ls-remote --tags origin ${env.VersionTag}", returnStdout: true)
    println "outputRemoto:" + outputRemoto
    def respuestaRemoto = (outputRemoto?.trim().contains("${env.VersionTag}")) ? true : false
     println "respuestaRemoto:" + respuestaRemoto
    if(respuestaRemoto){
        // Remoto 
        sh "git push --delete origin ${env.VersionTag}"
    }
    
    //Generar Tag y Subirlo
    sh "git checkout main" 
    sh "git tag ${env.VersionTag}" 
    sh "git push origin --tags"
}


return this;
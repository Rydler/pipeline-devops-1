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

    //y luego el comando de abajo pero local como la siguiente linea
    def output =  sh (script :"git log ${ramaOrigen}..${ramaDestino}" , returnStdout: true)
    //sh "git config --add remote.origin.fetch +refs/heads/${ramaOrigen}:refs/remotes/origin/${ramaOrigen}"
    //def output =  sh (script :"git log origin/${ramaOrigen}..origin/${ramaDestino}" , returnStdout: true)
    def respuesta = (!output?.trim()) ? true : false

    return respuesta
}

def deleteBranch(String branch){
    sh "git pull ; git push origin --delete ${branch}"
}

def createBranch(String ramaDestino, String ramaOrigen){
  println "Crear Rama"
  sh '''
    git reset --hard HEAD
    git pull
    git checkout '''+${ramaOrigen}+'''
    git checkout -b '''+${ramaDestino}+'''
    git push origin '''+${ramaDestino}+'''
  '''
  /*
  sh "git reset --hard HEAD"
  sh "git pull"
  sh "git checkout ${ramaOrigen}"
  sh "git checkout -b ${ramaDestino}"
  sh "git push origin ${ramaDestino}"
   */
}

return this;
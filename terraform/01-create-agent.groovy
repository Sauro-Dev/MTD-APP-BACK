import jenkins.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Variables de entorno
def nodeName = "ci-agent"
def remoteFS = "/home/ubuntu/jenkins"
def numExecutors = 2
def labelString = "ci-agent"
def host = System.getenv("JENKINS_AGENT_IP") ?: ""
def credentialsId = "ci-agent-ssh"

// 1. Crear el agente SSH
if (host) {
    if (Jenkins.instance.getNode(nodeName) == null) {
        def launcher = new SSHLauncher(host, 22, credentialsId, "", "", "", "", "", 60, 3, 15)
        def node = new DumbSlave(
                nodeName,
                "Agente CI creado por init.groovy.d",
                remoteFS,
                numExecutors.toString(),
                Node.Mode.NORMAL,
                labelString,
                launcher,
                new RetentionStrategy.Always(),
                []
        )
        Jenkins.instance.addNode(node)
        println "Agente '${nodeName}' creado exitosamente"
    } else {
        println "Agente '${nodeName}' ya existe"
    }
} else {
    println "No se encontró la variable de entorno JENKINS_AGENT_IP"
}

// 2. Timer robusto para ejecutar el pipeline automáticamente
def executor = Executors.newSingleThreadScheduledExecutor()
executor.scheduleWithFixedDelay(new Runnable() {
    @Override
    void run() {
        def job = Jenkins.instance.getItem('mtd-pipeline')
        if (job != null) {
            println "Pipeline 'mtd-pipeline' encontrado, ejecutando..."
            job.scheduleBuild2(0)
            println "Pipeline 'mtd-pipeline' programado para ejecución"
            executor.shutdown() // Detener el timer
        } else {
            println "Esperando a que se cree el pipeline 'mtd-pipeline'..."
        }
    }
}, 10, 5, TimeUnit.SECONDS)
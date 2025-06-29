FROM jenkins/jenkins:lts

# Cambiar a usuario root para instalar paquetes del sistema
USER root

# Instalar Maven y otras dependencias necesarias
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Volver al usuario jenkins
USER jenkins

# Instalar plugins necesarios
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# Copiar el archivo de configuración JCasC
COPY --chown=jenkins:jenkins jenkins-casc.yaml /var/jenkins_home/casc.yaml
COPY --chown=jenkins:jenkins 01-create-agent.groovy /usr/share/jenkins/ref/init.groovy.d/

# Configurar variables de entorno para JCasC
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc.yaml
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
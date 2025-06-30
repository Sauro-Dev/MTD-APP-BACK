FROM jenkins/jenkins:lts

USER root

# Instalar Maven, Docker CLI y Docker Compose de forma más directa
RUN apt-get update && \
    apt-get install -y maven curl && \
    # Instalar Docker CLI directamente desde el binario estático
    curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-24.0.7.tgz | \
    tar -xzC /tmp && \
    mv /tmp/docker/docker /usr/local/bin/docker && \
    chmod +x /usr/local/bin/docker && \
    # Instalar Docker Compose v2
    curl -SL https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    # Limpiar
    rm -rf /var/lib/apt/lists/* /tmp/docker

# Crear grupo docker (será ajustado dinámicamente por el entrypoint)
RUN groupadd -g 999 docker || true
RUN usermod -aG docker jenkins

# Cambiar a usuario jenkins para copiar archivos
USER jenkins

# Copiar configuración de Jenkins
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

COPY --chown=jenkins:jenkins jenkins-casc.yaml /var/jenkins_home/casc.yaml
COPY --chown=jenkins:jenkins 01-create-agent.groovy /usr/share/jenkins/ref/init.groovy.d/

# Variables de entorno
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc.yaml
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Volver a root para copiar el entrypoint
USER root

# Copiar y configurar el entrypoint
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Usar el entrypoint dinámico
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
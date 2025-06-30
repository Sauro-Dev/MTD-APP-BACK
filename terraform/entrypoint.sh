#!/bin/bash

# Obtener el GID real del socket Docker del host
DOCKER_SOCK_GID=$(stat -c '%g' /var/run/docker.sock 2>/dev/null || echo "999")

# Modificar o crear el grupo docker con el GID correcto
if getent group docker >/dev/null 2>&1; then
    groupmod -g $DOCKER_SOCK_GID docker
else
    groupadd -g $DOCKER_SOCK_GID docker
fi

# Asegurar que jenkins esté en el grupo docker
usermod -aG docker jenkins

# Ejecutar Jenkins como usuario jenkins
exec su jenkins -c "/usr/local/bin/jenkins.sh"
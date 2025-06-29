#!/bin/bash
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
echo "--- Iniciando script de user_data ---"

# Actualizar paquetes
apt-get update -y

# Instalar dependencias
apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

# Instalar Java 17 (OpenJDK)
apt-get install -y openjdk-17-jdk

# Instalar Maven
apt-get install -y maven

# Instalar Docker
mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io

# Añadir el usuario ubuntu al grupo docker para evitar problemas de permisos
usermod -aG docker ubuntu

# Habilitar y iniciar Docker
systemctl enable docker
systemctl start docker

# Instalar Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Crear un volumen para Jenkins
docker volume create jenkins_home

# Definir una contraseña predeterminada para pruebas
JENKINS_ADMIN_PASSWORD=$${JENKINS_ADMIN_PASSWORD:-admin123}

# Ejecutar tu imagen personalizada de Jenkins desde Docker Hub
docker run -d --name jenkins -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -e JENKINS_ADMIN_PASSWORD=$JENKINS_ADMIN_PASSWORD \
  -e JENKINS_AGENT_PRIVATE_KEY="$JENKINS_AGENT_PRIVATE_KEY" \
  -e JENKINS_AGENT_IP="$JENKINS_AGENT_IP" \
  kmotinxd/preconfig-jenkins:iac

sudo usermod -aG docker ubuntu
echo "${JENKINS_ADMIN_PASSWORD}" | sudo tee /var/lib/jenkins_admin_password.txt
echo "--- Jenkins configurado automáticamente ---"
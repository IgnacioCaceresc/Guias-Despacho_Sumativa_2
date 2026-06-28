#!/bin/bash
# Script de inicialización (User Data) para Ubuntu Server 22.04 / 24.04 LTS
# Esto instalará Docker y Git automáticamente al iniciar la máquina por primera vez.

# 1. Actualizar el sistema
apt-get update -y
apt-get upgrade -y

# 2. Instalar dependencias esenciales
apt-get install -y ca-certificates curl gnupg git

# 3. Configurar el repositorio oficial de Docker
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

# 4. Instalar Docker y Docker Compose
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin docker-compose

# 5. Agregar el usuario 'ubuntu' al grupo de Docker (para no tener que usar sudo docker)
usermod -aG docker ubuntu

# 6. Habilitar Docker para que inicie al encender la máquina
systemctl enable docker
systemctl start docker

echo "¡Instalación de Docker y dependencias completada exitosamente!"

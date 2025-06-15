terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_instance" "existing_instance" {
  filter {
    name   = "tag:Name"
    values = ["mtd-backend"]
  }
}

resource "null_resource" "deploy_app" {
  connection {
    type = "ssh"
    user = "ec2-user"
    private_key = file("${path.module}/../mtd-backend.pem")
    host = data.aws_instance.existing_instance.public_ip
  }

  provisioner "remote-exec" {
    inline = [
      "sudo yum update -y",

      # Instalar Java 17
      "sudo amazon-linux-extras enable corretto17",  # Habilitar el repositorio de Amazon Corretto 17
      "sudo yum install -y java-17-amazon-corretto-devel",  # Instalar Java 17 desde Amazon Corretto

      # Configurar JAVA_HOME
      "echo 'export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64' >> ~/.bashrc",
      "echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc",
      "source ~/.bashrc",

      # Instalar Git
      "sudo yum install -y git",

      # Clonar el repositorio
      "rm -rf MTD-APP-BACK",
      "git clone https://github.com/Sauro-Dev/MTD-APP-BACK.git",

      # Compilar y ejecutar la aplicación
      "cd MTD-APP-BACK",
      "chmod +x ./mvnw",
      "./mvnw clean package -DskipTests",  # Omitir las pruebas
      "java -jar target/your-application.jar &"
    ]
  }
}
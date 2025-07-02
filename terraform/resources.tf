# 1. Crear una Red Virtual Privada (VPC)
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "${var.app_name}-vpc"
  }
}

# 2. Crear una Subred Pública
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  tags = {
    Name = "${var.app_name}-public-subnet"
  }
}

# 3. Crear una Puerta de Enlace a Internet (Internet Gateway)
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "${var.app_name}-igw"
  }
}

# 4. Crear una Tabla de Rutas para dirigir el tráfico a internet
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }
  tags = {
    Name = "${var.app_name}-public-rt"
  }
}

# 5. Asociar la Tabla de Rutas con nuestra Subred
resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# 6. Crear un Grupo de Seguridad (Firewall)
resource "aws_security_group" "allow_ssh" {
  name        = "${var.app_name}-allow-ssh"
  description = "Allow traffic SSH and Jenkins"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH desde cualquier lugar"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Jenkins Web UI"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Prometheus"
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "Grafana"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.app_name}-allow-ssh-sg"
  }
}

# 7. Generar un par de claves RSA de 4096 bits
resource "tls_private_key" "rsa_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# 8. Guardar la clave privada generada en un archivo local
resource "local_file" "private_key_pem" {
  content         = tls_private_key.rsa_key.private_key_pem
  filename        = "mtd-app-key.pem"
  file_permission = "0400" # Permisos de solo lectura para el propietario
}

# 9. Cargar nuestra clave pública generada a AWS
resource "aws_key_pair" "deployer" {
  key_name   = "${var.app_name}-key"
  public_key = tls_private_key.rsa_key.public_key_openssh
}

# 10. Data source para obtener la AMI más reciente de Ubuntu 22.04
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# 11. Crear la instancia EC2
resource "aws_instance" "backend_server" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.ec2_instance_type
  key_name               = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.allow_ssh.id]
  subnet_id              = aws_subnet.public.id

  user_data = templatefile("${path.module}/user_data.sh", {
    JENKINS_ADMIN_PASSWORD = random_password.jenkins_admin.result
  })

  tags = {
    Name = "mtd-app-server"
  }
}

resource "random_password" "jenkins_admin" {
  length  = 16
  special = true
}

# Recurso para generar el secreto del túnel de forma aleatoria y segura
resource "random_bytes" "tunnel_secret" {
  length = 32
}

# 12. Crear la base de datos D1
resource "cloudflare_d1_database" "database" {
  account_id = var.cloudflare_account_id
  name       = "${var.app_name}-db"
}

# 13. Crear el bucket de almacenamiento R2
resource "cloudflare_r2_bucket" "bucket" {
  account_id = var.cloudflare_account_id
  name       = "${var.app_name}-storage"
  location   = "ENAM"
}

# 14. Crear el Túnel de Cloudflare (versión original)
resource "cloudflare_tunnel" "app_tunnel" {
  account_id = var.cloudflare_account_id
  name       = "${var.app_name}-tunnel"
  secret     = random_bytes.tunnel_secret.base64
}

# Configuración del túnel de Cloudflare para apuntar a la instancia EC2 (versión original)
resource "cloudflare_tunnel_config" "app_tunnel_config" {
  account_id = var.cloudflare_account_id
  tunnel_id  = cloudflare_tunnel.app_tunnel.id

  config {
    ingress_rule {
      hostname = "api.${var.domain_name}"
      service  = "http://${aws_instance.backend_server.private_ip}:8080"
    }
    ingress_rule {
      service = "http_status:404"
    }
  }
}

# 15. Crear un registro DNS para apuntar al túnel
resource "cloudflare_record" "api" {
  zone_id = var.cloudflare_zone_id
  name    = "api"
  type    = "CNAME"
  value   = "${cloudflare_tunnel.app_tunnel.id}.cfargotunnel.com"
  proxied = true
}

# Recurso para la aplicación en Cloudflare Access (versión original)
resource "cloudflare_access_application" "app" {
  zone_id      = var.cloudflare_zone_id
  name         = "${var.app_name}-app"
  domain       = "api.${var.domain_name}"
  type         = "self_hosted"
}

resource "cloudflare_access_policy" "app_policy" {
  application_id = cloudflare_access_application.app.id
  zone_id        = var.cloudflare_zone_id
  name           = "${var.app_name}-policy"
  decision       = "allow"
  precedence     = 1

  include {
    everyone = true
  }
}
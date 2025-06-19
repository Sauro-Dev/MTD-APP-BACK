terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}

resource "aws_instance" "mtd_instance" {
  ami           = "ami-0c55b159cbfafe1f0" # Ubuntu 20.04 LTS
  instance_type = "c5.large" # Tipo de instancia optimizada para cómputo
  key_name      = "mtd-key-pair"
  security_groups = ["mtd-security-group"]

  tags = {
    Name = "mtd-instance"
  }
}

resource "aws_security_group" "mtd_security_group" {
  name        = "mtd-security-group"
  description = "Allow SSH and HTTP traffic"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_key_pair" "mtd_key_pair" {
  key_name   = "mtd-key-pair"
  public_key = file("~/.ssh/id_rsa.pub")
}

resource "cloudflare_r2_bucket" "mtd_files" {
  account_id = var.cloudflare_account_id
  name       = var.cloudflare_r2_bucket_name
  location   = "ENAM"
}

resource "cloudflare_d1_database" "mtd" {
  account_id = var.cloudflare_account_id
  name       = var.cloudflare_d1_database_name
}

resource "cloudflare_tunnel" "mtd_tunnel" {
  account_id = var.cloudflare_account_id
  name       = "mtd-app-tunnel"
  secret     = base64encode(random_password.tunnel_secret.result)
}

resource "random_password" "tunnel_secret" {
  length  = 32
  special = false
}

output "tunnel_id" {
  value = cloudflare_tunnel.mtd_tunnel.id
}

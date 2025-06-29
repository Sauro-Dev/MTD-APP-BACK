output "instance_public_ip" {
  description = "La dirección IP pública de la instancia EC2."
  value       = aws_instance.backend_server.public_ip
}

output "cloudflare_d1_database_name" {
  description = "El nombre de la base de datos D1 creada."
  value       = cloudflare_d1_database.database.name
}

output "cloudflare_r2_bucket_name" {
  description = "El nombre del bucket R2 creado."
  value       = cloudflare_r2_bucket.bucket.name
}

output "cloudflare_tunnel_secret" {
  description = "El secreto del túnel de Cloudflare. Necesario para configurar el servicio 'cloudflared'."
  value       = cloudflare_tunnel.app_tunnel.secret
  sensitive   = true
}

output "jenkins_admin_password" {
  description = "Password del usuario admin de Jenkins"
  value       = random_password.jenkins_admin.result
  sensitive   = true
}

output "jenkins_url" {
  description = "URL de acceso a Jenkins"
  value       = "http://${aws_instance.backend_server.public_ip}:8080"
}

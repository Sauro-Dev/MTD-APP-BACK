/* GENERAL VARIABLES */
variable "app_name" {
  description = "MTD"
  type        = string
  default     = "mtd-app"
}

variable "domain_name" {
  description = "make-the-difference.site"
  type        = string
}

/* AWS VARIABLES */
variable "aws_access_key" {
  description = "AWS Access Key"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS Secret Key"
  type        = string
  sensitive   = true
}

variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "us-east-2"
}

variable "ec2_instance_type" {
  description = "EC2 for mtd"
  type        = string
  default     = "t3.small"
}

/* CLOUDFLARE VARIABLES */
variable "cloudflare_api_token" {
  description = "The API token for Cloudflare"
  type        = string
  sensitive   = true
}

variable "cloudflare_account_id" {
  description = "The account ID for Cloudflare"
  type        = string
  sensitive   = true
}

variable "cloudflare_zone_id" {
  description = "Zone id for MTD"
  type        = string
  sensitive   = true
}

variable "cloudflare_r2_access_key" {
  description = "Cloudflare R2 Access Key"
  type        = string
  sensitive   = true
}

variable "cloudflare_r2_secret_key" {
  description = "Cloudflare R2 Secret Key"
  type        = string
  sensitive   = true
}

variable "cloudflare_r2_endpoint" {
  description = "Cloudflare R2 Endpoint"
  type        = string
}

variable "cloudflare_r2_bucket_name" {
  description = "Cloudflare R2 Bucket Name"
  type        = string
}
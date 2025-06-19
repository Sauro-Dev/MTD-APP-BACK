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

variable "cloudflare_api_token" {
  description = "The API token for Cloudflare"
  type        = string
}

variable "cloudflare_account_id" {
  description = "The account ID for Cloudflare"
  type        = string
}

variable "cloudflare_r2_bucket_name" {
  description = "The name of the R2 bucket"
  type        = string
  default     = "mtd-files"
}

variable "cloudflare_d1_database_name" {
  description = "The name of the D1 database"
  type        = string
  default     = "mtd"
}

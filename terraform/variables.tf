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
}

variable "cloudflare_d1_database_name" {
  description = "The name of the D1 database"
  type        = string
}

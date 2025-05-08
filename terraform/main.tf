terraform {
  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 4.0"
    }
  }
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}

resource "cloudflare_r2_bucket" "mtd-files" {
  account_id = var.cloudflare_account_id
  name       = var.cloudflare_r2_bucket_name
  location   = "ENAM"
}

resource "cloudflare_d1_database" "mtd" {
  account_id = var.cloudflare_account_id
  name       = var.cloudflare_d1_database_name
}


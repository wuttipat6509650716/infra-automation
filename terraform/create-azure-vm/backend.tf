//backend.tf
terraform {
  backend "azurerm" {
    resource_group_name  = "ชื่อ-resource-group"
    storage_account_name = "ชื่อ-storage-account"
    container_name       = "tfstate"
    key                  = "servicename.tfstate"
  }
}
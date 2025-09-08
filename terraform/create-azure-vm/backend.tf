//backend.tf
terraform {
  backend "azurerm" {
    resource_group_name  = "terraform-rg"
    storage_account_name = "terraformstate12345abcde"
    container_name       = "tfstate"
    key                  = "servicename.tfstate"
  }
}
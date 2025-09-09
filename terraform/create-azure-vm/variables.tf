//variables.tf

variable "location" {
  type        = string
}

variable "resource_group_name" {
  type        = string
}

variable "vm_name" {
  type        = string
}

variable "vm_size" {
  type        = string
}

variable "admin_username" {
  type        = string
}

variable "admin_password" {
  type        = string
}

variable "address_space" {
  type = list(string)
}

variable "subnet_prefix" {
  type = list(string)
}

variable "public_ip_allocation_method" {
  type    = string
}

variable "private_ip_allocation_method" {
  type    = string
}

variable "public_ip_sku" {
  type    = string
}

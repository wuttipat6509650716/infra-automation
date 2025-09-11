variable "resource_group_name" {
  type        = string
}

variable "location" {
  type        = string
}

variable "aks_name" {
  type        = string
}

variable "dns_prefix" {
  type        = string
}

variable "node_count" {
  type        = number
}

variable "node_vm_size" {
  type        = string
}

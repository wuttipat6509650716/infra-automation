# Makefile
.PHONY: init plan apply destroy fmt validate output

CONFIG_FILE ?= ../../terraform-configuration/config.json

init:
	terraform init

plan:
	terraform plan -var-file=$(CONFIG_FILE) -out=tfplan

apply:
	terraform apply tfplan

destroy:
	terraform destroy -var-file=$(CONFIG_FILE)

fmt:
	terraform fmt

validate:
	terraform validate

output:
	terraform output -json
# Makefile
.PHONY: init plan apply destroy fmt validate output

CONFIG_FILE ?= ./config.json

init:
	terraform init

plan:
	terraform plan -var-file=$(CONFIG_FILE) -out=tfplan

apply:
	terraform apply $(if $(AUTO_APPROVE),-auto-approve) tfplan

plan-destroy:
	terraform plan -destroy -var-file=$(CONFIG_FILE) -out=tfplan_destroy

destroy:
	terraform apply $(if $(AUTO_APPROVE),-auto-approve) tfplan_destroy

fmt:
	terraform fmt

validate:
	terraform validate

output:
	terraform output -json
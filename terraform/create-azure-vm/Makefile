.PHONY: init plan apply destroy fmt validate

CONFIG_FILE ?= config.json

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
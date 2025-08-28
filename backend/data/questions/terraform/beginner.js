module.exports = [
  {
    "id": 1,
    "text": "What is Terraform?",
    "textSize": 24,
    "explanation": "Terraform is an open-source Infrastructure as Code (IaC) tool created by HashiCorp. It allows you to define and provision infrastructure using a declarative configuration language called HCL (HashiCorp Configuration Language).\n\nWith Terraform, you can manage infrastructure across multiple cloud providers, including AWS, Azure, Google Cloud, and many others, using a consistent workflow."
  },
  {
    "id": 2,
    "text": "What is the Terraform workflow?",
    "textSize": 20,
    "explanation": "The core Terraform workflow consists of three main commands:\n\n1. terraform init: Initializes working directory and downloads required providers\n2. terraform plan: Creates an execution plan showing what changes will be made\n3. terraform apply: Executes the planned changes to reach desired state\n\nAdditional commands include 'terraform destroy' to remove infrastructure and 'terraform validate' to check configuration syntax."
  },
  {
    "id": 3,
    "text": "What is Terraform state?",
    "textSize": 19,
    "explanation": "Terraform state is a file that tracks the current state of your infrastructure. It maps your configuration to real-world resources and stores metadata about your infrastructure.\n\nKey points about state:\n• Stored in terraform.tfstate file by default\n• Contains sensitive information\n• Should be stored remotely in production (S3, Azure Storage, etc.)\n• Enables Terraform to determine what changes need to be made\n• Supports state locking to prevent concurrent modifications"
  }
];

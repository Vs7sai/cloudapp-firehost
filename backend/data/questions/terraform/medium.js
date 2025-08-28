module.exports = [
  {
    "id": 1,
    "text": "Explain Terraform modules and their benefits",
    "explanation": "Terraform modules are containers for multiple resources that are used together. They allow you to create reusable, composable, and shareable infrastructure components.\n\nBenefits:\n• Code reusability across projects\n• Standardization of infrastructure patterns\n• Easier maintenance and updates\n• Abstraction of complex configurations\n• Version control for infrastructure components\n\nModules can be local (in your repository) or remote (from Terraform Registry or Git repositories)."
  },
  {
    "id": 2,
    "text": "What are Terraform variables and outputs?",
    "explanation": "Variables and outputs are key components for making Terraform configurations flexible and reusable.\n\nVariables:\n• Input parameters for your configuration\n• Can have default values, types, and descriptions\n• Can be set via CLI, files, environment variables\n• Enable customization without changing code\n\nOutputs:\n• Return values from your configuration\n• Can be used by other configurations or modules\n• Useful for displaying important information\n• Enable module composition and data sharing"
  },
  {
    "id": 3,
    "text": "How do you manage Terraform state in a team environment?",
    "explanation": "For team environments, you should use remote state backend with the following considerations:\n\n1. Remote Backend: Store state in S3, Azure Storage, or Terraform Cloud\n2. State Locking: Use DynamoDB (AWS) or similar to prevent concurrent modifications\n3. Access Control: Implement proper IAM policies for state access\n4. Encryption: Enable encryption at rest and in transit\n5. Versioning: Enable state file versioning for rollback capability\n6. Workspace Isolation: Use Terraform workspaces for different environments\n7. CI/CD Integration: Automate Terraform runs in pipelines"
  }
];

module.exports = [
  {
    "id": 1,
    "text": "Design a multi-environment Terraform architecture",
    "explanation": "A robust multi-environment Terraform architecture should include:\n\n1. Directory Structure:\n   • environments/dev, staging, prod\n   • modules/ for reusable components\n   • shared/ for common configurations\n\n2. State Management:\n   • Separate backends per environment\n   • Remote state with locking\n   • State isolation between environments\n\n3. Variable Management:\n   • Environment-specific tfvars files\n   • Secrets management integration\n   • Consistent naming conventions\n\n4. CI/CD Pipeline:\n   • Automated plan/apply workflows\n   • Environment promotion strategies\n   • Approval gates for production"
  },
  {
    "id": 2,
    "text": "Explain Terraform provisioners and when to avoid them",
    "explanation": "Terraform provisioners are used to execute scripts or commands on local or remote machines after resource creation.\n\nTypes:\n• local-exec: Runs commands locally\n• remote-exec: Runs commands on remote resources\n• file: Copies files to remote resources\n\nWhen to avoid:\n• Provisioners should be a last resort\n• They break Terraform's declarative model\n• Can cause state inconsistencies\n• Make resources less portable\n\nAlternatives:\n• Use cloud-init or user data\n• Configuration management tools (Ansible, Chef)\n• Container images with pre-configured software\n• Infrastructure-specific solutions (AWS Systems Manager)"
  },
  {
    "id": 3,
    "text": "How do you implement Terraform security best practices?",
    "explanation": "Terraform security best practices include:\n\n1. State Security:\n   • Remote backend with encryption\n   • Restrict state file access\n   • Use state locking\n\n2. Secrets Management:\n   • Never hardcode secrets in .tf files\n   • Use external secret managers\n   • Leverage provider-specific secret resources\n\n3. Access Control:\n   • Implement least-privilege IAM policies\n   • Use service accounts, not personal credentials\n   • Regular credential rotation\n\n4. Code Security:\n   • Static analysis tools (tfsec, Checkov)\n   • Peer review process\n   • Version control with signed commits\n   • Scan for security misconfigurations"
  }
];

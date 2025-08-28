module.exports = [
  {
    "id": 1,
    "text": "Design an enterprise Ansible architecture",
    "explanation": "An enterprise Ansible architecture should include:\n\n1. Ansible Tower/AWX:\n   • Centralized management and scheduling\n   • RBAC and audit logging\n   • Web UI and REST API\n   • Credential management\n\n2. Infrastructure:\n   • Dedicated Ansible control nodes\n   • High availability setup\n   • Network isolation and security\n   • Monitoring and logging\n\n3. Organization:\n   • Standardized role and playbook structure\n   • Version control integration\n   • Testing and validation pipelines\n   • Documentation and knowledge sharing\n\n4. Security:\n   • Encrypted communication\n   • Proper credential management\n   • Regular security audits\n   • Compliance with organizational policies"
  },
  {
    "id": 2,
    "text": "Explain Ansible performance optimization techniques",
    "explanation": "Ansible performance can be optimized through various techniques:\n\n1. Parallelism:\n   • Increase forks value for more parallel connections\n   • Use strategy plugins (free, linear)\n   • Optimize batch size with serial directive\n\n2. Connection Optimization:\n   • Enable SSH pipelining\n   • Use ControlMaster for SSH connection reuse\n   • Disable gathering facts when not needed\n   • Use delegate_to for local operations\n\n3. Task Optimization:\n   • Use specific modules instead of shell/command\n   • Leverage async for long-running tasks\n   • Use block statements to group related tasks\n   • Implement proper error handling\n\n4. Caching:\n   • Enable fact caching\n   • Use smart gathering\n   • Cache API responses when possible"
  },
  {
    "id": 3,
    "text": "How do you implement Ansible testing and CI/CD integration?",
    "explanation": "Comprehensive Ansible testing and CI/CD should include:\n\n1. Testing Levels:\n   • Syntax validation: ansible-playbook --syntax-check\n   • Molecule: Full role testing with different scenarios\n   • Integration tests: End-to-end testing\n   • Security testing: ansible-lint, yamllint\n\n2. CI/CD Pipeline:\n   • Version control triggers\n   • Automated testing on pull requests\n   • Environment-specific deployments\n   • Rollback capabilities\n\n3. Tools:\n   • Molecule for role testing\n   • Testinfra for infrastructure testing\n   • Jenkins/GitLab CI for pipeline automation\n   • Ansible Tower/AWX for production deployments\n\n4. Best Practices:\n   • Test in isolated environments\n   • Use infrastructure as code for test environments\n   • Implement proper change management\n   • Monitor and validate deployments"
  }
];

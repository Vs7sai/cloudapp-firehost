module.exports = [
  {
    "id": 1,
    "text": "Explain Ansible roles and their structure",
    "explanation": "Ansible roles are a way to organize playbooks and reuse code. They provide a standardized directory structure for organizing tasks, variables, files, and templates.\n\nRole structure:\n• tasks/main.yml: Main task list\n• handlers/main.yml: Handler definitions\n• vars/main.yml: Role variables\n• defaults/main.yml: Default variables\n• files/: Static files to copy\n• templates/: Jinja2 templates\n• meta/main.yml: Role metadata and dependencies\n\nRoles promote reusability and maintainability of automation code."
  },
  {
    "id": 2,
    "text": "What are Ansible variables and how do you use them?",
    "explanation": "Ansible variables store values that can be used throughout playbooks and roles. They provide flexibility and reusability in automation.\n\nVariable types and precedence (highest to lowest):\n• Extra variables (ansible-playbook -e)\n• Task variables\n• Block variables\n• Role and include variables\n• Play variables\n• Host facts\n• Host variables\n• Group variables\n• Default variables\n\nVariables can be defined in multiple places and are resolved based on precedence rules."
  },
  {
    "id": 3,
    "text": "How do you handle sensitive data in Ansible?",
    "explanation": "Ansible provides several mechanisms for handling sensitive data securely:\n\n1. Ansible Vault:\n   • Encrypts sensitive files or variables\n   • Uses AES256 encryption\n   • Can encrypt entire files or individual variables\n   • Requires vault password for decryption\n\n2. Best Practices:\n   • Store vault files separately from playbooks\n   • Use different vault passwords for different environments\n   • Never commit vault passwords to version control\n   • Use --ask-vault-pass or vault password files\n   • Consider external secret management systems for production"
  },
  {
    "id": 4,
    "text": "How do you handle sensitive data in Ansible11111?",
    "explanation": "Ansible provides several mechanisms for handling sensitive data securely:\n\n1. Ansible Vault:\n   • Encrypts sensitive files or variables\n   • Uses AES256 encryption\n   • Can encrypt entire files or individual variables\n   • Requires vault password for decryption\n\n2. Best Practices:\n   • Store vault files separately from playbooks\n   • Use different vault passwords for different environments\n   • Never commit vault passwords to version control\n   • Use --ask-vault-pass or vault password files\n   • Consider external secret management systems for production"
  }
];

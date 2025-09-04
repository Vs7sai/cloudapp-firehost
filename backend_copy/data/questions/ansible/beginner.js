module.exports = [
  {
    "id": 1,
    "text": "What is Ansible?",
    "textSize": 26,
    "explanation": "Ansible is an open-source automation tool that simplifies configuration management, application deployment, and task automation. It uses a simple, human-readable language (YAML) to describe automation tasks.\n\nKey features:\n• Agentless: No software to install on managed nodes\n• Idempotent: Safe to run multiple times\n• Simple: Easy to learn and use\n• Powerful: Can manage complex deployments"
  },
  {
    "id": 2,
    "text": "What is an Ansible Playbook?",
    "textSize": 22,
    "explanation": "An Ansible Playbook is a YAML file that defines a series of tasks to be executed on managed hosts. Playbooks are the primary way to use Ansible for configuration management and deployment.\n\nPlaybooks contain:\n• Plays: Define which hosts to target\n• Tasks: Individual actions to perform\n• Variables: Data that can change behavior\n• Handlers: Tasks triggered by other tasks\n• Templates: Dynamic file generation"
  },
  {
    "id": 3,
    "text": "What is an Ansible Inventory?",
    "textSize": 20,
    "explanation": "Ansible Inventory is a file that defines the hosts and groups of hosts that Ansible will manage. It can be static (INI or YAML file) or dynamic (script that queries external systems).\n\nInventory can include:\n• Host definitions with IP addresses or hostnames\n• Groups to organize related hosts\n• Variables specific to hosts or groups\n• Connection parameters (SSH keys, ports, users)\n• Host patterns for targeting specific systems"
  }
];

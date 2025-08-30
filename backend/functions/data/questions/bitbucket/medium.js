module.exports = [
  {
    "id": 1,
    "text": "Explain Bitbucket Pipelines and CI/CD",
    "explanation": "Bitbucket Pipelines is a built-in CI/CD service that automates building, testing, and deploying code.\n\nKey features:\n• YAML-based configuration (bitbucket-pipelines.yml)\n• Docker-based build environments\n• Parallel and sequential pipeline steps\n• Built-in variables and secrets management\n• Integration with Bitbucket repositories\n\nPipeline structure:\n• Pipelines: Complete CI/CD workflows\n• Steps: Individual tasks within a pipeline\n• Scripts: Commands to execute\n• Services: Additional services (databases, caches)\n\nBenefits:\n• No external CI/CD setup required\n• Tight integration with Bitbucket\n• Easy to configure and maintain\n• Supports multiple programming languages"
  },
  {
    "id": 2,
    "text": "How do you manage permissions in Bitbucket?",
    "explanation": "Bitbucket provides several levels of permission management:\n\nRepository permissions:\n• Admin: Full control over repository\n• Write: Can push code and manage issues\n• Read: Can view and clone repository\n\nProject permissions:\n• Project Admin: Manage project settings\n• Project Write: Create repositories\n• Project Read: View project repositories\n\nUser management:\n• Add/remove users from teams\n• Assign roles and permissions\n• Manage group access\n• Set up SSO integration\n\nBest practices:\n• Use groups for team management\n• Follow principle of least privilege\n• Regular permission audits\n• Document access policies"
  },
  {
    "id": 3,
    "text": "What are Bitbucket webhooks and how to use them?",
    "explanation": "Bitbucket webhooks allow external services to be notified when events occur in your repository.\n\nSupported events:\n• Repository push\n• Pull request created/updated/merged\n• Issue created/updated\n• Commit comments\n• Build status changes\n\nWebhook configuration:\n• URL endpoint to receive notifications\n• Event types to listen for\n• Authentication (if required)\n• Retry settings\n\nCommon use cases:\n• Trigger external CI/CD systems\n• Update issue trackers\n• Send notifications to chat platforms\n• Sync with project management tools\n• Automated testing triggers\n\nSecurity considerations:\n• Use HTTPS endpoints\n• Implement authentication\n• Validate webhook payloads\n• Monitor webhook failures"
  }
];

module.exports = [
  {
    "id": 1,
    "text": "Explain Kubernetes Services and their types",
    "explanation": "Kubernetes Services provide stable network endpoints for Pods. Since Pods are ephemeral, Services ensure consistent access to your applications.\n\nService types:\n• ClusterIP: Internal access within cluster\n• NodePort: Exposes service on each node's IP\n• LoadBalancer: Creates external load balancer\n• ExternalName: Maps service to external DNS name\n\nServices use selectors to determine which Pods to route traffic to."
  },
  {
    "id": 2,
    "text": "What are ConfigMaps and Secrets?",
    "explanation": "ConfigMaps and Secrets are used to separate configuration from application code.\n\nConfigMaps:\n• Store non-sensitive configuration data\n• Can be consumed as environment variables, command-line arguments, or config files\n• Allows you to change configuration without rebuilding images\n\nSecrets:\n• Store sensitive data like passwords, tokens, keys\n• Base64 encoded (not encrypted by default)\n• Should be used with additional security measures in production"
  },
  {
    "id": 3,
    "text": "How do you scale applications in Kubernetes?",
    "explanation": "Kubernetes provides multiple scaling mechanisms:\n\n1. Manual Scaling: kubectl scale deployment\n2. Horizontal Pod Autoscaler (HPA): Automatically scales based on CPU/memory/custom metrics\n3. Vertical Pod Autoscaler (VPA): Adjusts resource requests/limits\n4. Cluster Autoscaler: Scales the cluster nodes themselves\n\nHPA is most commonly used, monitoring metrics and adjusting replica count automatically."
  }
];

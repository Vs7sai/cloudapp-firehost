module.exports = [
  {
    "id": 1,
    "text": "Design a production-ready Kubernetes cluster architecture",
    "explanation": "A production Kubernetes cluster should include:\n\n1. Multi-master setup for HA control plane\n2. ETCD cluster with proper backup strategy\n3. Network policies for security\n4. RBAC for access control\n5. Resource quotas and limits\n6. Monitoring with Prometheus/Grafana\n7. Logging with ELK/EFK stack\n8. Ingress controller for traffic management\n9. Storage classes for persistent volumes\n10. Regular security scanning and updates"
  },
  {
    "id": 2,
    "text": "Explain Kubernetes networking model and CNI",
    "explanation": "Kubernetes networking follows these principles:\n\n1. Every Pod gets its own IP address\n2. Pods can communicate without NAT\n3. Agents on nodes can communicate with all Pods\n\nCNI (Container Network Interface) plugins implement this model:\n• Flannel: Simple overlay network\n• Calico: Policy-rich networking with BGP\n• Weave: Encrypted networking\n• Cilium: eBPF-based networking with advanced security\n\nEach CNI has different features for security, performance, and policy enforcement."
  },
  {
    "id": 3,
    "text": "How do you implement security best practices in Kubernetes?",
    "explanation": "Kubernetes security best practices include:\n\n1. RBAC: Implement least-privilege access control\n2. Network Policies: Restrict pod-to-pod communication\n3. Pod Security Standards: Enforce security contexts\n4. Image Security: Scan images, use trusted registries\n5. Secrets Management: Use external secret managers\n6. Regular Updates: Keep cluster and nodes updated\n7. Audit Logging: Monitor API server access\n8. Resource Limits: Prevent resource exhaustion\n9. Admission Controllers: Validate/mutate resources\n10. Runtime Security: Monitor container behavior"
  }
];

module.exports = [
  {
    "id": 1,
    "text": "Explain Docker Swarm and container orchestration",
    "explanation": "Docker Swarm is Docker's native clustering and orchestration tool for managing multiple Docker hosts as a single virtual system.\n\nKey concepts:\n• Manager nodes: Control the swarm and orchestrate containers\n• Worker nodes: Run containers as assigned by managers\n• Services: Define how containers should run across the swarm\n• Tasks: Individual container instances\n• Load balancing: Automatic distribution of requests\n\nSwarm vs Kubernetes:\n• Swarm: Simpler, integrated with Docker, good for smaller deployments\n• Kubernetes: More features, larger ecosystem, better for complex deployments"
  },
  {
    "id": 2,
    "text": "What are Docker security best practices?",
    "explanation": "Docker security best practices:\n\n1. Image Security:\n• Use official base images\n• Scan images for vulnerabilities\n• Use minimal base images\n• Keep images updated\n\n2. Container Runtime:\n• Run as non-root user\n• Use read-only filesystems\n• Limit resource usage\n• Enable security profiles (AppArmor/SELinux)\n\n3. Network Security:\n• Use custom networks\n• Limit port exposure\n• Use secrets management\n• Enable TLS encryption\n\n4. Host Security:\n• Keep Docker daemon updated\n• Limit Docker daemon access\n• Use Docker Bench for Security\n• Monitor container behavior"
  },
  {
    "id": 3,
    "text": "How do you troubleshoot Docker performance issues?",
    "explanation": "Docker performance troubleshooting approach:\n\n1. Resource Monitoring:\n• docker stats: Real-time resource usage\n• docker system df: Disk space usage\n• docker system events: System events\n\n2. Common Issues:\n• Memory leaks in applications\n• Inefficient Dockerfile layers\n• Too many containers on single host\n• Network bottlenecks\n• Storage driver performance\n\n3. Optimization:\n• Use appropriate resource limits\n• Optimize application code\n• Use caching strategies\n• Choose right base images\n• Monitor with tools like Prometheus/Grafana\n• Use health checks for better orchestration"
  }
];

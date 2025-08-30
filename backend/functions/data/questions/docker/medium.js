module.exports = [
  {
    "id": 1,
    "text": "Explain Docker networking and its types",
    "explanation": "Docker provides several network drivers:\n\n1. Bridge (default): Isolated network for containers on same host\n2. Host: Container uses host's network directly\n3. None: Container has no network access\n4. Overlay: Multi-host networking for Docker Swarm\n5. Macvlan: Assign MAC address to container\n\nContainers can communicate through:\n• Port mapping (-p flag)\n• Container linking (--link, deprecated)\n• Custom networks (docker network create)\n• Environment variables"
  },
  {
    "id": 2,
    "text": "What are Docker volumes and why use them?",
    "explanation": "Docker volumes are used to persist data generated and used by containers. Data in containers is ephemeral by default.\n\nTypes of data persistence:\n• Volumes: Managed by Docker, stored in Docker area\n• Bind mounts: Mount host directory into container\n• tmpfs mounts: Stored in host memory only\n\nVolumes are preferred because they:\n• Are managed by Docker\n• Work across platforms\n• Can be safely shared between containers\n• Support volume drivers for remote storage"
  },
  {
    "id": 3,
    "text": "How do you optimize Docker images?",
    "explanation": "Docker image optimization techniques:\n\n1. Use smaller base images (alpine, distroless)\n2. Minimize layers by combining RUN commands\n3. Use multi-stage builds to reduce final image size\n4. Order Dockerfile instructions by frequency of change\n5. Use .dockerignore to exclude unnecessary files\n6. Remove package managers and caches\n7. Use specific tags instead of 'latest'\n8. Run containers as non-root users\n9. Use COPY instead of ADD when possible\n10. Clean up temporary files in the same layer"
  }
];

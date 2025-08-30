module.exports = [
    {
        id: 1,
        text: "What is Alibaba Cloud (Aliyun) and how does it compare to AWS?",
        explanation: "Alibaba Cloud (Aliyun) is Alibaba Group's cloud computing platform, similar to AWS. It provides a comprehensive suite of cloud services including computing, storage, networking, databases, and AI services. Key differences include:\n\n1. **Geographic Presence**: Strong in Asia-Pacific, especially China\n2. **Pricing**: Often more competitive pricing in Asian markets\n3. **Local Compliance**: Better compliance with Chinese regulations\n4. **E-commerce Integration**: Deep integration with Alibaba's e-commerce ecosystem\n5. **AI Services**: Strong focus on AI and machine learning services"
    },
    {
        id: 2,
        text: "Explain Alibaba Cloud's Elastic Compute Service (ECS) architecture and best practices.",
        explanation: "Alibaba Cloud ECS (Elastic Compute Service) is similar to AWS EC2 and provides scalable virtual servers. Key architectural components and best practices include:\n\n**Architecture**:\n- **Instance Types**: General-purpose, compute-optimized, memory-optimized, etc.\n- **Networking**: VPC, security groups, and elastic IPs\n- **Storage**: Cloud disks, shared block storage, and object storage\n- **Load Balancing**: SLB (Server Load Balancer) for traffic distribution\n\n**Best Practices**:\n1. **Security**: Use security groups, VPC isolation, and encryption\n2. **High Availability**: Deploy across multiple availability zones\n3. **Auto Scaling**: Implement auto-scaling groups for dynamic workloads\n4. **Monitoring**: Use CloudMonitor for comprehensive monitoring\n5. **Backup**: Regular snapshots and cross-region replication"
    },
    {
        id: 3,
        text: "How does Alibaba Cloud's Object Storage Service (OSS) work and what are its advanced features?",
        explanation: "Alibaba Cloud OSS (Object Storage Service) is a highly scalable, secure, and cost-effective object storage service. Here's how it works and its advanced features:\n\n**How It Works**:\n- **Bucket-based Organization**: Data is organized in buckets (containers)\n- **RESTful API**: Access via HTTP/HTTPS REST APIs\n- **Global Namespace**: Unique bucket names across the entire Alibaba Cloud\n- **Data Consistency**: Strong consistency for all operations\n\n**Advanced Features**:\n1. **Lifecycle Management**: Automatic transition between storage classes\n2. **Versioning**: Keep multiple versions of objects\n3. **Cross-Region Replication**: Replicate data across regions\n4. **Server-Side Encryption**: AES-256 encryption at rest\n5. **Access Control**: Fine-grained permissions and policies\n6. **CDN Integration**: Global content delivery network\n7. **Data Transfer Acceleration**: Optimized data transfer\n8. **Event Notifications**: Trigger functions on object changes"
    }
];

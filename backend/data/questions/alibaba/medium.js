module.exports = [
    {
        id: 1,
        text: "What are the key differences between Alibaba Cloud and other major cloud providers?",
        explanation: "Alibaba Cloud has several key differentiators compared to AWS, Azure, and GCP:\n\n**Geographic Advantages**:\n- Strong presence in Asia-Pacific markets\n- Local data centers in China and surrounding regions\n- Better compliance with Asian regulations\n\n**E-commerce Integration**:\n- Deep integration with Alibaba's e-commerce platforms\n- Built-in solutions for online retail and logistics\n- Payment processing and financial services\n\n**AI and Machine Learning**:\n- Advanced AI services like PAI (Platform for AI)\n- Natural language processing for multiple Asian languages\n- Computer vision optimized for Asian markets\n\n**Cost Structure**:\n- Competitive pricing in Asian markets\n- Flexible billing options\n- Volume discounts for enterprise customers"
    },
    {
        id: 2,
        text: "How do you implement high availability in Alibaba Cloud?",
        explanation: "Implementing high availability in Alibaba Cloud involves several key strategies:\n\n**Multi-Availability Zone Deployment**:\n- Deploy resources across multiple AZs in the same region\n- Use Alibaba Cloud's built-in AZ redundancy\n- Implement cross-AZ load balancing\n\n**Load Balancing**:\n- Use Server Load Balancer (SLB) for traffic distribution\n- Configure health checks and failover\n- Implement session persistence when needed\n\n**Database High Availability**:\n- Use RDS with Multi-AZ deployment\n- Implement read replicas for read scaling\n- Use ApsaraDB for distributed databases\n\n**Storage Redundancy**:\n- Enable cross-region replication for critical data\n- Use multiple storage classes for cost optimization\n- Implement regular backup and disaster recovery"
    },
    {
        id: 3,
        text: "What security features does Alibaba Cloud provide?",
        explanation: "Alibaba Cloud offers comprehensive security features:\n\n**Network Security**:\n- Virtual Private Cloud (VPC) for network isolation\n- Security groups for instance-level firewall rules\n- DDoS protection and anti-DDoS services\n- Web Application Firewall (WAF)\n\n**Identity and Access Management**:\n- RAM (Resource Access Management) for user permissions\n- Multi-factor authentication (MFA)\n- Single Sign-On (SSO) integration\n- Role-based access control (RBAC)\n\n**Data Security**:\n- Server-side encryption for data at rest\n- SSL/TLS encryption for data in transit\n- Key Management Service (KMS)\n- Data encryption for databases\n\n**Compliance and Auditing**:\n- SOC 1, SOC 2, and SOC 3 compliance\n- ISO 27001 certification\n- Regular security audits and penetration testing\n- Comprehensive logging and monitoring"
    }
];

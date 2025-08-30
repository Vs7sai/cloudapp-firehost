module.exports = [
  {
    "id": 1,
    "text": "Design a highly available multi-tier architecture on AWS",
    "textSize": 19,
    "explanation": "A highly available multi-tier architecture should include:\n\n1. Load Balancer (ALB/NLB) across multiple AZs\n2. Web tier: Auto Scaling Group with EC2 instances in multiple AZs\n3. Application tier: Auto Scaling Group in private subnets\n4. Database tier: RDS Multi-AZ or Aurora with read replicas\n5. Use CloudFront for content delivery\n6. Implement proper security groups and NACLs\n7. Use Route 53 for DNS with health checks"
  },
  {
    "id": 2,
    "text": "Explain AWS VPC peering vs Transit Gateway",
    "textSize": 20,
    "explanation": "VPC Peering creates a direct network connection between two VPCs, but doesn't support transitive routing and becomes complex with multiple VPCs.\n\nTransit Gateway acts as a hub that controls how traffic is routed among connected VPCs, VPN connections, and AWS Direct Connect gateways. It supports transitive routing and simplifies network architecture at scale."
  },
  {
    "id": 3,
    "text": "How would you implement disaster recovery for a critical application?",
    "textSize": 18,
    "explanation": "A comprehensive DR strategy includes:\n\n1. Multi-region deployment with RTO/RPO requirements\n2. Database replication (RDS Cross-Region, DynamoDB Global Tables)\n3. Automated backups and point-in-time recovery\n4. Infrastructure as Code for quick recreation\n5. Regular DR testing and runbooks\n6. Route 53 health checks for automatic failover\n7. Consider pilot light, warm standby, or hot site approaches"
  }
];

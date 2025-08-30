module.exports = [
  {
    "id": 1,
    "text": "Explain the difference between EC2 and Lambda",
    "textSize": 22,
    "explanation": "EC2 (Elastic Compute Cloud) provides virtual servers where you have full control over the operating system and can run applications continuously. You pay for the time the instance is running.\n\nLambda is a serverless compute service that runs your code in response to events. You only pay for the compute time consumed when your code is executing. Lambda automatically manages the underlying infrastructure."
  },
  {
    "id": 2,
    "text": "What is the difference between S3 storage classes?",
    "textSize": 20,
    "explanation": "AWS S3 offers different storage classes for different use cases:\n\n• S3 Standard: For frequently accessed data\n• S3 Intelligent-Tiering: Automatically moves data between tiers\n• S3 Standard-IA: For infrequently accessed data\n• S3 One Zone-IA: Lower cost for infrequent access\n• S3 Glacier: For long-term archival\n• S3 Glacier Deep Archive: Lowest cost for long-term storage"
  },
  {
    "id": 3,
    "text": "How does Auto Scaling work in AWS?",
    "textSize": 21,
    "explanation": "Auto Scaling automatically adjusts the number of EC2 instances in your application based on demand. It uses scaling policies based on metrics like CPU utilization, network traffic, or custom metrics.\n\nWhen demand increases, Auto Scaling launches new instances. When demand decreases, it terminates instances to save costs while maintaining application availability."
  }
];

module.exports = [
  {
    "id": 1,
    "text": "What is Kubernetes?",
    "textSize": 22,
    "explanation": "Kubernetes (K8s) is an open-source container orchestration platform that automates the deployment, scaling, and management of containerized applications. It was originally developed by Google and is now maintained by the Cloud Native Computing Foundation.\n\nKubernetes helps manage containers across multiple machines, providing features like load balancing, service discovery, and automatic scaling."
  },
  {
    "id": 2,
    "text": "What is a Pod in Kubernetes?",
    "textSize": 20,
    "explanation": "A Pod is the smallest deployable unit in Kubernetes. It represents a group of one or more containers that share storage, network, and a specification for how to run the containers.\n\nPods are ephemeral - they can be created, destroyed, and recreated. Usually, a Pod contains just one container, but it can contain multiple tightly coupled containers that need to work together."
  },
  {
    "id": 3,
    "text": "What is the difference between a Deployment and a Pod?",
    "textSize": 18,
    "explanation": "A Pod is a single instance of your application, while a Deployment is a higher-level object that manages Pods.\n\nDeployments provide:\n• Declarative updates for Pods\n• Replica management (ensuring desired number of Pods)\n• Rolling updates and rollbacks\n• Self-healing capabilities\n\nYou typically don't create Pods directly; instead, you create Deployments that manage Pods for you."
  }
];

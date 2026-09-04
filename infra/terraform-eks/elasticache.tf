resource "aws_security_group" "elasticache" {
  name   = "e-commerce-elasticache-sg"
  vpc_id = aws_vpc.eks_vpc.id

  ingress {
    description     = "Redis from EKS worker nodes"
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_eks_cluster.main.vpc_config[0].cluster_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "e-commerce-elasticache-sg"
  }
}

resource "aws_elasticache_subnet_group" "redis" {
  name       = "e-commerce-elasticache-subnet-group"
  subnet_ids = aws_subnet.database[*].id
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id      = "e-commerce-redis"
  engine          = "redis"
  engine_version  = "7.0"
  node_type       = "cache.t3.micro"
  num_cache_nodes = 1
  port            = 6379

  subnet_group_name = aws_elasticache_subnet_group.redis.name
  security_group_ids = [aws_security_group.elasticache.id]

  tags = {
    Name = "e-commerce-redis"
  }
}
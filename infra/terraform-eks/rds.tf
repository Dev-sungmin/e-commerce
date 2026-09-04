resource "aws_security_group" "rds" {
  name   = "e-commerce-rds-sg"
  vpc_id = aws_vpc.eks_vpc.id

  ingress {
    description     = "MySQL from EKS worker nodes"
    from_port       = 3306
    to_port         = 3306
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
    Name = "e-commerce-rds-sg"
  }
}

resource "aws_db_subnet_group" "rds" {
  name       = "e-commerce-rds-subnet-group"
  subnet_ids = aws_subnet.database[*].id

  tags = {
    Name = "e-commerce-rds-subnet-group"
  }
}

resource "aws_db_instance" "mysql" {
  identifier     = "e-commerce-mysql"
  engine         = "mysql"
  engine_version = "8.0"
  instance_class = "db.t3.micro"

  allocated_storage = 20
  storage_type      = "gp3"

  db_name  = "ecommerce"
  username = "admin"
  password = var.rds_master_password

  db_subnet_group_name  = aws_db_subnet_group.rds.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az            = false
  publicly_accessible = false
  skip_final_snapshot = true

  backup_retention_period = 1

  tags = {
    Name = "e-commerce-mysql"
  }
}
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_iam_role" "ssm_role" {
  name = "e-commerce-ec2-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_role_policy_attachment" "ecr_pull_policy" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

resource "aws_iam_instance_profile" "ssm_profile" {
  name = "e-commerce-ec2-ssm-profile"
  role = aws_iam_role.ssm_role.name
}

resource "aws_security_group" "app_sg" {
  name = "e-commerce-app-sg"
  description = "Allow app port only (SSH access via SSM, no port 22 needed)"

  ingress {
    description = "Gateway - single entry point for all API traffic"
    from_port   = 8000
    to_port     = 8000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "app_server" {
  ami = data.aws_ami.amazon_linux.id
  instance_type = "t3.small"
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  iam_instance_profile = aws_iam_instance_profile.ssm_profile.name

  user_data = file("${path.module}/install-docker.sh")

  tags = {
    Name = "e-commerce-app-server"
  }

  lifecycle {
    ignore_changes = [ami]
  }
}

resource "aws_iam_role_policy" "s3_review_upload_policy" {
  name = "e-commerce-s3-review-upload-policy"
  role = aws_iam_role.ssm_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["s3:PutObject"]
      Resource = "${aws_s3_bucket.review_images.arn}/*"
    }]
  })
}

resource "aws_iam_role_policy" "ssm_parameter_read_policy" {
  name = "e-commerce-ssm-parameter-read-policy"
  role = aws_iam_role.ssm_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["ssm:GetParameter", "ssm:GetParameters", "ssm:GetParametersByPath"]
      Resource = [
        "arn:aws:ssm:${var.aws_region}:*:parameter/e-commerce/prod",
        "arn:aws:ssm:${var.aws_region}:*:parameter/e-commerce/prod/*"
      ]
    }]
  })
}
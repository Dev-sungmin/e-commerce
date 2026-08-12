resource "aws_s3_bucket" "review_images" {
  bucket = "e-commerce-review-images-sungmin2026"
}

resource "aws_s3_bucket_public_access_block" "review_images" {
  bucket = aws_s3_bucket.review_images.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "review_images" {
  bucket = aws_s3_bucket.review_images.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "PublicReadGetObject"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:GetObject"
      Resource  = "${aws_s3_bucket.review_images.arn}/*"
    }]
  })

  depends_on = [aws_s3_bucket_public_access_block.review_images]
}

output "review_images_bucket_name" {
  value = aws_s3_bucket.review_images.bucket
}
resource "aws_s3_bucket" "codebuild" {
  bucket = "codebuild-bucket-c7b8df61-14fc-4987-b59e-b9ab843c3216"
  acl    = "private"
  force_destroy = true
}

resource "aws_codebuild_project" "codebuild" {
  name          = "tf-codebuild"
  description   = "tf_codebuild_project"
  build_timeout = "10"
  service_role  = "${aws_iam_role.codebuild.arn}"

  source {
    type = "CODEPIPELINE"
    buildspec = "buildspec.yml"
  }

  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type                = "BUILD_GENERAL1_SMALL"
    image                       = "aws/codebuild/standard:2.0"
    type                        = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"
  }
}

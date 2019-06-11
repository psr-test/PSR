provider "aws" {
  access_key = var.access_key
  secret_key = var.secret_key
  region     = var.region
}

provider "github" {
  token        = "${var.github_token}"
  organization = "${var.github_organization}"
}

resource "aws_s3_bucket" "pipeline" {
  bucket = "pipeline-bucket-c7b8df61-14fc-4987-b59e-b9ab843c3216"
  acl    = "private"
}

resource "aws_iam_role" "pipeline" {
  name = "pipeline-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "codepipeline.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "pipeline" {
  name = "codepipeline_policy"
  role = "${aws_iam_role.pipeline.id}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect":"Allow",
      "Action": [
        "s3:GetObject",
        "s3:GetObjectVersion",
        "s3:GetBucketVersioning"
      ],
      "Resource": [
        "${aws_s3_bucket.pipeline.arn}",
        "${aws_s3_bucket.pipeline.arn}/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "codebuild:BatchGetBuilds",
        "codebuild:StartBuild"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_kms_key" "pipeline" {
  description = "Code Pipeline key"
}

resource "aws_kms_alias" "pipeline" {
  name          = "alias/pipeline_alias"
  target_key_id = "${aws_kms_key.pipeline.key_id}"
}

resource "aws_codepipeline" "pipeline" {
  name     = "tf-test-pipeline"
  role_arn = "${aws_iam_role.pipeline.arn}"

  artifact_store {
    location = "${aws_s3_bucket.pipeline.bucket}"
    type     = "S3"

    encryption_key {
      id   = "${aws_kms_alias.pipeline.arn}"
      type = "KMS"
    }
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner            = "ThirdParty"
      provider         = "GitHub"
      version          = "1"
      output_artifacts = ["test"]

      configuration = {
        OAuthToken = "${var.github_token}"
        Owner  = "psr-test"
        Repo   = "PSR"
        Branch = "library"
      }
    }
  }

  stage {
    name = "Build"

    action {
      name            = "Build"
      category        = "Build"
      owner           = "AWS"
      provider        = "CodeBuild"
      input_artifacts = ["test"]
      version         = "1"

      configuration = {
        ProjectName = "test"
      }
    }
  }
}

# A shared secret between GitHub and AWS that allows AWS
# CodePipeline to authenticate the request came from GitHub.
# Would probably be better to pull this from the environment
# or something like SSM Parameter Store.
locals {
  webhook_secret = "super-secret"
}

resource "aws_codepipeline_webhook" "pipeline" {
  name            = "test-webhook-github-bar"
  authentication  = "GITHUB_HMAC"
  target_action   = "Source"
  target_pipeline = "${aws_codepipeline.pipeline.name}"

  authentication_configuration {
    secret_token = "${local.webhook_secret}"
  }

  filter {
    json_path    = "$.ref"
    match_equals = "refs/heads/{Branch}"
  }
}

# Wire the CodePipeline webhook into a GitHub repository.
resource "github_repository_webhook" "pipeline" {
  repository = "PSR"

  configuration {
    url          = "${aws_codepipeline_webhook.pipeline.url}"
    content_type = "json"
    insecure_ssl = true
    secret       = "${local.webhook_secret}"
  }

  events = ["push"]
}

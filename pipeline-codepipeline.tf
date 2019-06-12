provider "aws" {
  access_key = var.access_key
  secret_key = var.secret_key
  region     = var.region
}

resource "aws_s3_bucket" "codepipeline" {
  bucket = "codepipeline-bucket-c7b8df61-14fc-4987-b59e-b9ab843c3216"
  acl = "private"
  force_destroy = true
}

resource "aws_codepipeline" "codepipeline" {
  name     = "tf-codepipeline"
  role_arn = "${aws_iam_role.codebuild.arn}"

  artifact_store {
    location = "${aws_s3_bucket.codepipeline.bucket}"
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner            = "ThirdParty"
      provider         = "GitHub"
      version          = "1"
      output_artifacts = ["SourceArtifact"]

      configuration = {
        OAuthToken = "${var.github_token}"
        Owner  = "psr-test"
        Repo   = "PSR"
        Branch = "master"
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
      input_artifacts = ["SourceArtifact"]
      output_artifacts = ["BuildArtifact"]
      version         = "1"

      configuration = {
        ProjectName = "${aws_codebuild_project.codebuild.name}"
      }
    }
  }

  stage {
    name = "DeployProd"

    action {
      name      = "ApprovalStage"
      category  = "Approval"
      owner     = "AWS"
      provider  = "Manual"
      run_order = 1
      version   = "1"
    }

    action {
      name            = "Deploy"
      category        = "Build"
      owner           = "AWS"
      provider        = "CodeBuild"
      input_artifacts = ["BuildArtifact"]
      run_order       = 2
      version         = "1"

      configuration = {
        ProjectName = "${aws_codebuild_project.deploy_prod.name}"
      }
    }
  }
}


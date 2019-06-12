resource "aws_codebuild_project" "deploy_prod" {
  name          = "${aws_codebuild_project.codebuild.name}-deploy-prod"
  description   = "${aws_codebuild_project.codebuild.name} Project Deploy Prod"
  build_timeout = "12"
  service_role  = "${aws_iam_role.codebuild.arn}"

  environment {
    compute_type                = "BUILD_GENERAL1_SMALL"
    image                       = "aws/codebuild/standard:2.0"
    type                        = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"

    environment_variable {
      name  = "env"
      value = "prod"
    }
  }

  source {
    type = "CODEPIPELINE"
    buildspec = "buildspec-deploy.yml"
  }

  artifacts {
    type = "CODEPIPELINE"
  }
}

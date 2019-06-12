resource "aws_iam_role" "codebuild" {
  name = "codebuild-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "codebuild.amazonaws.com",
          "codepipeline.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "codebuild" {
  name        = "codebuild-policy"
  path        = "/service-role/"
  description = "Policy used in trust relationship with CodeBuild"

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "codebuild:*",
      "Resource": "${aws_codebuild_project.codebuild.id}"
    },
    {
      "Effect": "Allow",
      "Resource": [
        "*"
      ],
      "Action": [
        "autoscaling:*",
        "codebuild:*",
        "ec2:*",
        "elasticloadbalancing:*",
        "iam:*",
        "logs:*",
        "rds:DescribeDBInstances",
        "route53:*",
        "s3:*",
        "cloudformation:*",
        "apigateway:*",
        "dynamodb:*",
        "lambda:*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "ssm:GetParameters",
        "ssm:PutParameter"
      ],
      "Resource": "arn:aws:ssm:${var.region}:${var.account_id}:parameter/*"
    }
  ]
}
POLICY
}

resource "aws_iam_policy_attachment" "codebuild_policy_attachment" {
  name       = "codebuild-policy-attachment"
  policy_arn = "${aws_iam_policy.codebuild.arn}"
  roles      = ["${aws_iam_role.codebuild.id}"]
}
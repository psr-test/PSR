npm install -g serverless@1.41.1
serverless deploy --stage $env --package $CODEBUILD_SRC_DIR/target/$env -v -r eu-central-1
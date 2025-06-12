#!/bin/bash

# Ensure AWS CLI is configured (if necessary)
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

# Create Lambda function
awslocal lambda create-function \
    --function-name healthcheck-monitoring \
    --zip-file fileb://target/lambda.zip \
    --handler com.maps.pagamentos.healthcheck.lambda.HealthCheckFunction \
    --runtime java21 \
    --role arn:aws:iam::000000000000:role/lambda-role
# S3ShareBox
This repository contains the backend part of **S3ShareBox**, a cloud storage webapp using AWS S3 for storing files and AWS Cognito for manage user accounts. 
The backend server repository is located at [s3sharebox-front](https://github.com/avergel/s3sharebox-front).

## Features
- Authentication is provided via JWT token and authenticated against AWS Cognito
- Every user has its own space in a common bucket for all users
- Users can upload and download files
- *Work In Progress: Files can be moved, renamed and deleted*
- *WIP Users can signup, change passwords and use the `forgot password` functionality*

## Technologies and frameworks used
- Spring Boot 2.2
- AWS Java SDK 1.11.658
- nimbus-jose-jwt 1.23

## Configuration
The following configuration properties are mapped to its own environment variables
### application.properties
- `aws.accessKeyId`: The access key for the user with permissions to access Cognito and s3
- `aws.secretKey`: The secret key for the user with permissions to access Cognito and s3
- `frontendServer`: Frontend Server URL for CORS
### cognito.properties
- `pool-id`=`${S3SHAREBOX_COGNITO_POOL_ID}`
- `client-app-id`=`${S3SHAREBOX_COGNITO_CLIENT_APP_ID}`
- `client-app-secret`=`${S3SHAREBOX_COGNITO_CLIENT_APP_SECRET}`
- `region`= `eu-west-1`
- `identity-pool-id`=`${S3SHAREBOX_COGNITO_IDENTITY_POOL_ID}`
### s3.properties
- `region`
- `bucketName` = `S3SHAREBOX_S3_BUCKET_NAME`

## Dependencies
- Maven

## Installation
Clone repository and perform a `mvn clean install`.

## Run app
Run this project as a Spring Boot Application, either using java executable or maven plugin:
- `java -jar target/s3sharebox-system-0.0.1-SNAPSHOT.jar`
- `mvn spring-boot:run`

## Run app with Docker
In the project root folder, run `docker compose up`. It will expose the client on http://localhost:8080 url

The environment variables can be specified on the `.env` file (not included in the repository)

## Run tests
Run `mvn clean test` in the project directory

## License
Copyright (c) 2019 Alberto Vergel licensed under the MIT license.
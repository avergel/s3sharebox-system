package com.avergel.s3shareboxsystem.infrastructure.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSClient {
    private final CognitoConfig cognitoConfig;
    private final S3Config s3Config;

    public AWSClient(CognitoConfig cognitoConfig, S3Config s3Config) {
        this.cognitoConfig = cognitoConfig;
        this.s3Config = s3Config;
    }

    @Bean
    public AWSCognitoIdentityProvider cognitoClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                                                      .withRegion(cognitoConfig.getRegion())
                                                      .build();
    }

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(s3Config.getRegion())
                                    .build();
    }
}

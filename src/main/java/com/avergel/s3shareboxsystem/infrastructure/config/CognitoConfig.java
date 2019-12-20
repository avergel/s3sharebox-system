/**
 * 
 */
package com.avergel.s3shareboxsystem.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
@Configuration
@PropertySource("classpath:cognito.properties")
@ConfigurationProperties
@Data
public class CognitoConfig {

	private static final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
	private static final String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";

	private String clientAppId;
	private String poolId;
	private String region;
	private String identityPoolId;
	private String developerGroup;
	private String clientAppSecret;

	private String userNameField = "cognito:username";
	private String groupsField = "cognito:groups";
	private int connectionTimeout = 2000;
	private int readTimeout = 2000;
	private String httpHeader = "Authorization";

	public String getJwkUrl() {
		StringBuilder cognitoURL = new StringBuilder();
		cognitoURL.append(COGNITO_IDENTITY_POOL_URL);
		cognitoURL.append(JSON_WEB_TOKEN_SET_URL_SUFFIX);
		return String.format(cognitoURL.toString(), region, poolId);
	}

	public String getCognitoIdentityPoolUrl() {
		return String.format(COGNITO_IDENTITY_POOL_URL, region, poolId);
	}


}
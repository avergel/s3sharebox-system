/**
 * 
 */
package com.avergel.s3shareboxsystem.infrastructure.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:s3.properties")
@ConfigurationProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3Config {
	private String region;
	private String bucketName;
}
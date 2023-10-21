package com.demo.learnify.configuration.s3

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.time.Duration

@Configuration
class S3Config(
    private val s3ConfigProperties: AWSProperties
) {

    @Bean
    fun s3AsyncClient(awsCredentialsProvider: AwsCredentialsProvider): S3AsyncClient =
        S3AsyncClient.builder()
            .httpClient(sdkAsyncHttpClient())
            .region(Region.of(s3ConfigProperties.region))
            .credentialsProvider(awsCredentialsProvider)
            .serviceConfiguration(s3Configuration()).build()

    private fun sdkAsyncHttpClient(): SdkAsyncHttpClient =
        NettyNioAsyncHttpClient.builder()
            .writeTimeout(Duration.ZERO)
            .maxConcurrency(64)
            .build()

    private fun s3Configuration(): S3Configuration =
        S3Configuration.builder()
            .checksumValidationEnabled(false)
            .chunkedEncodingEnabled(true)
            .build()

    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider =
        AwsCredentialsProvider {
            AwsBasicCredentials.create(
                s3ConfigProperties.accessKey,
                s3ConfigProperties.secretKey
            )
        }
}
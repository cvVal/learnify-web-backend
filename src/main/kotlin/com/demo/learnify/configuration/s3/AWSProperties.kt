package com.demo.learnify.configuration.s3

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
data class AWSProperties(
    /**
     * Aws access key ID
     */
    val accessKey: String,

    /**
     * Aws secret access key
     */
    val secretKey: String,

    /**
     * Aws region
     */
    val region: String,

    /**
     * Aws S3 bucket name
     */
    val s3BucketName: String,

    /**
     * AWS S3 requires that file parts must have at least 5MB, except for the last part.
     */
    val multipartMinPartSize: Int
)
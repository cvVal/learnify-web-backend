package com.demo.learnify.routers.s3

import com.demo.learnify.handlers.s3.S3Handler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class AWSS3Router {

    @Bean
    fun s3Routes(awsS3Handler: S3Handler): RouterFunction<ServerResponse> {
        return router {
            POST("/s3/upload", awsS3Handler::uploadS3Object)
            GET("/s3/{fileKey}/bytes", awsS3Handler::downloadS3ObjectInBytes)
            GET("/s3", awsS3Handler::downloadS3Objects)
            GET("/s3/{fileKey}", awsS3Handler::downloadS3Object)
            DELETE("/s3/{objectKey}", awsS3Handler::deleteS3Object)
        }
    }
}

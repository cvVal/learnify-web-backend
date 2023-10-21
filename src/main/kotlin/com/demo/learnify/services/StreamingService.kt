package com.demo.learnify.services

import org.springframework.core.io.Resource
import reactor.core.publisher.Mono

interface StreamingService {

    fun getVideoLocallyStored(title: String): Mono<Resource>
    fun getVideoFromS3(title: String): Mono<Resource>
}
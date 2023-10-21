package com.demo.learnify.services

import com.demo.learnify.errorhandler.exceptions.ResourceNotFoundException
import com.demo.learnify.services.s3.S3FileStorageService
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StreamingServiceImpl(
    private val resourceLoader: ResourceLoader,
    private val s3FileStorageService: S3FileStorageService
) : StreamingService {

    override fun getVideoLocallyStored(title: String): Mono<Resource> =
        Mono.fromSupplier { resourceLoader.getResource(String.format(FORMAT, title)) }
            .filter { it.exists() }
            .switchIfEmpty(Mono.error(ResourceNotFoundException("Resource not found")))

    override fun getVideoFromS3(title: String): Mono<Resource> =
        s3FileStorageService.getS3ObjectBytes(title)
            .map { bytes -> ByteArrayResource(bytes) }

    companion object {
        const val FORMAT = "classpath:videos/%s.mp4"
    }
}
package com.demo.learnify.handlers.s3

import com.demo.learnify.dtos.s3.SuccessResponse
import com.demo.learnify.services.s3.S3FileStorageService
import com.demo.learnify.utils.FileUtils
import com.demo.learnify.utils.successJsonResponse
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.text.MessageFormat

@Component
class S3Handler(
    private val s3Service: S3FileStorageService,
    private val fileUtils: FileUtils
) {

    fun uploadS3Object(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest
            .body(BodyExtractors.toMultipartData())
            .map { map -> map["file-data"]?.get(0) }
            .cast(FilePart::class.java)
            .doOnNext { filePart -> fileUtils.filePartValidator(filePart) }
            .flatMap { filePart -> s3Service.uploadS3Object(filePart) }
            .flatMap { fileResponse -> successJsonResponse(SuccessResponse(fileResponse, "Upload successfully")) }
    }

    fun downloadS3ObjectInBytes(serverRequest: ServerRequest): Mono<ServerResponse> {
        val fileKey = serverRequest.pathVariable("fileKey")
        return s3Service.getS3ObjectBytes(fileKey)
            .flatMap { objectKey -> successJsonResponse(SuccessResponse(objectKey, "Object byte response")) }
    }

    fun downloadS3Object(serverRequest: ServerRequest): Mono<ServerResponse> {
        val fileKey = serverRequest.pathVariable("fileKey")
        return s3Service.getS3Object(fileKey)
            .flatMap { objectKey -> successJsonResponse(SuccessResponse(objectKey, "Object response")) }
    }

    fun downloadS3Objects(serverRequest: ServerRequest): Mono<ServerResponse> {
        return s3Service.getS3Objects()
            .flatMap { objectKey -> successJsonResponse(SuccessResponse(objectKey, "${objectKey.size} results found")) }
    }

    fun deleteS3Object(serverRequest: ServerRequest): Mono<ServerResponse> {
        val objectKey = serverRequest.pathVariable("objectKey")
        return s3Service.deleteS3Object(objectKey)
            .flatMap {
                successJsonResponse(
                    SuccessResponse(
                        null,
                        MessageFormat.format(
                            "Object with key: {0} deleted successfully",
                            objectKey
                        )
                    )
                )
            }
    }
}

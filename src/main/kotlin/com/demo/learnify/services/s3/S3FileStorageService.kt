package com.demo.learnify.services.s3

import com.demo.learnify.dtos.s3.AWSS3Object
import com.demo.learnify.dtos.s3.FileResponse
import jakarta.validation.constraints.NotNull
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface S3FileStorageService {
    /**
     * Upload object in Amazon S3
     * @param filePart - the request part containing the file to save
     * @return Mono of [FileResponse] representing the result of the operation
     */
    fun uploadS3Object(filePart: FilePart): Mono<FileResponse>

    /**
     * Retrieves byte objects from Amazon S3.
     * @param objectKey object key to retrieve the object
     * @return Mono of [ByteArray]
     */
    fun getS3ObjectBytes(objectKey: @NotNull String): Mono<ByteArray>

    /**
     * Retrieves an object from Amazon S3.
     * @param objectKey object key to retrieve the object
     * @return Mono of [AWSS3Object] representing the result of the operation
     */
    fun getS3Object(objectKey: @NotNull String): Mono<AWSS3Object>

    /**
     * Returns some or all (up to 1,000) of the objects in a bucket.
     * @return Mono list of [AWSS3Object] representing the result of the operation
     */
    fun getS3Objects(): Mono<List<AWSS3Object>>

    /**
     * Delete an object from a bucket
     * @param objectKey object key to delete the object
     */
    fun deleteS3Object(objectKey: @NotNull String): Mono<Void>
}
package com.demo.learnify.services.s3

import com.demo.learnify.configuration.s3.AWSProperties
import com.demo.learnify.dtos.s3.AWSS3Object
import com.demo.learnify.dtos.s3.FileResponse
import com.demo.learnify.dtos.s3.UploadStatus
import com.demo.learnify.utils.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.core.BytesWrapper
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.nio.ByteBuffer
import java.time.Duration
import java.util.*

@Service
@EnableConfigurationProperties(AWSProperties::class)
class S3FileStorageServiceImpl(
    private val s3AsyncClient: S3AsyncClient,
    private val s3ConfigProperties: AWSProperties,
    private val fileUtils: FileUtils,
    private val awsCredentialsProvider: AwsCredentialsProvider
) : S3FileStorageService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun uploadS3Object(filePart: FilePart): Mono<FileResponse> {
        val uuid = UUID.randomUUID().toString()
        val type = filePart.filename().split(".").last()
        val fileName = "$uuid.$type"
        val metadata = mapOf("filename" to fileName)
        val mediaType = filePart.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val s3AsyncClientMultipartUpload = s3AsyncClient
            .createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType(mediaType.toString())
                    .key(fileName)
                    .metadata(metadata)
                    .bucket(s3ConfigProperties.s3BucketName)
                    .build()
            )
        val uploadStatus = UploadStatus(
            contentType = mediaType.type,
            fileKey = fileName
        )
        return Mono.fromFuture(s3AsyncClientMultipartUpload)
            .flatMapMany { response ->
                fileUtils.checkSdkResponse(response)
                uploadStatus.uploadId = response.uploadId()
                logger.info("Upload object with ID ${response.uploadId()}")
                filePart.content()
            }
            .bufferUntil { dataBuffer ->
                // Collect incoming values into multiple List buffers
                // that will be emitted by the resulting Flux each time the given predicate returns true.
                uploadStatus.addBuffered(dataBuffer.readableByteCount())
                if (uploadStatus.buffered >= s3ConfigProperties.multipartMinPartSize) {
                    logger.info(
                        "BufferUntil - bufferedBytes={}, partCounter={}, uploadId={}. Returning true",
                        uploadStatus.buffered,
                        uploadStatus.partCounter,
                        uploadStatus.uploadId
                    )
                    // reset buffer
                    uploadStatus.buffered = 0
                    true
                } else false
            }
            .map { fileUtils.dataBufferToByteBuffer(it) }
            .flatMapSequential { byteBuffer ->
                uploadPartObject(uploadStatus, byteBuffer)
            }
            .onBackpressureBuffer()
            .reduce(uploadStatus) { status, completedPart ->
                logger.info("Completed: PartNumber=${completedPart.partNumber()}, eTag=${completedPart.eTag()}")
                status.completedParts[completedPart.partNumber()] = completedPart
                status
            }
            .flatMap { completeMultipartUpload(uploadStatus) }
            .map { uploadResponse ->
                fileUtils.checkSdkResponse(uploadResponse)
                val preSignedUrl = generateSignedUrl(fileName)
                FileResponse(
                    name = fileName,
                    uploadId = uploadStatus.uploadId!!,
                    location = uploadResponse.location(),
                    type = uploadStatus.contentType,
                    eTag = uploadResponse.eTag(),
                    preSignedUrl = preSignedUrl
                )
            }
    }

    override fun getS3ObjectBytes(objectKey: String): Mono<ByteArray> =
        Mono.just(GetObjectRequest.builder().bucket(s3ConfigProperties.s3BucketName).key(objectKey).build())
            .doOnNext { logger.info("Fetching object as byte array from s3 bucket $s3ConfigProperties.s3BucketName") }
            .map { s3AsyncClient.getObject(it, AsyncResponseTransformer.toBytes()) }
            .flatMap { Mono.fromFuture(it) }
            .map(BytesWrapper::asByteArray)

    override fun getS3Object(objectKey: String): Mono<AWSS3Object> =
        Mono.just(GetObjectRequest.builder().bucket(s3ConfigProperties.s3BucketName).key(objectKey).build())
            .doOnNext { logger.info("Fetching object from s3 bucket $s3ConfigProperties.s3BucketName") }
            .map { s3AsyncClient.getObject(it, AsyncResponseTransformer.toBytes()) }
            .flatMap { Mono.fromFuture(it) }
            .map {
                AWSS3Object(
                    key = objectKey,
                    lastModified = it.response().lastModified(),
                    eTag = it.response().eTag(),
                    size = it.response().contentLength()
                )
            }

    override fun getS3Objects(): Mono<List<AWSS3Object>> =
        Flux.from(
            s3AsyncClient.listObjectsV2Paginator(
                ListObjectsV2Request.builder()
                    .bucket(s3ConfigProperties.s3BucketName)
                    .build()
            )
        )
            .flatMap { Flux.fromIterable(it.contents()) }
            .map { s3Object ->
                AWSS3Object(
                    key = s3Object.key(),
                    lastModified = s3Object.lastModified(),
                    eTag = s3Object.eTag(),
                    size = s3Object.size()
                )
            }
            .collectList()

    override fun deleteS3Object(objectKey: String): Mono<Void> =
        Mono.just(DeleteObjectRequest.builder().bucket(s3ConfigProperties.s3BucketName).key(objectKey).build())
            .map(s3AsyncClient::deleteObject)
            .flatMap { Mono.fromFuture(it) }
            .doOnNext { logger.info("Object deleted with key $objectKey") }
            .then()

    /**
     * Uploads a part in a multipart upload.
     */
    private fun uploadPartObject(
        uploadStatus: UploadStatus,
        buffer: ByteBuffer
    ): Mono<CompletedPart> {
        val partNumber = uploadStatus.incrementPartCounter()
        logger.info("UploadPart - partNumber={}, contentLength={}", partNumber, buffer.capacity())
        val uploadPartRequest = s3AsyncClient.uploadPart(
            UploadPartRequest.builder()
                .bucket(s3ConfigProperties.s3BucketName)
                .key(uploadStatus.fileKey)
                .partNumber(partNumber)
                .uploadId(uploadStatus.uploadId)
                .contentLength(buffer.capacity().toLong())
                .build(),
            AsyncRequestBody.fromPublisher(Mono.just(buffer))
        )
        return Mono.fromFuture(uploadPartRequest)
            .map { uploadPartResult ->
                fileUtils.checkSdkResponse(uploadPartResult)
                logger.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag())
                CompletedPart.builder()
                    .eTag(uploadPartResult.eTag())
                    .partNumber(partNumber)
                    .build()
            }
            .subscribeOn(Schedulers.boundedElastic())
    }

    /**
     * This method is called when a part finishes uploading.
     * It's primary function is to verify the ETag of the part we just uploaded.
     */
    private fun completeMultipartUpload(uploadStatus: UploadStatus): Mono<CompleteMultipartUploadResponse> {
        logger.info(
            "CompleteUpload - fileKey={}, completedParts.size={}",
            uploadStatus.fileKey,
            uploadStatus.completedParts.size
        )
        val multipartUpload = CompletedMultipartUpload.builder()
            .parts(uploadStatus.completedParts.values)
            .build()
        return Mono.fromFuture(
            s3AsyncClient.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                    .bucket(s3ConfigProperties.s3BucketName)
                    .uploadId(uploadStatus.uploadId)
                    .multipartUpload(multipartUpload)
                    .key(uploadStatus.fileKey)
                    .build()
            )
        )
    }

    /**
     * Requests that are pre-signed by SigV4 algorithm are valid for at most 7 days.
     */
    private fun generateSignedUrl(objectKey: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(s3ConfigProperties.s3BucketName)
            .key(objectKey)
            .build()
        val region = Region.of(s3ConfigProperties.region)
        val presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(awsCredentialsProvider)
            .build()
        return try {
            presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofDays(3))
                    .build()
            ).url().toString()
        } catch (e: S3Exception) {
            throw e
        } finally {
            presigner.close()
        }
    }
}
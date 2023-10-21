package com.demo.learnify.dtos.s3

import com.fasterxml.jackson.annotation.JsonFormat
import software.amazon.awssdk.services.s3.model.CompletedPart
import java.time.Instant

data class AWSS3Object(
    val key: String,
//    @JsonFormat(pattern="yyyy-MM-dd")
    val lastModified: Instant,
    val eTag: String,
    val size: Long
)

data class FileResponse(
    val name: String,
    val uploadId: String,
    val location: String,
    val type: String,
    val eTag: String,
    val preSignedUrl: String?
)

data class SuccessResponse(
    val data: Any?,
    val message: String
)

class UploadStatus(
    val contentType: String,
    val fileKey: String
) {
    var uploadId: String? = null
    var partCounter: Int = 0
    var buffered: Int = 0
    val completedParts: MutableMap<Int, CompletedPart> = mutableMapOf()

    fun addBuffered(count: Int) {
        buffered += count
    }

    fun incrementPartCounter(): Int {
        return ++partCounter
    }
}

package com.demo.learnify.utils

import com.demo.learnify.errorhandler.exceptions.FileValidatorException
import com.demo.learnify.errorhandler.exceptions.UploadException
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkResponse
import java.nio.ByteBuffer

@Component
class FileUtils {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val contentTypes = arrayOf(
        "image/png",
        "image/jpg",
        "image/jpeg",
        "video/mp4",
        "video/mov"
    )

    private fun isSupportedContentType(contentType: String) = contentTypes.contains(contentType)

    private fun isValidType(filePart: FilePart) = isSupportedContentType(filePart.headers().contentType.toString())

    private fun isEmpty(filePart: FilePart) =
        StringUtils.isEmpty(filePart.filename())
                && ObjectUtils.isEmpty(filePart.headers().contentType)

    fun dataBufferToByteBuffer(buffers: MutableList<DataBuffer>): ByteBuffer {
        logger.info("Creating ByteBuffer from {} chunks", buffers.size)

        var partSize = 0
        buffers.forEach { partSize += it.readableByteCount() }

        val byteArray = ByteArray(partSize)
        var offset = 0
        buffers.forEach {
            val inputStream = it.asInputStream()
            val bytesRead = inputStream.read(byteArray, offset, it.readableByteCount())
            offset += bytesRead
            inputStream.close()
        }

        val partData = ByteBuffer.wrap(byteArray)

        logger.info("PartData: capacity={}", partData.capacity())
        return partData
    }

    fun checkSdkResponse(sdkResponse: SdkResponse) {
        if (AwsSdkUtil.isErrorSdkHttpResponse(sdkResponse)) {
            val response = sdkResponse.sdkHttpResponse()
            throw UploadException("${response.statusCode()} - ${response.statusText()}")
        }
    }

    fun filePartValidator(filePart: FilePart) {
        if (isEmpty(filePart)) {
            throw FileValidatorException("File cannot be empty or null")
        }
        if (!isValidType(filePart)) {
            throw FileValidatorException("Invalid file type")
        }
    }
}

package com.demo.learnify.utils

import software.amazon.awssdk.core.SdkResponse

object AwsSdkUtil {
    fun isErrorSdkHttpResponse(sdkResponse: SdkResponse): Boolean {
        return sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful
    }
}
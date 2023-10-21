package com.demo.learnify.errorhandler

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {
    override fun getErrorAttributes(serverRequest: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
        val errorMap: MutableMap<String, Any> = HashMap()
        val error = getError(serverRequest)
        errorMap["path"] = serverRequest.path()
        errorMap["message"] = error.message!!
        return errorMap
    }
}
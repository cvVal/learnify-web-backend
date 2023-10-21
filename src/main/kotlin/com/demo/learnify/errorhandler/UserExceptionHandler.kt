package com.demo.learnify.errorhandler

import com.demo.learnify.errorhandler.exceptions.*
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

/**
 * Create a new `AbstractErrorWebExceptionHandler`.
 *
 * @param errorAttributes    the error attributes
 * @param resources          the resources configuration properties
 * @param applicationContext the application context
 * @since 2.4.0
 */
@Component
class UserExceptionHandler(
    errorAttributes: ErrorAttributes,
    resources: WebProperties.Resources,
    applicationContext: ApplicationContext,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, resources, applicationContext) {

    init {
        this.setMessageReaders(configurer.readers)
        this.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { serverRequest: ServerRequest ->
            renderException(
                serverRequest
            )
        }
    }

    private fun renderException(serverRequest: ServerRequest): Mono<ServerResponse> {
        val error = getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults())
        val status: HttpStatus
        val errorObj = getError(serverRequest)
        status = when (errorObj) {
            is NotFoundException,
            is NoSuchKeyException,
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            is ConflictException -> HttpStatus.CONFLICT
            is ForbiddenException -> HttpStatus.FORBIDDEN
            is BadRequestException -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error))
    }
}
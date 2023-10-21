package com.demo.learnify.handlers

import com.demo.learnify.services.StreamingService
import com.demo.learnify.utils.resourceJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class StreamingHandler(
    private val streamingService: StreamingService
) {

    fun getVideo(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { it.headers().header("Range") }
//            .doOnNext { println(it) }
            .flatMap { streamingService.getVideoLocallyStored(serverRequest.pathVariable("title")) }
            .flatMap { resourceJsonResponse(it) }
}
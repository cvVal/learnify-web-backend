package com.demo.learnify.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.json
import reactor.core.publisher.Mono

fun successJsonResponse(body: Any): Mono<ServerResponse> =
    ServerResponse.ok()
        .json()
        .bodyValue(body)

fun createdJsonResponse(body: Any): Mono<ServerResponse> =
    ServerResponse.status(HttpStatus.CREATED)
        .json()
        .bodyValue(body)

fun resourceJsonResponse(body: Any): Mono<ServerResponse> {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
        .body(BodyInserters.fromValue(body))
}
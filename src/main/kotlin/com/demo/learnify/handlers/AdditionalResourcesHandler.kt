package com.demo.learnify.handlers

import com.demo.learnify.dtos.ResourceDto
import com.demo.learnify.services.AdditionalResourcesService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class AdditionalResourcesHandler(
    private val resourcesService: AdditionalResourcesService
) {

    fun addAdditionalResources(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<ResourceDto>()
            .flatMap { resourcesService.addAdditionalResources(it) }
            .flatMap { createdJsonResponse(it) }


    fun updateAdditionalResources(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<ResourceDto>()
            .flatMap { resourcesService.updateAdditionalResources(it) }
            .flatMap { successJsonResponse(it) }

    fun getAdditionalResources(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("resourceId") }
            .flatMap { resourcesService.getAdditionalResources(it.toLong()) }
            .flatMap { successJsonResponse(it) }


    fun deleteAdditionalResources(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("resourceId") }
            .flatMap { resourcesService.deleteAdditionalResources(it.toLong()) }
            .flatMap { successJsonResponse(it) }
}

package com.demo.learnify.services;

import com.demo.learnify.dtos.ResourceDto;
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono;

interface AdditionalResourcesService {

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun addAdditionalResources(request: ResourceDto): Mono<ResourceDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun updateAdditionalResources(request: ResourceDto): Mono<ResourceDto>

    fun getAdditionalResources(id: Long): Mono<ResourceDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun deleteAdditionalResources(id: Long): Mono<Void>
}

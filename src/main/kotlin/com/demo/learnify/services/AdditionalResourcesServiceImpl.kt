package com.demo.learnify.services;

import com.demo.learnify.data.models.Resource
import com.demo.learnify.data.repositories.ResourceRepository
import com.demo.learnify.dtos.ResourceDto
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.LessonMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

@Service
class AdditionalResourcesServiceImpl(
    private val resourceRepository: ResourceRepository,
    private val lessonMapper: LessonMapper
): AdditionalResourcesService {

    @Transactional
    override fun addAdditionalResources(request: ResourceDto): Mono<ResourceDto> =
        lessonMapper.convertToResource(request).toMono()
            .flatMap { resourceRepository.save(it) }
            .map { lessonMapper.convertToResourceDto(it) }

    @Transactional
    override fun updateAdditionalResources(request: ResourceDto): Mono<ResourceDto> =
        resourceRepository.findById(request.id.toLong())
            .flatMap { existingResource ->
                val updatedResource = mergeDtoWithResource(request, existingResource)
                resourceRepository.save(updatedResource)
            }
            .map { lessonMapper.convertToResourceDto(it) }

    override fun getAdditionalResources(id: Long): Mono<ResourceDto> =
        resourceRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("Resource not found")))
            .map { lessonMapper.convertToResourceDto(it) }

    override fun deleteAdditionalResources(id: Long): Mono<Void> =
        resourceRepository.deleteById(id)

    fun mergeDtoWithResource(resourceDto: ResourceDto, resource: Resource) =
        resource.copy(
            name = resourceDto.name ?: resource.name,
            resourceUrl = resourceDto.resourceUrl ?: resource.resourceUrl,
            updatedAt = LocalDateTime.now()
        )
}

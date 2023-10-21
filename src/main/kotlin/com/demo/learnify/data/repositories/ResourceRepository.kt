package com.demo.learnify.data.repositories

import com.demo.learnify.data.models.Resource
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ResourceRepository: ReactiveCrudRepository<Resource, Long>

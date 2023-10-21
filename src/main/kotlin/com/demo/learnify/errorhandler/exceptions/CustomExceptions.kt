package com.demo.learnify.errorhandler.exceptions

class BadRequestException(message: String) : RuntimeException(message)

class ConflictException(message: String) : RuntimeException(message)

class FileValidatorException(message: String) : RuntimeException(message)

class ForbiddenException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : RuntimeException(message)

class UploadException(message: String) : RuntimeException(message)

class ResourceNotFoundException(message: String) : RuntimeException(message)

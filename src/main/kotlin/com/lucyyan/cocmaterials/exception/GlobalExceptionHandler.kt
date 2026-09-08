package com.lucyyan.cocmaterials.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(
        ex: ResourceNotFoundException
    ): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiError(
                    status = 404,
                    error = "Not Found",
                    message = ex.message ?: "Resource not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException
    ): ResponseEntity<ApiError> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiError(
                    status = 400,
                    error = "Bad Request",
                    message = ex.message ?: "Invalid request",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ApiError> {

        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiError(
                    status = 400,
                    error = "Validation Failed",
                    message = message,
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(DuplicateRawMaterialException::class)
    fun handleDuplicateRawMaterial(
        ex: DuplicateRawMaterialException
    ): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiError(
                    status = HttpStatus.CONFLICT.value(),
                    error = "Conflict",
                    message = ex.message ?: "Raw material already exists",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailure(
        ex: ObjectOptimisticLockingFailureException
    ): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiError(
                    status = HttpStatus.CONFLICT.value(),
                    error = "Conflict",
                    message = "The resource was modified by another request. Refresh and try again.",
                    timestamp = Instant.now()
                )
            )
    }
}

package ru.example.product.receiver.controller.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.example.product.receiver.dto.error.ApiError
import ru.example.product.receiver.exception.ProductNotFoundException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFoundException(
        ex: ProductNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val error = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            message = "Product not found",
            details = ex.message,
            timestamp = Instant.now(),
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Invalid request",
            details = ex.message,
            timestamp = Instant.now(),
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { error ->
            "${error.field}: ${error.defaultMessage}"
        }
        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            details = errors,
            timestamp = Instant.now(),
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val errors = ex.constraintViolations.joinToString(", ") { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }
        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Constraint violation",
            details = errors,
            timestamp = Instant.now(),
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val error = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Internal server error",
            details = ex.message,
            timestamp = Instant.now(),
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
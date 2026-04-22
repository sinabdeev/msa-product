package ru.example.product.receiver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.example.product.receiver.domain.ProductDto
import ru.example.product.receiver.dto.request.CreateProductRequest
import ru.example.product.receiver.dto.request.UpdateProductRequest
import ru.example.product.receiver.dto.response.ApiResponse
import ru.example.product.receiver.service.ProductService
import java.util.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management API")
class ProductController(
    private val productService: ProductService
) {

    @Operation(summary = "Create a new product")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ApiResponse::class)
            )]
        ),
        SwaggerApiResponse(responseCode = "400", description = "Invalid input"),
        SwaggerApiResponse(responseCode = "409", description = "Product with same SKU already exists")
    )
    @PostMapping
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.createProduct(request)
        val response = ApiResponse.success("Product created successfully", product)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "Product found",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ApiResponse::class)
            )]
        ),
        SwaggerApiResponse(responseCode = "404", description = "Product not found")
    )
    @GetMapping("/{id}")
    fun getProduct(
        @Parameter(description = "Product ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.getProduct(id)
        val response = ApiResponse.success("Product retrieved successfully", product)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get all products")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "List of products",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ApiResponse::class)
            )]
        )
    )
    @GetMapping
    fun getAllProducts(): ResponseEntity<ApiResponse<List<ProductDto>>> {
        val products = productService.getAllProducts()
        val response = ApiResponse.success("Products retrieved successfully", products)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Update product")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ApiResponse::class)
            )]
        ),
        SwaggerApiResponse(responseCode = "404", description = "Product not found"),
        SwaggerApiResponse(responseCode = "400", description = "Invalid input"),
        SwaggerApiResponse(responseCode = "409", description = "Product with same SKU already exists")
    )
    @PutMapping("/{id}")
    fun updateProduct(
        @Parameter(description = "Product ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.updateProduct(id, request)
        val response = ApiResponse.success("Product updated successfully", product)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Delete product")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "204",
            description = "Product deleted successfully"
        ),
        SwaggerApiResponse(responseCode = "404", description = "Product not found")
    )
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @Parameter(description = "Product ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
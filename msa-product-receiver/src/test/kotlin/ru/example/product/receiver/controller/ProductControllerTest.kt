package ru.example.product.receiver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.example.product.receiver.domain.ProductCategory
import ru.example.product.receiver.domain.ProductDto
import ru.example.product.receiver.dto.request.CreateProductRequest
import ru.example.product.receiver.dto.request.UpdateProductRequest
import ru.example.product.receiver.exception.ProductNotFoundException
import ru.example.product.receiver.service.ProductService
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@WebMvcTest(ProductController::class)
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var productService: ProductService

    private val sampleProductId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val sampleProduct =
        ProductDto(
            id = sampleProductId,
            sku = "TEST-SKU-001",
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal("99.99"),
            quantity = 10,
            weight = 1.5,
            isAvailable = true,
            category = ProductCategory.ELECTRONICS,
            tags = listOf("test", "electronics"),
            createdAt = Instant.parse("2023-01-01T12:00:00Z"),
            updatedAt = Instant.parse("2023-01-02T14:30:00Z"),
        )

    @Test
    fun `create product should return 201`() {
        val request =
            CreateProductRequest(
                sku = "TEST-SKU-001",
                name = "Test Product",
                description = "Test Description",
                price = BigDecimal("99.99"),
                quantity = 10,
                weight = 1.5,
                isAvailable = true,
                category = "ELECTRONICS",
                tags = listOf("test", "electronics"),
            )

        `when`(productService.createProduct(request)).thenReturn(sampleProduct)

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Product created successfully"))
            .andExpect(jsonPath("$.data.id").value(sampleProductId.toString()))
            .andExpect(jsonPath("$.data.sku").value("TEST-SKU-001"))
            .andExpect(jsonPath("$.data.name").value("Test Product"))
    }

    @Test
    fun `create product with invalid data should return 400`() {
        val request =
            CreateProductRequest(
                sku = "", // Invalid: empty SKU
                name = "",
                description = "",
                price = BigDecimal("-10.0"), // Invalid: negative price
                quantity = -5, // Invalid: negative quantity
                isAvailable = true,
                category = "INVALID_CATEGORY",
                tags = emptyList(),
            )

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `get product by ID should return 200`() {
        `when`(productService.getProduct(sampleProductId)).thenReturn(sampleProduct)

        mockMvc.perform(get("/api/v1/products/{id}", sampleProductId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Product retrieved successfully"))
            .andExpect(jsonPath("$.data.id").value(sampleProductId.toString()))
            .andExpect(jsonPath("$.data.name").value("Test Product"))
    }

    @Test
    fun `get non-existent product should return 404`() {
        `when`(productService.getProduct(sampleProductId))
            .thenThrow(ProductNotFoundException(sampleProductId))

        mockMvc.perform(get("/api/v1/products/{id}", sampleProductId))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Product not found"))
    }

    @Test
    fun `get all products should return 200`() {
        val products = listOf(sampleProduct)
        `when`(productService.getAllProducts()).thenReturn(products)

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
            .andExpect(jsonPath("$.data[0].id").value(sampleProductId.toString()))
            .andExpect(jsonPath("$.data[0].name").value("Test Product"))
    }

    @Test
    fun `update product should return 200`() {
        val updateRequest =
            UpdateProductRequest(
                name = "Updated Product Name",
                price = BigDecimal("149.99"),
                quantity = 20,
            )

        val updatedProduct =
            sampleProduct.copy(
                name = "Updated Product Name",
                price = BigDecimal("149.99"),
                quantity = 20,
            )

        `when`(productService.updateProduct(sampleProductId, updateRequest)).thenReturn(updatedProduct)

        mockMvc.perform(
            put("/api/v1/products/{id}", sampleProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Product updated successfully"))
            .andExpect(jsonPath("$.data.name").value("Updated Product Name"))
            .andExpect(jsonPath("$.data.price").value(149.99))
    }

    @Test
    fun `delete product should return 204`() {
        mockMvc.perform(delete("/api/v1/products/{id}", sampleProductId))
            .andExpect(status().isNoContent)
    }
}

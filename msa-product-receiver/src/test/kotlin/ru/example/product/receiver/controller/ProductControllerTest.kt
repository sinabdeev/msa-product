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
import ru.example.product.receiver.dto.request.CreateProductsBatchRequest
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
    fun `batch create products should return 201`() {
        val request1 =
            CreateProductRequest(
                sku = "TEST-SKU-001",
                name = "Test Product 1",
                description = "Test Description 1",
                price = BigDecimal("99.99"),
                quantity = 10,
                weight = 1.5,
                isAvailable = true,
                category = "ELECTRONICS",
                tags = listOf("test", "electronics"),
            )
        val request2 =
            CreateProductRequest(
                sku = "TEST-SKU-002",
                name = "Test Product 2",
                description = "Test Description 2",
                price = BigDecimal("149.99"),
                quantity = 5,
                weight = 2.0,
                isAvailable = true,
                category = "CLOTHING",
                tags = listOf("clothing"),
            )

        val product1 = sampleProduct.copy(id = UUID.randomUUID(), sku = "TEST-SKU-001", name = "Test Product 1")
        val product2 = sampleProduct.copy(id = UUID.randomUUID(), sku = "TEST-SKU-002", name = "Test Product 2")

        val batchRequest = CreateProductsBatchRequest(listOf(request1, request2))

        `when`(productService.createProductsBatch(listOf(request1, request2))).thenReturn(listOf(product1, product2))

        mockMvc.perform(
            post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Products created successfully"))
            .andExpect(jsonPath("$.data[0].sku").value("TEST-SKU-001"))
            .andExpect(jsonPath("$.data[1].sku").value("TEST-SKU-002"))
    }

    @Test
    fun `batch create with empty array should return 400`() {
        val batchRequest = CreateProductsBatchRequest(emptyList())

        mockMvc.perform(
            post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `batch create with too many products should return 400`() {
        val manyRequests =
            List(101) { index ->
                CreateProductRequest(
                    sku = "TEST-SKU-$index",
                    name = "Test Product $index",
                    description = "Test Description $index",
                    price = BigDecimal("99.99"),
                    quantity = 10,
                    weight = 1.5,
                    isAvailable = true,
                    category = "ELECTRONICS",
                    tags = emptyList(),
                )
            }
        val batchRequest = CreateProductsBatchRequest(manyRequests)

        mockMvc.perform(
            post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `batch create with duplicate SKUs should return 400`() {
        val request1 =
            CreateProductRequest(
                sku = "TEST-SKU-001",
                name = "Test Product 1",
                description = "Test Description 1",
                price = BigDecimal("99.99"),
                quantity = 10,
                weight = 1.5,
                isAvailable = true,
                category = "ELECTRONICS",
                tags = emptyList(),
            )
        val request2 =
            CreateProductRequest(
                sku = "TEST-SKU-001", // Duplicate SKU
                name = "Test Product 2",
                description = "Test Description 2",
                price = BigDecimal("149.99"),
                quantity = 5,
                weight = 2.0,
                isAvailable = true,
                category = "CLOTHING",
                tags = emptyList(),
            )

        val batchRequest = CreateProductsBatchRequest(listOf(request1, request2))

        `when`(productService.createProductsBatch(listOf(request1, request2)))
            .thenThrow(IllegalArgumentException("Duplicate SKUs in batch: TEST-SKU-001"))

        mockMvc.perform(
            post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `batch create with SKU conflict should return 400`() {
        val request1 =
            CreateProductRequest(
                sku = "TEST-SKU-001",
                name = "Test Product 1",
                description = "Test Description 1",
                price = BigDecimal("99.99"),
                quantity = 10,
                weight = 1.5,
                isAvailable = true,
                category = "ELECTRONICS",
                tags = emptyList(),
            )
        val request2 =
            CreateProductRequest(
                sku = "TEST-SKU-002",
                name = "Test Product 2",
                description = "Test Description 2",
                price = BigDecimal("149.99"),
                quantity = 5,
                weight = 2.0,
                isAvailable = true,
                category = "CLOTHING",
                tags = emptyList(),
            )

        val batchRequest = CreateProductsBatchRequest(listOf(request1, request2))

        `when`(productService.createProductsBatch(listOf(request1, request2)))
            .thenThrow(IllegalArgumentException("SKU already exists: TEST-SKU-001"))

        mockMvc.perform(
            post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `delete product should return 204`() {
        mockMvc.perform(delete("/api/v1/products/{id}", sampleProductId))
            .andExpect(status().isNoContent)
    }
}

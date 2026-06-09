package ru.example.product.data.controller

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.bean.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.service.ProductStatusHistoryService
import java.time.Instant
import java.util.UUID

@WebMvcTest(StatusHistoryController::class)
class StatusHistoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var productStatusHistoryService: ProductStatusHistoryService

    @Test
    fun `getStatusHistory should return list of records with default limit`() {
        val mockDto = ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "DRAFT",
            toStatus = "PENDING_REVIEW",
            reason = "Test reason",
            userId = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
            processingDurationSeconds = 123L,
        )

        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(listOf(mockDto))

        val result = mockMvc.perform(get("/api/v1/status-history"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].id").isNotEmpty)
            .andExpect(jsonPath("$.data[0].product_id").isNotEmpty)
            .andExpect(jsonPath("$.data[0].from_status").value("DRAFT"))
            .andExpect(jsonPath("$.data[0].to_status").value("PENDING_REVIEW"))
            .andExpect(jsonPath("$.data[0].reason").value("Test reason"))
            .andExpect(jsonPath("$.data[0].created_at").isNotEmpty)
            .andReturn()

        assertNotNull(result.response.contentAsString)
    }

    @Test
    fun `getStatusHistory should return list of records with created_after parameter`() {
        val mockDto = ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "ACTIVE",
            toStatus = "ARCHIVED",
            reason = null,
            userId = null,
            createdAt = Instant.now(),
            processingDurationSeconds = null,
        )

        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(listOf(mockDto))

        val timestamp = Instant.now().toString()

        mockMvc.perform(
            get("/api/v1/status-history")
                .param("limit", "50")
                .param("created_after", timestamp)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
    }

    @Test
    fun `getStatusHistory should return list of records with productId filter`() {
        val mockDto = ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "DRAFT",
            toStatus = "APPROVED",
            reason = null,
            userId = null,
            createdAt = Instant.now(),
            processingDurationSeconds = null,
        )

        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(listOf(mockDto))

        val productId = UUID.randomUUID().toString()

        mockMvc.perform(
            get("/api/v1/status-history")
                .param("product_id", productId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
    }

    @Test
    fun `getStatusHistory should return list of records with toStatus filter`() {
        val mockDto = ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "DRAFT",
            toStatus = "ACTIVE",
            reason = null,
            userId = null,
            createdAt = Instant.now(),
            processingDurationSeconds = null,
        )

        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(listOf(mockDto))

        mockMvc.perform(
            get("/api/v1/status-history")
                .param("to_status", "ACTIVE")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
    }

    @Test
    fun `getStatusHistory should return list of records with fromStatus filter`() {
        val mockDto = ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "DRAFT",
            toStatus = "PENDING_REVIEW",
            reason = null,
            userId = null,
            createdAt = Instant.now(),
            processingDurationSeconds = null,
        )

        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(listOf(mockDto))

        mockMvc.perform(
            get("/api/v1/status-history")
                .param("from_status", "DRAFT")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
    }

    @Test
    fun `getStatusHistory should return empty list when no records found`() {
        whenever(productStatusHistoryService.getStatusHistory(any())).thenReturn(emptyList())

        mockMvc.perform(get("/api/v1/status-history"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(0))
    }

    @Test
    fun `getStatusHistory should return 400 when created_after format is invalid`() {
        assertThrows<Exception> {
            mockMvc.perform(
                get("/api/v1/status-history")
                    .param("created_after", "invalid-timestamp")
            )
                .andReturn()
        }
    }

    @Test
    fun `getStatusHistory should return 400 when limit is less than 1`() {
        assertThrows<Exception> {
            mockMvc.perform(
                get("/api/v1/status-history")
                    .param("limit", "0")
            )
                .andReturn()
        }
    }

    @Test
    fun `getStatusHistory should return 400 when limit is greater than 10000`() {
        assertThrows<Exception> {
            mockMvc.perform(
                get("/api/v1/status-history")
                    .param("limit", "10001")
            )
                .andReturn()
        }
    }
}

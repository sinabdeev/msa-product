package ru.example.product.generator.client

import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.example.product.generator.dto.request.CreateProductsBatchRequest
import ru.example.product.generator.dto.response.ApiResponse
import ru.example.product.generator.domain.ProductDto
import java.net.URI
import java.util.Optional

/**
 * REST client for batch product submission using RestTemplate.
 * Handles communication with the /api/v1/products/batch endpoint.
 */
@Component
class ProductBatchClient(
    private val restTemplate: org.springframework.web.client.RestTemplate,
) {

    private companion object {
        private val log = LoggerFactory.getLogger(ProductBatchClient::class.java)
    }

    /**
     * Submit a batch of products to the API.
     *
     * @param products List of products to create
     * @return Optional of successful ApiResponse, empty if submission failed
     */
    fun submitBatch(products: List<ru.example.product.generator.dto.request.CreateProductRequest>): Optional<ApiResponse<List<ProductDto>>> {
        return try {
            val request = CreateProductsBatchRequest(products)
            val entity = HttpEntity(request)
            val url = apiBaseUrl

            val responseEntity: org.springframework.http.ResponseEntity<ApiResponse<List<ProductDto>>> =
                restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.POST,
                    entity,
                    object : org.springframework.core.ParameterizedTypeReference<ApiResponse<List<ProductDto>>>() {}
                )

            val body = responseEntity.body
            val statusCode = responseEntity.statusCode

            if (statusCode == HttpStatus.CREATED && body != null) {
                log.info("Successfully submitted batch of ${products.size} products. Response: ${body.message}")
                Optional.of(body)
            } else {
                log.warn("Batch submission returned non-success status: $statusCode")
                Optional.empty()
            }
        } catch (e: org.springframework.web.client.ResourceAccessException) {
            log.error("Network error while submitting batch of ${products.size} products: ${e.message}")
            Optional.empty()
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            log.error("Client error while submitting batch of ${products.size} products: ${e.message}")
            Optional.empty()
        } catch (e: org.springframework.web.client.HttpServerErrorException) {
            log.error("Server error while submitting batch of ${products.size} products: ${e.message}")
            Optional.empty()
        } catch (e: Exception) {
            log.error("Unexpected error while submitting batch of ${products.size} products: ${e.message}")
            Optional.empty()
        }
    }

    /**
     * Get the API base URL from environment or use default.
     */
    private val apiBaseUrl: String
        get() = System.getenv("API_BASE_URL") ?: "http://localhost:8080/api/v1/products/batch"
}

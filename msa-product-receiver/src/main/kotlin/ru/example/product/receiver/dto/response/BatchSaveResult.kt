package ru.example.product.receiver.dto.response

import ru.example.product.receiver.domain.ProductDto

/**
 * Result of saving a single product in batch operation.
 *
 * @param T Type of the data (usually ProductDto)
 * @property success Whether the save operation was successful
 * @property data The saved product data if successful
 * @property error Error message if save failed
 * @property index Original index of the product in the batch request
 */
data class BatchSaveResult<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val index: Int? = null,
)

/**
 * Summary of batch save operation.
 *
 * @property total Total number of products in batch
 * @property successful Number of successfully saved products
 * @property failed Number of failed saves
 * @property results Detailed results for each product
 */
data class BatchSaveSummary(
    val total: Int,
    val successful: Int,
    val failed: Int,
    val results: List<BatchSaveResult<ProductDto>>,
) {
    companion object {
        /**
         * Creates a BatchSaveSummary from a list of BatchSaveResult.
         */
        fun fromResults(results: List<BatchSaveResult<ProductDto>>): BatchSaveSummary {
            val total = results.size
            val successful = results.count { it.success }
            val failed = total - successful
            return BatchSaveSummary(total, successful, failed, results)
        }
    }
}

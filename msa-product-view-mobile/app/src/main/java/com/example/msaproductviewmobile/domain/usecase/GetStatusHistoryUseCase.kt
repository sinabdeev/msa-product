package com.example.msaproductviewmobile.domain.usecase

import com.example.msaproductviewmobile.domain.repository.StatusHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting status history for a product.
 */
class GetStatusHistoryUseCase @Inject constructor(
    private val statusHistoryRepository: StatusHistoryRepository
) {

    operator fun invoke(productId: String): Flow<List<com.example.msaproductviewmobile.domain.model.StatusHistoryEntry>> {
        return statusHistoryRepository.getStatusHistory(productId)
    }
}

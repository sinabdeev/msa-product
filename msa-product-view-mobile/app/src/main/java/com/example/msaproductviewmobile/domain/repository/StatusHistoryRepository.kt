package com.example.msaproductviewmobile.domain.repository

import com.example.msaproductviewmobile.domain.model.StatusHistoryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for status history data.
 */
interface StatusHistoryRepository {

    fun getStatusHistory(productId: String): Flow<List<StatusHistoryEntry>>

    suspend fun refreshStatusHistory(productId: String)
}

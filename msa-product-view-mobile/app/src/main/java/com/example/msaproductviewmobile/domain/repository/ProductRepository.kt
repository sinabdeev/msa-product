package com.example.msaproductviewmobile.domain.repository

import com.example.msaproductviewmobile.domain.model.Product
import com.example.msaproductviewmobile.domain.model.StatusHistoryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product data.
 */
interface ProductRepository {

    fun getProducts(): Flow<List<Product>>

    fun getProductById(id: String): Flow<Product?>

    suspend fun refreshProducts()
}

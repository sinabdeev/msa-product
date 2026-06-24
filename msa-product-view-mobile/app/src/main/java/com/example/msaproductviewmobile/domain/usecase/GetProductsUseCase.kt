package com.example.msaproductviewmobile.domain.usecase

import com.example.msaproductviewmobile.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the list of products.
 */
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(): Flow<List<com.example.msaproductviewmobile.domain.model.Product>> {
        return productRepository.getProducts()
    }
}

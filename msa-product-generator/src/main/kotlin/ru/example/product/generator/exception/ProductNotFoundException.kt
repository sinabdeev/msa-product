package ru.example.product.generator.exception

import java.util.*

/**
 * Exception thrown when a product is not found.
 */
class ProductNotFoundException(id: UUID) : RuntimeException("Product with ID $id not found")

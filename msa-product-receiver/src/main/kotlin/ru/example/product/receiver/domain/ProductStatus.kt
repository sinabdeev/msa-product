package ru.example.product.receiver.domain

/**
 * Product status enumeration with numeric codes.
 * Represents the lifecycle of a product from draft to shipped.
 */
enum class ProductStatus(val code: Int) {
    DRAFT(0),
    PENDING_REVIEW(10),
    REVIEWED(20),
    APPROVED(30),
    REJECTED(40),
    ACTIVE(50),
    PROCESSED(60),
    SHIPPED(70),
    ARCHIVED(80);

    companion object {
        private val CODE_MAP = values().associateBy(ProductStatus::code)
        private val NAME_MAP = values().associateBy(ProductStatus::name)

        fun fromCode(code: Int): ProductStatus? = CODE_MAP[code]

        fun fromName(name: String): ProductStatus? = NAME_MAP[name.uppercase()]
    }
}

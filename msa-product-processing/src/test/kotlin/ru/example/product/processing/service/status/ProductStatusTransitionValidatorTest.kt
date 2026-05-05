package ru.example.product.processing.service.status

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.example.product.processing.domain.ProductStatus

class ProductStatusTransitionValidatorTest {
    private val validator = ProductStatusTransitionValidatorImpl()

    @Test
    fun `should allow draft to pending review`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.PENDING_REVIEW))
    }

    @Test
    fun `should allow draft to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject draft to reviewed`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.REVIEWED))
    }

    @Test
    fun `should allow pending review to reviewed`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.PENDING_REVIEW, ProductStatus.REVIEWED))
    }

    @Test
    fun `should allow pending review to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.PENDING_REVIEW, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should allow pending review to draft`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.PENDING_REVIEW, ProductStatus.DRAFT))
    }

    @Test
    fun `should reject pending review to active`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.PENDING_REVIEW, ProductStatus.ACTIVE))
    }

    @Test
    fun `should allow reviewed to approved`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.REVIEWED, ProductStatus.APPROVED))
    }

    @Test
    fun `should allow reviewed to rejected`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.REVIEWED, ProductStatus.REJECTED))
    }

    @Test
    fun `should reject reviewed to draft`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.REVIEWED, ProductStatus.DRAFT))
    }

    @Test
    fun `should allow approved to active`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.APPROVED, ProductStatus.ACTIVE))
    }

    @Test
    fun `should allow approved to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.APPROVED, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject approved to rejected`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.APPROVED, ProductStatus.REJECTED))
    }

    @Test
    fun `should allow rejected to pending review`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.REJECTED, ProductStatus.PENDING_REVIEW))
    }

    @Test
    fun `should allow rejected to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.REJECTED, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject rejected to active`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.REJECTED, ProductStatus.ACTIVE))
    }

    @Test
    fun `should allow active to processed`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.ACTIVE, ProductStatus.PROCESSED))
    }

    @Test
    fun `should allow active to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.ACTIVE, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject active to draft`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.ACTIVE, ProductStatus.DRAFT))
    }

    @Test
    fun `should allow processed to shipped`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.PROCESSED, ProductStatus.SHIPPED))
    }

    @Test
    fun `should allow processed to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.PROCESSED, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject processed to active`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.PROCESSED, ProductStatus.ACTIVE))
    }

    @Test
    fun `should allow shipped to archived`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.SHIPPED, ProductStatus.ARCHIVED))
    }

    @Test
    fun `should reject shipped to processed`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.SHIPPED, ProductStatus.PROCESSED))
    }

    @Test
    fun `should allow archived to active`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.ARCHIVED, ProductStatus.ACTIVE))
    }

    @Test
    fun `should reject archived to shipped`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.ARCHIVED, ProductStatus.SHIPPED))
    }

    @Test
    fun `should allow transition to same status`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.DRAFT))
        assertTrue(validator.isTransitionAllowed(ProductStatus.ACTIVE, ProductStatus.ACTIVE))
    }

    @Test
    fun `should return correct allowed transitions for draft`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.DRAFT)
        assertEquals(setOf(ProductStatus.PENDING_REVIEW, ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for pending review`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.PENDING_REVIEW)
        assertEquals(
            setOf(ProductStatus.REVIEWED, ProductStatus.ARCHIVED, ProductStatus.DRAFT),
            allowed,
        )
    }

    @Test
    fun `should return correct allowed transitions for reviewed`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.REVIEWED)
        assertEquals(setOf(ProductStatus.APPROVED, ProductStatus.REJECTED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for approved`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.APPROVED)
        assertEquals(setOf(ProductStatus.ACTIVE, ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for rejected`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.REJECTED)
        assertEquals(setOf(ProductStatus.PENDING_REVIEW, ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for active`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.ACTIVE)
        assertEquals(setOf(ProductStatus.PROCESSED, ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for processed`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.PROCESSED)
        assertEquals(setOf(ProductStatus.SHIPPED, ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for shipped`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.SHIPPED)
        assertEquals(setOf(ProductStatus.ARCHIVED), allowed)
    }

    @Test
    fun `should return correct allowed transitions for archived`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.ARCHIVED)
        assertEquals(setOf(ProductStatus.ACTIVE), allowed)
    }
}

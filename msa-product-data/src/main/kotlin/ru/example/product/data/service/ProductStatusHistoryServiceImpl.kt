package ru.example.product.data.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest
import ru.example.product.data.mappers.ProductStatusHistoryMapper
import ru.example.product.data.repository.ProductStatusHistoryRepository
import java.util.*

/**
 * Implementation of ProductStatusHistoryService.
 * Handles business logic for status history queries.
 */
@Service
class ProductStatusHistoryServiceImpl(
    private val productStatusHistoryRepository: ProductStatusHistoryRepository,
    private val productStatusHistoryMapper: ProductStatusHistoryMapper,
) : ProductStatusHistoryService {
    private val logger: Logger = LoggerFactory.getLogger(ProductStatusHistoryServiceImpl::class.java)

    override fun getStatusHistory(query: StatusHistoryQueryRequest): List<ProductStatusHistoryDto> {
        logger.info(
            "Fetching status history: limit={}, createdAfter={}, productId={}, toStatus={}, fromStatus={}",
            query.limit,
            query.createdAfter,
            query.productId,
            query.toStatus,
            query.fromStatus,
        )

        val entities =
            when {
                query.productId != null && query.toStatus != null && query.fromStatus != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByProductIdAndFromStatusAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.fromStatus,
                        query.toStatus,
                        query.createdAfter,
                        query.limit,
                    )
                query.productId != null && query.toStatus != null && query.fromStatus != null ->
                    productStatusHistoryRepository.findByProductIdAndFromStatusAndToStatusOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.fromStatus,
                        query.toStatus,
                        query.limit,
                    )
                query.productId != null && query.toStatus != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByProductIdAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.toStatus,
                        query.createdAfter,
                        query.limit,
                    )
                query.productId != null && query.fromStatus != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByProductIdAndFromStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.fromStatus,
                        query.createdAfter,
                        query.limit,
                    )
                query.productId != null && query.toStatus != null ->
                    productStatusHistoryRepository.findByProductIdAndToStatusOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.toStatus,
                        query.limit,
                    )
                query.productId != null && query.fromStatus != null ->
                    productStatusHistoryRepository.findByProductIdAndFromStatusOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.fromStatus,
                        query.limit,
                    )
                query.productId != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.createdAfter,
                        query.limit,
                    )
                query.toStatus != null && query.fromStatus != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByFromStatusAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                        query.fromStatus,
                        query.toStatus,
                        query.createdAfter,
                        query.limit,
                    )
                query.toStatus != null && query.fromStatus != null ->
                    productStatusHistoryRepository.findByFromStatusAndToStatusOrderByCreatedAtDesc(
                        query.fromStatus,
                        query.toStatus,
                        query.limit,
                    )
                query.toStatus != null && query.createdAfter != null ->
                    productStatusHistoryRepository.findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
                        query.createdAfter,
                        query.toStatus,
                        query.limit,
                    )
                query.productId != null ->
                    productStatusHistoryRepository.findByProductIdOrderByCreatedAtDesc(
                        UUID.fromString(query.productId),
                        query.limit,
                    )
                query.createdAfter != null ->
                    productStatusHistoryRepository.findByCreatedAtAfterOrderByCreatedAtDesc(
                        query.createdAfter,
                        query.limit,
                    )
                query.toStatus != null ->
                    productStatusHistoryRepository.findByToStatusOrderByCreatedAtDesc(
                        query.toStatus,
                        query.limit,
                    )
                query.fromStatus != null ->
                    productStatusHistoryRepository.findByFromStatusOrderByCreatedAtDesc(
                        query.fromStatus,
                        query.limit,
                    )
                else ->
                    productStatusHistoryRepository.findTopByOrderByCreatedAtDesc(query.limit)
            }

        logger.info("Found {} status history records", entities.size)
        return productStatusHistoryMapper.toDto(entities)
    }
}

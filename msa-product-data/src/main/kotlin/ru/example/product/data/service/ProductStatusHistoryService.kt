package ru.example.product.data.service

import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest

/**
 * Service interface for product status history operations.
 */
interface ProductStatusHistoryService {

    /**
     * Получить записи истории статусов с фильтрацией.
     *
     * @param query Параметры запроса
     * @return Список DTO записей истории
     */
    fun getStatusHistory(query: StatusHistoryQueryRequest): List<ProductStatusHistoryDto>
}

package com.example.msaproductviewmobile.domain.model

/**
 * Domain model representing a status history entry.
 */
data class StatusHistoryEntry(
    val id: String,
    val productId: String,
    val status: ProductStatus,
    val timestamp: Long,
    val message: String? = null
)

/**
 * Domain model representing a chart data point.
 */
data class ChartDataPoint(
    val label: String,
    val value: Double
)

/**
 * Domain model representing a chart series.
 */
data class ChartSeries(
    val name: String,
    val data: List<ChartDataPoint>
)

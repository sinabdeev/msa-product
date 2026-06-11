// Модель записи из API (camelCase, соответствует ProductStatusHistoryDto из openapi.yaml)
export interface StatusHistoryRecord {
  id: string;
  productId: string;
  fromStatus: string;
  toStatus: string;
  reason: string | null;
  userId: string | null;
  createdAt: string;                        // ISO 8601 с часовым поясом, например "2025-06-11T19:10:57.132499Z"
  processingDurationSeconds: number | null; // int64 в API
}

// Обёртка ответа API (соответствует ApiResponseListProductStatusHistoryDto из openapi.yaml)
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string; // ISO 8601
}

// Агрегированные данные для BarChart
export interface BarChartData {
  name: string;
  value: number;
}

// Агрегированные данные для PieChart
export interface PieChartData {
  name: string;
  value: number;
  fill: string;
}

// Все агрегированные данные для Dashboard
export interface AggregatedData {
  statusBar: BarChartData[];
  avgProcessingTime: BarChartData[];
  topProducts: BarChartData[];
  reasonsBar: BarChartData[];
  userActivity: BarChartData[];
  statusPie: PieChartData[];
  reasonsPie: PieChartData[];
  fromStatusPie: PieChartData[];
  hourlyActivity: PieChartData[];
}

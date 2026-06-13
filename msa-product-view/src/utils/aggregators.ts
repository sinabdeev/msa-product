import type { StatusHistoryRecord, BarChartData, PieChartData, AggregatedData } from '../types/data';
import { logger } from './logger';

// Цветовая палитра
const COLORS = [
  '#8884d8', '#82ca9d', '#ffc658', '#ff7300',
  '#a4de6c', '#d0ed57', '#ffc0cb', '#8dd1e1',
  '#a4a4f5', '#e57373', '#64b5f6', '#81c784'
];

// 1. Переходы по статусам: GROUP BY toStatus → COUNT
export function aggregateByToStatus(records: StatusHistoryRecord[]): BarChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    counts.set(record.toStatus, (counts.get(record.toStatus) || 0) + 1);
  }
  
  return Array.from(counts.entries())
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => b.value - a.value);
}

// 2. Среднее время обработки: GROUP BY toStatus → AVG(processingDurationSeconds)
export function aggregateAvgProcessingTime(records: StatusHistoryRecord[]): BarChartData[] {
  const sums = new Map<string, { total: number; count: number }>();
  
  for (const record of records) {
    if (record.processingDurationSeconds !== null) {
      const current = sums.get(record.toStatus) || { total: 0, count: 0 };
      current.total += record.processingDurationSeconds;
      current.count += 1;
      sums.set(record.toStatus, current);
    }
  }
  
  return Array.from(sums.entries())
    .map(([name, { total, count }]) => ({ 
      name, 
      value: Math.round(total / count) 
    }))
    .sort((a, b) => b.value - a.value);
}

// 3. Топ продуктов: GROUP BY productId → COUNT, топ-10
export function aggregateTopProducts(records: StatusHistoryRecord[]): BarChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    counts.set(record.productId, (counts.get(record.productId) || 0) + 1);
  }
  
  return Array.from(counts.entries())
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 10);
}

// 4. Причины переходов: GROUP BY reason → COUNT
export function aggregateReasons(records: StatusHistoryRecord[]): BarChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    if (record.reason !== null) {
      counts.set(record.reason, (counts.get(record.reason) || 0) + 1);
    }
  }
  
  return Array.from(counts.entries())
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => b.value - a.value);
}

// 5. Активность пользователей: GROUP BY userId → COUNT, топ-10
export function aggregateUserActivity(records: StatusHistoryRecord[]): BarChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    if (record.userId !== null) {
      counts.set(record.userId, (counts.get(record.userId) || 0) + 1);
    }
  }
  
  return Array.from(counts.entries())
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 10);
}

// 6. Доля статусов: GROUP BY toStatus → COUNT
/**
 * Агрегирует записи истории статусов для построения круговой диаграммы (pie chart),
 * отображающей долю каждого целевого статуса (`toStatus`) в общем количестве переходов.
 *
 * **Логика работы:**
 * 1. Выполняется группировка записей по полю `toStatus` с подсчётом количества
 *    переходов в каждый статус (аналог SQL-запроса `GROUP BY toStatus → COUNT`).
 * 2. Для каждой группы формируется объект {@link PieChartData}, содержащий:
 *    - `name` — название статуса;
 *    - `value` — количество переходов в данный статус;
 *    - `fill` — цвет сектора диаграммы, назначаемый циклически из палитры {@link COLORS}.
 * 3. Результирующий массив сортируется по убыванию `value`, чтобы наиболее
 *    частые статусы отображались первыми.
 *
 * @param records — массив записей истории статусов ({@link StatusHistoryRecord}).
 *                  Каждая запись должна содержать поле `toStatus`.
 *                  Пустой массив допустим — в этом случае будет возвращён пустой результат.
 * @returns Массив объектов {@link PieChartData}, готовый для передачи в компонент
 *          круговой диаграммы (например, `StatusPieChart`). Если `records` пуст,
 *          возвращается пустой массив.
 *
 * @example
 * ```ts
 * const records: StatusHistoryRecord[] = [
 *   { toStatus: 'ACTIVE', ... },
 *   { toStatus: 'ACTIVE', ... },
 *   { toStatus: 'INACTIVE', ... },
 * ];
 * const pieData = aggregateStatusPie(records);
 * // Результат: [
 * //   { name: 'ACTIVE', value: 2, fill: '#8884d8' },
 * //   { name: 'INACTIVE', value: 1, fill: '#82ca9d' },
 * // ]
 * ```
 */
export function aggregateStatusPie(records: StatusHistoryRecord[]): PieChartData[] {
  const counts = new Map<string, number>();
  
  // 1. Собираем количество вхождений
  for (const record of records) {
    counts.set(record.toStatus, (counts.get(record.toStatus) || 0) + 1);
  }
  
  // 2. Превращаем в массив, СНАЧАЛА сортируем, а ПОТОМ красим
  return Array.from(counts.entries())
    .sort((a, b) => b[1] - a[1]) // Сортировка по значению count (индекс 1 в [key, value])
    .map(([name, value], index) => ({ 
      name, 
      value, 
      fill: COLORS[index % COLORS.length] 
    }));
}


// 7. Доля причин (donut): GROUP BY reason → COUNT
export function aggregateReasonsPie(records: StatusHistoryRecord[]): PieChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    if (record.reason !== null) {
      counts.set(record.reason, (counts.get(record.reason) || 0) + 1);
    }
  }
  
  return Array.from(counts.entries())
    .map(([name, value], index) => ({ 
      name, 
      value, 
      fill: COLORS[index % COLORS.length] 
    }))
    .sort((a, b) => b.value - a.value);
}

// 8. Откуда переходят: GROUP BY fromStatus → COUNT
export function aggregateFromStatusPie(records: StatusHistoryRecord[]): PieChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    counts.set(record.fromStatus, (counts.get(record.fromStatus) || 0) + 1);
  }
  
  return Array.from(counts.entries())
    .map(([name, value], index) => ({ 
      name, 
      value, 
      fill: COLORS[index % COLORS.length] 
    }))
    .sort((a, b) => b.value - a.value);
}

// 9. Активность по часам: GROUP BY час из createdAt → COUNT
export function aggregateHourlyActivity(records: StatusHistoryRecord[]): PieChartData[] {
  const counts = new Map<string, number>();
  
  for (const record of records) {
    const date = new Date(record.createdAt);
    const hour = date.getHours();
    const hourLabel = `${String(hour).padStart(2, '0')}:00`;
    counts.set(hourLabel, (counts.get(hourLabel) || 0) + 1);
  }
  
  return Array.from(counts.entries())
    .map(([name, value], index) => ({ 
      name, 
      value, 
      fill: COLORS[index % COLORS.length] 
    }))
    .sort((a, b) => a.name.localeCompare(b.name));
}

// Главная функция агрегации всех данных
export function aggregateAll(records: StatusHistoryRecord[]): AggregatedData {
  const startTime = Date.now();
  logger.debug('Aggregators', 'AGGREGATION_START', { recordsCount: records.length });

  if (records.length === 0) {
    logger.warn('Aggregators', 'AGGREGATION_SKIP', { reason: 'no records' });
    return {
      statusBar: [],
      avgProcessingTime: [],
      topProducts: [],
      reasonsBar: [],
      userActivity: [],
      statusPie: [],
      reasonsPie: [],
      fromStatusPie: [],
      hourlyActivity: []
    };
  }

  const data: AggregatedData = {
    statusBar: aggregateByToStatus(records),
    avgProcessingTime: aggregateAvgProcessingTime(records),
    topProducts: aggregateTopProducts(records),
    reasonsBar: aggregateReasons(records),
    userActivity: aggregateUserActivity(records),
    statusPie: aggregateStatusPie(records),
    reasonsPie: aggregateReasonsPie(records),
    fromStatusPie: aggregateFromStatusPie(records),
    hourlyActivity: aggregateHourlyActivity(records)
  };

  const durationMs = Date.now() - startTime;
  const chartsUpdated = Object.entries(data)
    .filter(([_, value]) => value.length > 0)
    .map(([key, _]) => key);

  logger.success('Aggregators', 'AGGREGATION_COMPLETE', { 
    durationMs, 
    chartsUpdated: chartsUpdated.length 
  });

  return data;
}

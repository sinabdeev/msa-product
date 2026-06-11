import type { StatusHistoryRecord } from '../types/data';
import type { DataService } from './types';
import { logger } from '../utils/logger';

// Статусы (9 шт., как в реальных данных)
const STATUSES = [
  'DRAFT', 'PENDING_REVIEW', 'REVIEWED', 'APPROVED', 'REJECTED',
  'ARCHIVED', 'ACTIVE', 'PROCESSED', 'SHIPPED'
];

// reason всегда один (как в реальных данных)
const REASON = 'Batch processing';

// 20 разных UUID для productId
const PRODUCT_IDS = Array.from({ length: 20 }, (_, i) => 
  `00000000-0000-0000-0000-${String(i + 1).padStart(12, '0')}`
);

// 10 разных UUID для userId
const USER_IDS = Array.from({ length: 10 }, (_, i) => 
  `11111111-1111-1111-1111-${String(i + 1).padStart(12, '0')}`
);

// Генерация случайного UUID
function randomUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// Генерация случайной записи
function generateRecord(createdAt?: Date): StatusHistoryRecord {
  const now = createdAt || new Date();
  const hasProcessingDuration = Math.random() < 0.3; // 30% записей
  const hasUserId = Math.random() > 0.1; // 90% записей (10% null)

  return {
    id: randomUUID(),
    productId: PRODUCT_IDS[Math.floor(Math.random() * PRODUCT_IDS.length)],
    fromStatus: STATUSES[Math.floor(Math.random() * STATUSES.length)],
    toStatus: STATUSES[Math.floor(Math.random() * STATUSES.length)],
    reason: REASON,
    userId: hasUserId ? USER_IDS[Math.floor(Math.random() * USER_IDS.length)] : null,
    createdAt: now.toISOString(),
    processingDurationSeconds: hasProcessingDuration 
      ? Math.floor(Math.random() * 3_000_000) + 1 
      : null
  };
}

export class MockDataService implements DataService {
  private records: StatusHistoryRecord[] = [];

  async fetchRecords(): Promise<StatusHistoryRecord[]> {
    const startTime = Date.now();
    logger.debug('DataService', 'FETCH_START', { mode: 'initial' });

    // Генерируем начальный пул из 87 записей за последние 24 часа
    const now = Date.now();
    const records: StatusHistoryRecord[] = [];
    
    for (let i = 0; i < 87; i++) {
      const randomTime = now - Math.floor(Math.random() * 24 * 60 * 60 * 1000);
      records.push(generateRecord(new Date(randomTime)));
    }

    this.records = records;
    const durationMs = Date.now() - startTime;
    const estimatedSize = `~${Math.round(JSON.stringify(records).length / 1024)}KB`;

    logger.success('DataService', 'FETCH_SUCCESS', { 
      recordsCount: records.length, 
      estimatedSize, 
      durationMs 
    });
    logger.debug('DataService', 'MOCK_GENERATE', { 
      generatedCount: records.length, 
      totalPoolSize: this.records.length 
    });

    return records;
  }

  async fetchNewRecords(_createdAfter: string): Promise<StatusHistoryRecord[]> {
    const startTime = Date.now();
    logger.debug('DataService', 'FETCH_START', { mode: 'polling' });

    // Генерируем 1-5 новых записей с текущим временем
    const count = Math.floor(Math.random() * 5) + 1;
    const newRecords: StatusHistoryRecord[] = [];
    
    for (let i = 0; i < count; i++) {
      newRecords.push(generateRecord(new Date()));
    }

    this.records.push(...newRecords);
    const durationMs = Date.now() - startTime;
    const estimatedSize = `~${Math.round(JSON.stringify(newRecords).length / 1024)}KB`;

    logger.success('DataService', 'FETCH_SUCCESS', { 
      recordsCount: newRecords.length, 
      estimatedSize, 
      durationMs 
    });
    logger.debug('DataService', 'MOCK_GENERATE', { 
      generatedCount: newRecords.length, 
      totalPoolSize: this.records.length 
    });

    return newRecords;
  }
}

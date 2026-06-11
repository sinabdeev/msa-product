import type { StatusHistoryRecord } from '../types/data';

// Интерфейс слоя данных
export interface DataService {
  fetchRecords(): Promise<StatusHistoryRecord[]>;
  fetchNewRecords(createdAfter: string): Promise<StatusHistoryRecord[]>;
}

// Класс ошибки API
export class ApiError extends Error {
  constructor(
    public statusCode: number,
    public statusText: string,
    public errorMessage?: string
  ) {
    super(`API Error ${statusCode}: ${statusText}${errorMessage ? ` - ${errorMessage}` : ''}`);
    this.name = 'ApiError';
  }
}

import type { StatusHistoryRecord, ApiResponse } from '../types/data';
import type { DataService } from './types';
import { ApiError } from './types';
import { logger } from '../utils/logger';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Retry-логика с экспоненциальной задержкой
async function fetchWithRetry(
  url: string,
  maxRetries: number = 3,
  isClientError: (status: number) => boolean = (s) => s >= 400 && s < 500
): Promise<Response> {
  let lastError: Error | null = null;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      const response = await fetch(url);
      
      // Если ошибка клиента (4xx) - не повторяем
      if (isClientError(response.status)) {
        return response;
      }
      
      // Если успех или ошибка сервера (5xx) - возвращаем
      if (response.ok || response.status >= 500) {
        return response;
      }
      
      return response;
    } catch (error) {
      lastError = error as Error;
      
      // Если это последняя попытка - выбрасываем ошибку
      if (attempt === maxRetries) {
        throw lastError;
      }
      
      // Экспоненциальная задержка: 1s, 2s, 4s
      const delay = Math.pow(2, attempt) * 1000;
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw lastError || new Error('Unknown error');
}

export class ApiDataService implements DataService {
  async fetchRecords(): Promise<StatusHistoryRecord[]> {
    const startTime = Date.now();
    logger.debug('DataService', 'FETCH_START', { mode: 'initial' });

    try {
      const response = await fetchWithRetry(`${BASE_URL}/api/v1/status-history?limit=1000`, 3);
      
      if (!response.ok) {
        const error = new ApiError(response.status, response.statusText);
        logger.error('DataService', 'FETCH_ERROR', { 
          errorMessage: error.message, 
          statusCode: response.status 
        });
        throw error;
      }

      const apiResponse: ApiResponse<StatusHistoryRecord[]> = await response.json();
      
      if (!apiResponse.success) {
        const error = new ApiError(400, 'Bad Request', apiResponse.message);
        logger.error('DataService', 'FETCH_ERROR', { 
          errorMessage: error.message, 
          statusCode: 400 
        });
        throw error;
      }

      const durationMs = Date.now() - startTime;
      const estimatedSize = `~${Math.round(JSON.stringify(apiResponse.data).length / 1024)}KB`;

      logger.success('DataService', 'FETCH_SUCCESS', { 
        recordsCount: apiResponse.data.length, 
        estimatedSize, 
        durationMs 
      });

      return apiResponse.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      
      const networkError = error as Error;
      logger.error('DataService', 'FETCH_ERROR', { 
        errorMessage: networkError.message 
      });
      throw new ApiError(0, 'Network Error', networkError.message);
    }
  }

  async fetchNewRecords(createdAfter: string): Promise<StatusHistoryRecord[]> {
    const startTime = Date.now();
    logger.debug('DataService', 'FETCH_START', { mode: 'polling' });

    try {
      const response = await fetchWithRetry(
        `${BASE_URL}/api/v1/status-history?created_after=${createdAfter}&limit=100`,
        2
      );
      
      if (!response.ok) {
        const error = new ApiError(response.status, response.statusText);
        logger.error('DataService', 'FETCH_ERROR', { 
          errorMessage: error.message, 
          statusCode: response.status 
        });
        throw error;
      }

      const apiResponse: ApiResponse<StatusHistoryRecord[]> = await response.json();
      
      if (!apiResponse.success) {
        const error = new ApiError(400, 'Bad Request', apiResponse.message);
        logger.error('DataService', 'FETCH_ERROR', { 
          errorMessage: error.message, 
          statusCode: 400 
        });
        throw error;
      }

      const durationMs = Date.now() - startTime;
      const estimatedSize = `~${Math.round(JSON.stringify(apiResponse.data).length / 1024)}KB`;

      logger.success('DataService', 'FETCH_SUCCESS', { 
        recordsCount: apiResponse.data.length, 
        estimatedSize, 
        durationMs 
      });

      return apiResponse.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      
      const networkError = error as Error;
      logger.error('DataService', 'FETCH_ERROR', { 
        errorMessage: networkError.message 
      });
      throw new ApiError(0, 'Network Error', networkError.message);
    }
  }
}

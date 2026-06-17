import type { StatusHistoryRecord, ApiResponse } from '../types/data';
import type { DataService } from './types';
import { ApiError } from './types';
import { logger } from '../utils/logger';

export class ApiDataService implements DataService {
  async fetchRecords(): Promise<StatusHistoryRecord[]> {
    const startTime = Date.now();
    logger.debug('DataService', 'FETCH_START', { mode: 'initial' });

    try {
      const response = await fetch('/api/v1/status-history?limit=20');
      
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

import { useState, useEffect, useRef } from 'react';
import type { StatusHistoryRecord, AggregatedData } from '../types/data';
import type { DataService } from '../services/types';
import { MockDataService } from '../services/mockDataService';
import { aggregateAll } from '../utils/aggregators';
import { logger } from '../utils/logger';

export function useRealtimeData(
  dataService: DataService = new MockDataService(),
  initialLimit: number = 20,
  pollLimit: number = 100
) {
  const [records, setRecords] = useState<StatusHistoryRecord[]>([]);
  const [aggregatedData, setAggregatedData] = useState<AggregatedData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  
  const intervalIdRef = useRef<number | null>(null);
  const tickCountRef = useRef(0);

  // Первичная загрузка данных
  useEffect(() => {
    const loadInitialData = async () => {
      logger.info('useRealtimeData', 'INIT_LOAD_START');
      logger.group('useRealtimeData', 'Первичная загрузка данных');

      try {
        const initialRecords = await dataService.fetchRecords(initialLimit, pollLimit);
        setRecords(initialRecords);

        const aggregated = aggregateAll(initialRecords);
        setAggregatedData(aggregated);
        setLoading(false);

        logger.success('useRealtimeData', 'INIT_LOAD_SUCCESS', { 
          recordsCount: initialRecords.length 
        });
        logger.groupEnd();

        // Запуск периодического обновления
        logger.info('useRealtimeData', 'TICKER_START', { intervalMs: 5000 });
        
        intervalIdRef.current = window.setInterval(async () => {
          tickCountRef.current += 1;
          const tickNumber = tickCountRef.current;
          
          logger.debug('useRealtimeData', 'POLL_START', { tickNumber });
          const pollStartTime = Date.now();

          try {
            const freshRecords = await dataService.fetchRecords(initialLimit, pollLimit);
            
            if (freshRecords.length === 0) {
              logger.info('useRealtimeData', 'POLL_EMPTY', { tickNumber });
              return;
            }

            setRecords(freshRecords);
            const aggregated = aggregateAll(freshRecords);
            setAggregatedData(aggregated);

            const tickMs = Date.now() - pollStartTime;
            logger.success('useRealtimeData', 'POLL_SUCCESS', { 
              recordsCount: freshRecords.length, 
              tickMs 
            });
          } catch (err) {
            const error = err as Error;
            logger.error('useRealtimeData', 'POLL_ERROR', { 
              errorMessage: error.message, 
              tickNumber 
            });
          }
        }, 5000);
      } catch (err) {
        const error = err as Error;
        logger.error('useRealtimeData', 'INIT_LOAD_ERROR', { errorMessage: error.message });
        logger.groupEnd();
        setError(error);
        setLoading(false);
      }
    };

    loadInitialData();

    // Cleanup при размонтировании
    return () => {
      if (intervalIdRef.current !== null) {
        window.clearInterval(intervalIdRef.current);
        logger.info('useRealtimeData', 'TICKER_STOP', { totalTicks: tickCountRef.current });
      }
      logger.info('useRealtimeData', 'UNMOUNT');
    };
  }, []);

  return { records, aggregatedData, loading, error };
}

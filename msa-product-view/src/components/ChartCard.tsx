import { useEffect, type ReactNode } from 'react';
import { logger } from '../utils/logger';

interface ChartCardProps {
  title: string;
  children: ReactNode;
}

export default function ChartCard({ title, children }: ChartCardProps) {
  useEffect(() => {
    logger.info('ChartCard', 'CHART_MOUNT', { chartName: title });
    return () => {
      logger.info('ChartCard', 'CHART_UNMOUNT', { chartName: title });
    };
  }, [title]);

  return (
    <div className="bg-white rounded-lg shadow-md p-4 flex flex-col">
      <h3 className="text-lg font-semibold mb-3 text-gray-800">{title}</h3>
      <div className="flex-1 min-h-0">
        {children}
      </div>
    </div>
  );
}

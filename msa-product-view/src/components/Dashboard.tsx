import { useEffect } from 'react';
import { useRealtimeData } from '../hooks/useRealtimeData';
import { ApiDataService } from '../services/apiDataService';
import { logger } from '../utils/logger';
import StatusBarChart from './charts/StatusBarChart';
import AvgProcessingTimeChart from './charts/AvgProcessingTimeChart';
import TopProductsChart from './charts/TopProductsChart';
import ReasonsBarChart from './charts/ReasonsBarChart';
import UserActivityChart from './charts/UserActivityChart';
import StatusPieChart from './charts/StatusPieChart';
import ReasonsPieChart from './charts/ReasonsPieChart';
import FromStatusPieChart from './charts/FromStatusPieChart';
import HourlyActivityChart from './charts/HourlyActivityChart';

export default function Dashboard() {
  const { aggregatedData, loading, error } = useRealtimeData(new ApiDataService());

  useEffect(() => {
    logger.info('Dashboard', 'MOUNT');
    const startTime = Date.now();
    
    return () => {
      const uptime = ((Date.now() - startTime) / 1000).toFixed(1);
      logger.info('Dashboard', 'UNMOUNT', { uptime: `${uptime}s` });
    };
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-xl text-gray-600">Загрузка данных...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-xl text-red-600">Ошибка: {error.message}</div>
      </div>
    );
  }

  if (!aggregatedData) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-xl text-gray-600">Нет данных</div>
      </div>
    );
  }

  logger.debug('Dashboard', 'RENDER', { chartsCount: 9 });

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">
        Product Status History Dashboard
      </h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 h-[calc(100vh-120px)]">
        <StatusBarChart data={aggregatedData.statusBar} />
        <AvgProcessingTimeChart data={aggregatedData.avgProcessingTime} />
        <TopProductsChart data={aggregatedData.topProducts} />
        <ReasonsBarChart data={aggregatedData.reasonsBar} />
        <UserActivityChart data={aggregatedData.userActivity} />
        <StatusPieChart data={aggregatedData.statusPie} />
        <ReasonsPieChart data={aggregatedData.reasonsPie} />
        <FromStatusPieChart data={aggregatedData.fromStatusPie} />
        <HourlyActivityChart data={aggregatedData.hourlyActivity} />
      </div>
    </div>
  );
}

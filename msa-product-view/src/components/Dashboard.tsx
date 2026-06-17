import { useState, useEffect } from 'react';
import { useRealtimeData } from '../hooks/useRealtimeData';
import { ApiDataService } from '../services/apiDataService';
import { logger } from '../utils/logger';
import TabNavigation, { TabType } from './tabs/TabNavigation';
import ChartsTab from './tabs/ChartsTab';
import DataTab from './tabs/DataTab';
import SettingsTab from './tabs/SettingsTab';

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState<TabType>('charts');
  const { records, aggregatedData, loading, error } = useRealtimeData(new ApiDataService());

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

  logger.debug('Dashboard', 'RENDER', { activeTab, chartsCount: 9 });

  const renderTabContent = () => {
    switch (activeTab) {
      case 'charts':
        return <ChartsTab data={aggregatedData} />;
      case 'data':
        return <DataTab records={records} />;
      case 'settings':
        return <SettingsTab />;
      default:
        return <ChartsTab data={aggregatedData} />;
    }
  };

  return (
    <div className="flex h-screen bg-gray-100">
      <TabNavigation activeTab={activeTab} onTabChange={setActiveTab} />
      <div className="flex-1 overflow-auto">
        {renderTabContent()}
      </div>
    </div>
  );
}

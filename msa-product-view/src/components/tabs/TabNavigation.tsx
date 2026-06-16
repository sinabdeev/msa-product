import { BarChart3, Database, Settings } from 'lucide-react';

export type TabType = 'charts' | 'data' | 'settings';

interface TabNavigationProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
}

const tabs = [
  { id: 'charts' as TabType, label: 'Charts', icon: BarChart3 },
  { id: 'data' as TabType, label: 'Data', icon: Database },
  { id: 'settings' as TabType, label: 'Settings', icon: Settings },
];

export default function TabNavigation({ activeTab, onTabChange }: TabNavigationProps) {
  return (
    <div className="w-20 bg-white border-r border-gray-200 flex flex-col">
      {tabs.map((tab) => {
        const Icon = tab.icon;
        const isActive = activeTab === tab.id;
        
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={`flex flex-col items-center justify-center gap-1 px-2 py-4 text-xs font-medium transition-colors ${
              isActive
                ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <Icon size={20} />
            <span>{tab.label}</span>
          </button>
        );
      })}
    </div>
  );
}

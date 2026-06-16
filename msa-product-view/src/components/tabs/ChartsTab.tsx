import StatusBarChart from '../charts/StatusBarChart';
import AvgProcessingTimeChart from '../charts/AvgProcessingTimeChart';
import TopProductsChart from '../charts/TopProductsChart';
import ReasonsBarChart from '../charts/ReasonsBarChart';
import UserActivityChart from '../charts/UserActivityChart';
import StatusPieChart from '../charts/StatusPieChart';
import ReasonsPieChart from '../charts/ReasonsPieChart';
import FromStatusPieChart from '../charts/FromStatusPieChart';
import HourlyActivityChart from '../charts/HourlyActivityChart';
import { AggregatedData } from '../../types/data';

interface ChartsTabProps {
  data: AggregatedData;
}

export default function ChartsTab({ data }: ChartsTabProps) {
  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">
        Product Status History Dashboard
      </h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 h-[calc(100vh-120px)]">
        <StatusBarChart data={data.statusBar} />
        <AvgProcessingTimeChart data={data.avgProcessingTime} />
        <TopProductsChart data={data.topProducts} />
        <ReasonsBarChart data={data.reasonsBar} />
        <UserActivityChart data={data.userActivity} />
        <StatusPieChart data={data.statusPie} />
        <ReasonsPieChart data={data.reasonsPie} />
        <FromStatusPieChart data={data.fromStatusPie} />
        <HourlyActivityChart data={data.hourlyActivity} />
      </div>
    </div>
  );
}

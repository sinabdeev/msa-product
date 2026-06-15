import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { BarChartData } from '../../types/data';
import ChartCard from '../ChartCard';

interface AvgProcessingTimeChartProps {
  data: BarChartData[];
}

export default function AvgProcessingTimeChart({ data }: AvgProcessingTimeChartProps) {
  return (
    <ChartCard title="Среднее время обработки">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" angle={-90} textAnchor="end" height={100} interval={0} />
          <YAxis />
          <Tooltip />
          <Bar 
            dataKey="value" 
            fill="#82ca9d" 
            isAnimationActive={true}
            animationDuration={500}
          />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}

import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { BarChartData } from '../../types/data';
import ChartCard from '../ChartCard';

interface UserActivityChartProps {
  data: BarChartData[];
}

export default function UserActivityChart({ data }: UserActivityChartProps) {
  return (
    <ChartCard title="Активность пользователей">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
          <YAxis />
          <Tooltip />
          <Bar 
            dataKey="value" 
            fill="#a4de6c" 
            isAnimationActive={true}
            animationDuration={500}
          />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}

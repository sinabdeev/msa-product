import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { BarChartData } from '../../types/data';
import ChartCard from '../ChartCard';

interface ReasonsBarChartProps {
  data: BarChartData[];
}

export default function ReasonsBarChart({ data }: ReasonsBarChartProps) {
  return (
    <ChartCard title="Причины переходов">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
          <YAxis />
          <Tooltip />
          <Bar 
            dataKey="value" 
            fill="#ff7300" 
            isAnimationActive={true}
            animationDuration={500}
          />
        </BarChart>
      </ResponsiveContainer>
    </ChartCard>
  );
}

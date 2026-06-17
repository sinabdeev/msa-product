import type { StatusHistoryRecord } from '../../types/data';

interface DataTabProps {
  records: StatusHistoryRecord[];
}

// Форматирование UUID: первые 8 символов + '...'
const formatUuid = (uuid: string | null): string => {
  if (!uuid) return '—';
  return uuid.substring(0, 8) + '...';
};

// Форматирование даты в формат DD.MM.YYYY HH:mm:ss
const formatDate = (isoDate: string): string => {
  const date = new Date(isoDate);
  return date.toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};

// Форматирование значения с обработкой null
const formatNullable = (value: string | number | null): string => {
  if (value === null || value === undefined) return '—';
  return String(value);
};

export default function DataTab({ records }: DataTabProps) {
  return (
    <div className="p-6 h-full flex flex-col">
      <h1 className="text-2xl font-bold text-gray-800 mb-4">
        Product Status History
      </h1>
      
      <div className="flex-1 overflow-auto border border-gray-200 rounded-lg">
        <table className="w-full border-collapse">
          <thead className="sticky top-0 z-10">
            <tr>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                ID
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                Product ID
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                From Status
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                To Status
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                Reason
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                User ID
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3 border-r border-blue-500">
                Created At
              </th>
              <th className="bg-blue-600 text-white font-bold text-center px-4 py-3">
                Duration (s)
              </th>
            </tr>
          </thead>
          <tbody>
            {records.map((record, index) => (
              <tr
                key={record.id}
                className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
              >
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center font-mono text-xl">
                  {formatUuid(record.id)}
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center font-mono text-xl">
                  {formatUuid(record.productId)}
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center">
                  <span className="px-2 py-1 bg-gray-100 rounded text-lg font-medium">
                    {record.fromStatus}
                  </span>
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center">
                  <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-lg font-medium">
                    {record.toStatus}
                  </span>
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center text-xl">
                  {formatNullable(record.reason)}
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center font-mono text-xl">
                  {formatUuid(record.userId)}
                </td>
                <td className="px-4 py-2 border-r border-b border-gray-200 text-center text-xl whitespace-nowrap">
                  {formatDate(record.createdAt)}
                </td>
                <td className="px-4 py-2 border-b border-gray-200 text-center text-xl">
                  {formatNullable(record.processingDurationSeconds)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {records.length === 0 && (
          <div className="flex items-center justify-center py-12 text-gray-500">
            Нет данных для отображения
          </div>
        )}
      </div>
      
      <div className="mt-4 text-sm text-gray-600">
        Всего записей: {records.length}
      </div>
    </div>
  );
}

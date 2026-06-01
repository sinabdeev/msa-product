# План реализации: Страница с 9 анимированными графиками

## 1. Цель
Создать страницу с 9 графиками для визуализации данных из таблицы `product.product_status_history`. Данные обновляются каждую секунду с анимацией переходов.

## 2. Стек технологий
| Компонент | Технология |
|-----------|-----------|
| Язык | TypeScript |
| Фреймворк | React 18+ |
| Сборщик | Vite |
| Графики | Recharts (SVG, встроенная анимация) |
| Стилизация | Tailwind CSS |
| Сетка | CSS Grid (3×3) |
| API | REST (сырые записи) |
| Состояние | React hooks (useState + useEffect) |

## 3. Структура БД (product.product_status_history)

```sql
CREATE TABLE product.product_status_history (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    product_id uuid NOT NULL,
    from_status text NOT NULL,
    to_status text NOT NULL,
    reason text NULL,
    user_id uuid NULL,
    created_at timestamp DEFAULT now() NOT NULL,
    processing_duration_seconds int8 NULL,
    CONSTRAINT product_status_history_pkey PRIMARY KEY (id)
);
```

## 4. Архитектура данных

```
REST API (сырые записи) 
       ↓
  DataService (слой данных)
       ↓
  Агрегаторы (utils/aggregators.ts)
       ↓
  useRealtimeData (хук, тикер 1с)
       ↓
  9 компонентов графиков
```

### Слой данных (DataService)

Абстракция, которая позволяет легко переключаться между мок-данными и реальным API:

```typescript
interface DataService {
  fetchRecords(): Promise<StatusHistoryRecord[]>;
}
```

- **MockDataService** — пока нет API, генерирует случайные записи на фронте
- **ApiDataService** — когда появится API, делает fetch к REST эндпоинту

### Агрегаторы (utils/aggregators.ts)

Чистые функции, которые принимают массив сырых записей и возвращают агрегированные данные для каждого графика:

```typescript
function aggregateByStatus(records: StatusHistoryRecord[]): BarChartData[]
function aggregateAvgProcessingTime(records: StatusHistoryRecord[]): BarChartData[]
function aggregateTopProducts(records: StatusHistoryRecord[]): BarChartData[]
function aggregateReasons(records: StatusHistoryRecord[]): BarChartData[]
function aggregateUserActivity(records: StatusHistoryRecord[]): BarChartData[]
function aggregateStatusPie(records: StatusHistoryRecord[]): PieChartData[]
function aggregateReasonsPie(records: StatusHistoryRecord[]): PieChartData[]
function aggregateFromStatusPie(records: StatusHistoryRecord[]): PieChartData[]
function aggregateHourlyActivity(records: StatusHistoryRecord[]): PieChartData[]
```

## 5. Типы данных на фронте

```typescript
// Модель записи из БД (сырые данные)
interface StatusHistoryRecord {
  id: string;
  product_id: string;
  from_status: string;
  to_status: string;
  reason: string | null;
  user_id: string | null;
  created_at: string;
  processing_duration_seconds: number | null;
}

// Агрегированные данные для BarChart
interface BarChartData {
  name: string;
  value: number;
}

// Агрегированные данные для PieChart
interface PieChartData {
  name: string;
  value: number;
  fill: string;
}

// Все агрегированные данные для Dashboard
interface AggregatedData {
  statusBar: BarChartData[];
  avgProcessingTime: BarChartData[];
  topProducts: BarChartData[];
  reasonsBar: BarChartData[];
  userActivity: BarChartData[];
  statusPie: PieChartData[];
  reasonsPie: PieChartData[];
  fromStatusPie: PieChartData[];
  hourlyActivity: PieChartData[];
}
```

## 6. 9 графиков и их агрегация

| # | Тип | Название | Агрегация |
|---|-----|----------|-----------|
| 1 | BarChart | Переходы по статусам | GROUP BY `to_status` → COUNT |
| 2 | BarChart | Среднее время обработки | GROUP BY `to_status` → AVG(`processing_duration_seconds`) |
| 3 | BarChart | Топ продуктов | GROUP BY `product_id` → COUNT, топ-10 |
| 4 | BarChart | Причины переходов | GROUP BY `reason` → COUNT |
| 5 | BarChart | Активность пользователей | GROUP BY `user_id` → COUNT, топ-10 |
| 6 | PieChart | Доля статусов | GROUP BY `to_status` → COUNT |
| 7 | PieChart | Доля причин (donut) | GROUP BY `reason` → COUNT |
| 8 | PieChart | Откуда переходят | GROUP BY `from_status` → COUNT |
| 9 | PieChart | Активность по часам | GROUP BY час из `created_at` → COUNT |

## 7. Структура проекта

```
src/
├── main.tsx
├── App.tsx
├── index.css                       # Tailwind directives
├── types/
│   └── data.ts                     # Типы данных
├── services/
│   ├── types.ts                    # Интерфейс DataService
│   ├── mockDataService.ts          # Генератор мок-данных
│   └── apiDataService.ts           # Реальное API (заглушка пока)
├── utils/
│   └── aggregators.ts              # Функции агрегации
├── hooks/
│   └── useRealtimeData.ts          # Хук: тикер 1с + агрегация
├── components/
│   ├── Dashboard.tsx               # Сетка 3×3
│   ├── ChartCard.tsx               # Обёртка карточки графика
│   └── charts/
│       ├── StatusBarChart.tsx           # 1
│       ├── AvgProcessingTimeChart.tsx   # 2
│       ├── TopProductsChart.tsx         # 3
│       ├── ReasonsBarChart.tsx          # 4
│       ├── UserActivityChart.tsx        # 5
│       ├── StatusPieChart.tsx           # 6
│       ├── ReasonsPieChart.tsx          # 7 (donut)
│       ├── FromStatusPieChart.tsx       # 8
│       └── HourlyActivityChart.tsx      # 9
```

## 8. Генерация мок-данных (mockDataService.ts)

Пока нет реального API, генерируем случайные записи:

- **Статусы**: `draft`, `pending_review`, `approved`, `rejected`, `published`, `archived`
- **Причины**: `specification_error`, `design_review`, `quality_check`, `content_update`, `expired`
- **product_id**: 20 разных UUID
- **user_id**: 10 разных UUID
- **processing_duration_seconds**: случайное число от 10 до 3600
- **created_at**: случайное время за последние 24 часа

Каждую секунду генерируется 1-5 новых записей, пул записей растёт.

## 9. Анимация

- Recharts: `isAnimationActive={true}` по умолчанию
- `animationDuration={500}` для плавных переходов
- При обновлении данных каждую секунду графики плавно меняются

## 10. Цветовая палитра

```typescript
const COLORS = [
  '#8884d8', '#82ca9d', '#ffc658', '#ff7300',
  '#a4de6c', '#d0ed57', '#ffc0cb', '#8dd1e1',
  '#a4a4f5', '#e57373', '#64b5f6', '#81c784'
];
```

## 11. Пошаговый план выполнения

| Шаг | Действие | Файлы |
|-----|----------|-------|
| 1 | Инициализировать Vite + React + TS проект | `npm create vite@latest` |
| 2 | Установить зависимости | `npm install recharts tailwindcss @tailwindcss/vite` |
| 3 | Настроить Tailwind CSS | `tailwind.config.js`, `postcss.config.js`, `index.css` |
| 4 | Создать типы данных | `src/types/data.ts` |
| 5 | Создать интерфейс DataService + MockDataService | `src/services/types.ts`, `src/services/mockDataService.ts` |
| 6 | Создать агрегаторы | `src/utils/aggregators.ts` |
| 7 | Создать хук useRealtimeData | `src/hooks/useRealtimeData.ts` |
| 8 | Создать ChartCard | `src/components/ChartCard.tsx` |
| 9 | Создать 9 компонентов графиков | `src/components/charts/*.tsx` |
| 10 | Создать Dashboard | `src/components/Dashboard.tsx` |
| 11 | Обновить App.tsx | Подключить Dashboard |
| 12 | Проверить сборку и запустить | `npm run dev` |

## 12. Критерии готовности

- [ ] Проект собирается без ошибок (`npm run build`)
- [ ] Страница отображает 9 графиков в сетке 3×3
- [ ] Данные обновляются каждую секунду
- [ ] Переходы между состояниями данных анимированы
- [ ] Графики соответствуют структуре таблицы БД
- [ ] Слой данных абстрагирован (легко заменить мок на реальное API)
- [ ] Адаптивная вёрстка
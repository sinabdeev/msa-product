# FAQ — Часто задаваемые вопросы

## Где задаётся лимит получаемых с бэка записей?

Лимиты настраиваются через **вкладку Settings** в интерфейсе дашборда.

Там доступны два параметра:

| Параметр | По умолчанию | Описание |
|----------|-------------|----------|
| **Initial Load Limit** | `20` | Количество записей при начальной загрузке дашборда |
| **Poll Limit** | `100` | Количество записей при дозагрузке (long polling) |

Значения сохраняются в `localStorage` и восстанавливаются между сессиями.

### Где хранятся настройки

- Хук: [`src/hooks/useSettings.ts`](src/hooks/useSettings.ts)
- Компонент: [`src/components/tabs/SettingsTab.tsx`](src/components/tabs/SettingsTab.tsx)
- Ключи в `localStorage`: `settings:initialLimit`, `settings:pollLimit`

### Как лимиты применяются

1. [`Dashboard`](src/components/Dashboard.tsx) получает лимиты из `useSettings()`.
2. Передаёт их в [`useRealtimeData`](src/hooks/useRealtimeData.ts).
3. Хук передаёт `initialLimit` в [`ApiDataService.fetchRecords()`](src/services/apiDataService.ts).
4. Сервис формирует URL: `GET /api/v1/status-history?limit={initialLimit}`.

### Параметр `limit` в OpenAPI (спецификация бэка)

В файле [`openapi.yaml`](openapi.yaml:28) параметр описан так:

- **Тип:** integer
- **По умолчанию:** 1000
- **Минимум:** 1
- **Максимум:** 10000
- **Обязательность:** нет

То есть если не передавать `limit`, бэк отдаст 1000 записей. Фронт же явно передаёт значение из настроек.

### Где ещё встречается `limit`

| Где | Значение | Назначение |
|-----|----------|------------|
| [`README.md`](README.md:40) | по умолч. 1000 | Документация API |
| [`openapi.yaml`](openapi.yaml:34) | default: 1000 | Спецификация бэка |
| [`Product API.postman_collection.json`](Product%20API.postman_collection.json:15) | `limit=100` / `limit=1000` | Тестовые запросы в Postman |
| [`plans/implementation-plan.md`](plans/implementation-plan.md:20) | `limit=1000` / `limit=100` | План реализации |

## Где находится вкладка Settings с текстом "Coming soon..."?

Файл: [`src/components/tabs/SettingsTab.tsx`](src/components/tabs/SettingsTab.tsx)

Строка 6 содержит текст `Coming soon...`:

```tsx
<p className="text-gray-500">Coming soon...</p>
```

Полное содержимое компонента:

```tsx
export default function SettingsTab() {
  return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center">
        <h2 className="text-2xl font-semibold text-gray-700 mb-2">Settings</h2>
        <p className="text-gray-500">Coming soon...</p>
      </div>
    </div>
  );
}
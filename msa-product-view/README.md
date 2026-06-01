# MSA Product View

Страница с 9 анимированными графиками для визуализации данных из таблицы `product.product_status_history`. При получении новых данных графики **плавно анимируют переходы** — столбцы гистограмм и сектора круговых диаграмм меняют свои размеры с анимацией.

Страница с 9 анимированными графиками для визуализации данных из таблицы `product.product_status_history`.

## Стек

- **React 18** + **TypeScript**
- **Vite** — сборщик
- **Recharts** — библиотека графиков с анимацией
- **Tailwind CSS** — стилизация

## Структура БД

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

## REST API

Фронтенду нужен один эндпоинт, который отдаёт **сырые записи** из таблицы. Агрегация данных для графиков выполняется на стороне клиента.

### GET /api/status-history

Отдаёт массив записей из таблицы `product.product_status_history`.

**Query параметры:**

| Параметр | Тип | Обязательный | Описание |
|----------|-----|-------------|----------|
| `limit` | number | нет | Максимальное количество записей (по умолчанию 1000) |
| `offset` | number | нет | Смещение для пагинации |
| `from_status` | string | нет | Фильтр по исходному статусу |
| `to_status` | string | нет | Фильтр по целевому статусу |
| `product_id` | uuid | нет | Фильтр по продукту |
| `user_id` | uuid | нет | Фильтр по пользователю |
| `created_after` | ISO datetime | нет | Записи не ранее указанной даты |
| `created_before` | ISO datetime | нет | Записи не позднее указанной даты |

**Пример ответа (200 OK):**

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "product_id": "660e8400-e29b-41d4-a716-446655440001",
      "from_status": "draft",
      "to_status": "pending_review",
      "reason": "specification_error",
      "user_id": "770e8400-e29b-41d4-a716-446655440002",
      "created_at": "2026-06-01T12:00:00Z",
      "processing_duration_seconds": 120
    }
  ],
  "total": 1500,
  "limit": 100,
  "offset": 0
}
```

**Типы полей:**

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | string (uuid) | Первичный ключ |
| `product_id` | string (uuid) | ID продукта |
| `from_status` | string | Предыдущий статус |
| `to_status` | string | Новый статус |
| `reason` | string или null | Причина изменения |
| `user_id` | string (uuid) или null | ID пользователя |
| `created_at` | string (ISO 8601) | Дата создания |
| `processing_duration_seconds` | number или null | Длительность обработки (сек) |

**Возможные значения статусов:**

- `draft` — черновик
- `pending_review` — ожидает проверки
- `approved` — одобрено
- `rejected` — отклонено
- `published` — опубликовано
- `archived` — архивировано

**Возможные значения причин (reason):**

- `specification_error` — ошибка в спецификации
- `design_review` — проверка дизайна
- `quality_check` — контроль качества
- `content_update` — обновление контента
- `expired` — истёк срок

### Частота запросов (Polling)

Фронтенд работает в режиме **polling** (опрос) с интервалом **1 секунда**:

1. **Первый запрос** — получает последние 1000 записей (всю историю)
2. **Каждую секунду** — запрашивает только новые записи:
   `GET /api/status-history?created_after={timestamp_последней_полученной_записи}&limit=100`
3. Если новых записей нет — сервер возвращает пустой `data: []`, графики не меняются

Таким образом, нагрузка на сервер — **1 GET-запрос в секунду** с лёгким фильтром по индексу `created_at`.

## Графики (сетка 3×3)

### Гистограммы (BarChart)
1. **Переходы по статусам** — количество записей по `to_status`
2. **Среднее время обработки** — AVG(`processing_duration_seconds`) по `to_status`
3. **Топ продуктов** — топ-10 `product_id` по количеству переходов
4. **Причины переходов** — распределение `reason`
5. **Активность пользователей** — топ-10 `user_id` по количеству изменений

### Круговые диаграммы (PieChart)
6. **Доля статусов** — распределение `to_status`
7. **Доля причин** — распределение `reason` (donut)
8. **Откуда переходят** — распределение `from_status`
9. **Активность по часам** — распределение по часам из `created_at`

## Сетап проекта

### Требования

- **Node.js** версии 18+ (скачать: https://nodejs.org/)
- **npm** (устанавливается вместе с Node.js)

### Установка и запуск

```bash
# 1. Клонировать репозиторий
git clone <url-репозитория>
cd msa-product-view

# 2. Установить зависимости
npm install

# 3. Запустить в режиме разработки
npm run dev
```

После запуска открой браузер по адресу, который будет указан в терминале (обычно `http://localhost:5173`).

### Сборка для продакшена

```bash
npm run build
```

Собранные файлы будут в папке `dist/`.

### Предпросмотр собранного проекта

```bash
npm run preview
```

## Команды

| Команда | Описание |
|---------|----------|
| `npm run dev` | Запуск в режиме разработки |
| `npm run build` | Сборка для продакшена |
| `npm run preview` | Предпросмотр собранного проекта |
| `npm run lint` | Проверка кода линтером |
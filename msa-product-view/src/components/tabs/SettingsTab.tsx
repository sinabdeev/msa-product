import { useState, useEffect } from 'react';

interface SettingsTabProps {
  initialLimit: number;
  pollLimit: number;
  onInitialLimitChange: (value: number) => void;
  onPollLimitChange: (value: number) => void;
  onResetDefaults: () => void;
}

const MIN_LIMIT = 1;
const MAX_LIMIT = 250;
const DEFAULT_INITIAL = 20;
const DEFAULT_POLL = 100;

function LimitControl({
  label,
  description,
  value,
  onChange,
  defaultValue,
}: {
  label: string;
  description: string;
  value: number;
  onChange: (value: number) => void;
  defaultValue: number;
}) {
  const isDefault = value === defaultValue;

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-5">
      <div className="flex items-center justify-between mb-2">
        <label className="text-lg font-semibold text-gray-800">{label}</label>
        {!isDefault && (
          <button
            type="button"
            onClick={() => onChange(defaultValue)}
            className="text-sm text-blue-600 hover:text-blue-800 transition-colors"
          >
            Сбросить
          </button>
        )}
      </div>
      <p className="text-sm text-gray-500 mb-4">{description}</p>

      <div className="flex items-center gap-4">
        <input
          type="range"
          min={MIN_LIMIT}
          max={MAX_LIMIT}
          value={value}
          onChange={(e) => onChange(Number(e.target.value))}
          className="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
        />
        <input
          type="number"
          min={MIN_LIMIT}
          max={MAX_LIMIT}
          value={value}
          onChange={(e) => {
            const raw = e.target.value;
            if (raw === '') return;
            const num = parseInt(raw, 10);
            if (!isNaN(num)) {
              onChange(Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, num)));
            }
          }}
          className="w-24 px-3 py-2 text-center border border-gray-300 rounded-lg text-sm font-mono
                     focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
      </div>

      <div className="flex justify-between text-xs text-gray-400 mt-1 px-1">
        <span>{MIN_LIMIT}</span>
        <span>{MAX_LIMIT}</span>
      </div>
    </div>
  );
}

export default function SettingsTab({
  initialLimit,
  pollLimit,
  onInitialLimitChange,
  onPollLimitChange,
  onResetDefaults,
}: SettingsTabProps) {
  // Локальные временные значения — применяются только по кнопке "Сохранить"
  const [draftInitial, setDraftInitial] = useState(initialLimit);
  const [draftPoll, setDraftPoll] = useState(pollLimit);

  // Синхронизация при смене пропсов (например, после сброса из Dashboard)
  useEffect(() => {
    setDraftInitial(initialLimit);
    setDraftPoll(pollLimit);
  }, [initialLimit, pollLimit]);

  const hasChanges = draftInitial !== initialLimit || draftPoll !== pollLimit;

  const handleSave = () => {
    onInitialLimitChange(draftInitial);
    onPollLimitChange(draftPoll);
  };

  const handleReset = () => {
    setDraftInitial(DEFAULT_INITIAL);
    setDraftPoll(DEFAULT_POLL);
    onResetDefaults();
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Settings</h1>
        <div className="flex items-center gap-3">
          <button
            onClick={handleReset}
            className="px-4 py-2 text-sm font-medium text-gray-600 border border-gray-300 
                       rounded-lg hover:bg-gray-50 transition-colors"
          >
            Сбросить на defaults
          </button>
          <button
            onClick={handleSave}
            disabled={!hasChanges}
            className={`px-6 py-2 text-sm font-medium rounded-lg transition-colors ${
              hasChanges
                ? 'bg-blue-600 text-white hover:bg-blue-700'
                : 'bg-gray-200 text-gray-400 cursor-not-allowed'
            }`}
          >
            Сохранить
          </button>
        </div>
      </div>

      <div className="space-y-4">
        <LimitControl
          label="Initial Load Limit"
          description="Количество записей при начальной загрузке дашборда"
          value={draftInitial}
          onChange={setDraftInitial}
          defaultValue={DEFAULT_INITIAL}
        />

        <LimitControl
          label="Poll Limit"
          description="Количество записей при дозагрузке (long polling)"
          value={draftPoll}
          onChange={setDraftPoll}
          defaultValue={DEFAULT_POLL}
        />
      </div>

      <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-sm text-blue-800">
          <strong>ℹ️ Диапазон значений:</strong> от {MIN_LIMIT} до {MAX_LIMIT}.
          Изменения применяются только после нажатия кнопки <strong>«Сохранить»</strong>.
        </p>
      </div>
    </div>
  );
}

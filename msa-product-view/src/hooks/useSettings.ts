import { useState, useCallback } from 'react';

const STORAGE_KEY_INITIAL = 'settings:initialLimit';
const STORAGE_KEY_POLL = 'settings:pollLimit';

const DEFAULT_INITIAL_LIMIT = 20;
const DEFAULT_POLL_LIMIT = 100;

const MIN_LIMIT = 1;
const MAX_LIMIT = 10000;

function loadNumber(key: string, defaultValue: number): number {
  try {
    const stored = localStorage.getItem(key);
    if (stored === null) return defaultValue;
    const parsed = parseInt(stored, 10);
    if (isNaN(parsed)) return defaultValue;
    return Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, parsed));
  } catch {
    return defaultValue;
  }
}

function saveNumber(key: string, value: number): void {
  try {
    localStorage.setItem(key, String(value));
  } catch {
    // localStorage недоступен — игнорируем
  }
}

export function useSettings() {
  const [initialLimit, setInitialLimitState] = useState<number>(
    () => loadNumber(STORAGE_KEY_INITIAL, DEFAULT_INITIAL_LIMIT)
  );
  const [pollLimit, setPollLimitState] = useState<number>(
    () => loadNumber(STORAGE_KEY_POLL, DEFAULT_POLL_LIMIT)
  );

  const setInitialLimit = useCallback((value: number) => {
    const clamped = Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, value));
    setInitialLimitState(clamped);
    saveNumber(STORAGE_KEY_INITIAL, clamped);
  }, []);

  const setPollLimit = useCallback((value: number) => {
    const clamped = Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, value));
    setPollLimitState(clamped);
    saveNumber(STORAGE_KEY_POLL, clamped);
  }, []);

  const resetToDefaults = useCallback(() => {
    setInitialLimit(DEFAULT_INITIAL_LIMIT);
    setPollLimit(DEFAULT_POLL_LIMIT);
  }, [setInitialLimit, setPollLimit]);

  return {
    initialLimit,
    pollLimit,
    setInitialLimit,
    setPollLimit,
    resetToDefaults,
  };
}
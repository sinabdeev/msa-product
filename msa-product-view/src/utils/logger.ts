// Уровни логирования
type LogLevel = 'info' | 'success' | 'warn' | 'error' | 'debug';

// Эмодзи для каждого уровня
const EMOJIS: Record<LogLevel, string> = {
  info: 'ℹ️',
  success: '✅',
  warn: '⚠️',
  error: '❌',
  debug: '🔍'
};

// Цвета для каждого уровня
const COLORS: Record<LogLevel, string> = {
  info: '#2196F3',
  success: '#4CAF50',
  warn: '#FF9800',
  error: '#F44336',
  debug: '#9E9E9E'
};

// Форматирование деталей для вывода
function formatDetails(details?: Record<string, unknown>): string[] {
  if (!details || Object.keys(details).length === 0) {
    return [];
  }
  return Object.entries(details).map(([key, value]) => `${key}: ${value}`);
}

// Базовая функция логирования
function log(level: LogLevel, component: string, event: string, details?: Record<string, unknown>) {
  const timestamp = new Date().toISOString().slice(11, 23); // HH:mm:ss.SSS
  const style = `color: ${COLORS[level]}; font-weight: bold;`;
  const detailsStr = formatDetails(details).join(' | ');
  
  if (detailsStr) {
    console.log(`%c[${timestamp}] ${EMOJIS[level]} [${component}] ${event} | ${detailsStr}`, style);
  } else {
    console.log(`%c[${timestamp}] ${EMOJIS[level]} [${component}] ${event}`, style);
  }
}

// Экспортируемые функции логгера
export const logger = {
  info(component: string, event: string, details?: Record<string, unknown>) {
    log('info', component, event, details);
  },

  success(component: string, event: string, details?: Record<string, unknown>) {
    log('success', component, event, details);
  },

  warn(component: string, event: string, details?: Record<string, unknown>) {
    log('warn', component, event, details);
  },

  error(component: string, event: string, details?: Record<string, unknown>) {
    log('error', component, event, details);
  },

  debug(component: string, event: string, details?: Record<string, unknown>) {
    if (import.meta.env.DEV) {
      log('debug', component, event, details);
    }
  },

  group(component: string, event: string) {
    console.groupCollapsed(`📦 [${component}] ${event}`);
  },

  groupEnd() {
    console.groupEnd();
  }
};

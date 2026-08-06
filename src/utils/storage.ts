import { Task, UserSettings, SyncLog } from '../types';

const STORAGE_KEYS = {
  TASKS: 'itask_pro_tasks_v1',
  SETTINGS: 'itask_pro_settings_v1',
  SYNC_QUEUE: 'itask_pro_sync_queue_v1',
  SYNC_LOGS: 'itask_pro_sync_logs_v1',
};

export const defaultSettings: UserSettings = {
  darkMode: 'dark',
  accentColor: 'blue',
  appIcon: 'minimalist',
  language: 'es',
  biometricEnabled: true,
  biometricType: 'faceid',
  pinCode: '1234',
  autoLockMinutes: 5,
  cloudBackupEnabled: true,
  lastCloudBackup: new Date().toISOString(),
  offlineModeForce: false,
  batterySaverMode: false,
  pushNotificationsEnabled: true,
  criticalAlertsEnabled: true,
  notificationSound: 'aurora',
  quietHoursStart: '22:00',
  quietHoursEnd: '07:00',
  calendarAutoSync: true,
};

export const defaultInitialTasks: Task[] = [
  {
    id: 'task-1',
    title: 'Revisión de arquitectura y modelo de datos iOS',
    description: 'Verificar la escalabilidad de la base de datos local y sincronización en segundo plano con baja latencia.',
    category: 'projects',
    priority: 'critical',
    completed: false,
    dueDate: new Date(Date.now() + 86400000).toISOString().split('T')[0],
    dueTime: '10:00',
    reminderTime: '09:45',
    soundAlert: 'aurora',
    criticalAlert: true,
    subtasks: [
      { id: 'sub-1', title: 'Optimizar índices SQLite/IndexedDB', completed: true },
      { id: 'sub-2', title: 'Validar cifrado de datos biométricos', completed: false },
      { id: 'sub-3', title: 'Asegurar compatibilidad con widget iOS', completed: false }
    ],
    tags: ['iOS', 'Desarrollo', 'Sprint1'],
    recurrence: 'weekly',
    calendarSynced: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: 'task-2',
    title: 'Sincronizar calendario corporativo Google & iCal',
    description: 'Exportar archivo .ICS e integrar suscripción automatizada para reuniones estratégicas de la semana.',
    category: 'work',
    priority: 'high',
    completed: false,
    dueDate: new Date().toISOString().split('T')[0],
    dueTime: '15:30',
    reminderTime: '15:15',
    soundAlert: 'chime',
    criticalAlert: false,
    subtasks: [
      { id: 'sub-4', title: 'Generar URL del feed WebCal', completed: true },
      { id: 'sub-5', title: 'Probar importación en Apple Calendar', completed: false }
    ],
    tags: ['Calendario', 'Sincronización'],
    recurrence: 'none',
    calendarSynced: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: 'task-3',
    title: 'Informe de rendimiento de batería y memoria RAM',
    description: 'Monitorear métricas del panel de control para reducir el consumo energético en modo offline.',
    category: 'health',
    priority: 'medium',
    completed: true,
    dueDate: new Date(Date.now() - 86400000).toISOString().split('T')[0],
    dueTime: '18:00',
    reminderTime: '17:30',
    soundAlert: 'apex',
    criticalAlert: false,
    subtasks: [
      { id: 'sub-6', title: 'Verificar Garbage Collector y memoria libre', completed: true }
    ],
    tags: ['Batería', 'Métricas'],
    recurrence: 'daily',
    calendarSynced: false,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: 'task-4',
    title: 'Exportar respaldo completo en PDF y CSV',
    description: 'Generar reporte detallado de tareas completadas e indicadores de efectividad personal.',
    category: 'personal',
    priority: 'low',
    completed: false,
    dueDate: new Date(Date.now() + 172800000).toISOString().split('T')[0],
    dueTime: '12:00',
    reminderTime: '11:45',
    soundAlert: 'synth',
    criticalAlert: false,
    subtasks: [],
    tags: ['Respaldo', 'PDF'],
    recurrence: 'monthly',
    calendarSynced: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

export function getStoredTasks(): Task[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.TASKS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.TASKS, JSON.stringify(defaultInitialTasks));
      return defaultInitialTasks;
    }
    return JSON.parse(raw);
  } catch (e) {
    console.error('Error reading tasks from storage:', e);
    return defaultInitialTasks;
  }
}

export function saveStoredTasks(tasks: Task[]): void {
  try {
    localStorage.setItem(STORAGE_KEYS.TASKS, JSON.stringify(tasks));
  } catch (e) {
    console.error('Error saving tasks to storage:', e);
  }
}

export function getStoredSettings(): UserSettings {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.SETTINGS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(defaultSettings));
      return defaultSettings;
    }
    return { ...defaultSettings, ...JSON.parse(raw) };
  } catch (e) {
    console.error('Error reading settings from storage:', e);
    return defaultSettings;
  }
}

export function saveStoredSettings(settings: UserSettings): void {
  try {
    localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(settings));
  } catch (e) {
    console.error('Error saving settings to storage:', e);
  }
}

export function getStoredSyncQueue(): Task[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.SYNC_QUEUE);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function saveSyncQueue(queue: Task[]): void {
  try {
    localStorage.setItem(STORAGE_KEYS.SYNC_QUEUE, JSON.stringify(queue));
  } catch (e) {
    console.error('Error saving sync queue:', e);
  }
}

export function getStoredSyncLogs(): SyncLog[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.SYNC_LOGS);
    if (!raw) return [
      {
        id: 'log-1',
        timestamp: new Date().toISOString(),
        type: 'cloud_backup',
        status: 'success',
        details: 'Perfil sincronizado con la nube de iTask Cloud'
      }
    ];
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

export function addSyncLog(log: Omit<SyncLog, 'id' | 'timestamp'>): void {
  try {
    const logs = getStoredSyncLogs();
    const newLog: SyncLog = {
      ...log,
      id: 'log-' + Date.now(),
      timestamp: new Date().toISOString(),
    };
    logs.unshift(newLog);
    localStorage.setItem(STORAGE_KEYS.SYNC_LOGS, JSON.stringify(logs.slice(0, 30)));
  } catch (e) {
    console.error('Error adding sync log:', e);
  }
}

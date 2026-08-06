export type Priority = 'low' | 'medium' | 'high' | 'critical';

export type Category = 'personal' | 'work' | 'projects' | 'urgent' | 'health' | 'finance';

export type Recurrence = 'none' | 'daily' | 'weekly' | 'monthly';

export interface Subtask {
  id: string;
  title: string;
  completed: boolean;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  category: Category;
  priority: Priority;
  completed: boolean;
  dueDate: string; // ISO date string
  dueTime?: string; // e.g. "14:30"
  reminderTime?: string; // ISO or minutes before
  soundAlert?: string;
  criticalAlert?: boolean;
  subtasks: Subtask[];
  tags: string[];
  recurrence: Recurrence;
  calendarSynced?: boolean;
  createdAt: string;
  updatedAt: string;
}

export type ThemeColor = 'blue' | 'purple' | 'emerald' | 'rose' | 'amber' | 'midnight';

export type AppIconStyle = 'minimalist' | 'neon' | 'classic' | 'glass' | 'sunset' | 'cyber';

export type Language = 'es' | 'en' | 'fr' | 'de' | 'ja';

export type SoundOption = 'aurora' | 'chime' | 'apex' | 'synth' | 'radar';

export interface UserSettings {
  darkMode: 'dark' | 'light' | 'system';
  accentColor: ThemeColor;
  appIcon: AppIconStyle;
  language: Language;
  biometricEnabled: boolean;
  biometricType: 'faceid' | 'touchid' | 'pin';
  pinCode: string;
  autoLockMinutes: number;
  cloudBackupEnabled: boolean;
  lastCloudBackup?: string;
  offlineModeForce: boolean;
  batterySaverMode: boolean;
  pushNotificationsEnabled: boolean;
  criticalAlertsEnabled: boolean;
  notificationSound: SoundOption;
  quietHoursStart: string;
  quietHoursEnd: string;
  calendarAutoSync: boolean;
}

export interface SyncLog {
  id: string;
  timestamp: string;
  type: 'cloud_backup' | 'calendar_sync' | 'csv_export' | 'pdf_export' | 'offline_queue';
  status: 'success' | 'pending' | 'failed';
  details: string;
}

export interface SystemMetrics {
  batteryLevel: number; // percentage
  isCharging: boolean;
  cpuUsage: number; // percentage
  ramUsageMB: number;
  storageUsageKB: number;
  pendingOfflineSyncCount: number;
  lastSyncTime: string;
}

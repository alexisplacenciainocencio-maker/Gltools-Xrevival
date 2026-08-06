import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Task, UserSettings, Language, Subtask } from './types';
import {
  getStoredTasks,
  saveStoredTasks,
  getStoredSettings,
  saveStoredSettings,
  getStoredSyncQueue,
  saveSyncQueue,
  addSyncLog
} from './utils/storage';
import { Header } from './components/Header';
import { TabBar, TabType } from './components/TabBar';
import { DynamicIsland } from './components/DynamicIsland';
import { BiometricModal } from './components/BiometricModal';
import { TaskModal } from './components/TaskModal';
import { PushNotificationBanner } from './components/PushNotificationBanner';
import { FocusTimerModal } from './components/FocusTimerModal';

import { TasksView } from './views/TasksView';
import { CalendarView } from './views/CalendarView';
import { PerformanceView } from './views/PerformanceView';
import { SettingsView } from './views/SettingsView';

import { themeColorMap } from './utils/theme';
import { playNotificationSound } from './utils/audio';

export default function App() {
  const [tasks, setTasks] = useState<Task[]>(() => getStoredTasks());
  const [settings, setSettings] = useState<UserSettings>(() => getStoredSettings());
  const [syncQueue, setSyncQueue] = useState<Task[]>(() => getStoredSyncQueue());

  const [activeTab, setActiveTab] = useState<TabType>('tasks');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedFilter, setSelectedFilter] = useState('all');

  // Lock state
  const [isLocked, setIsLocked] = useState<boolean>(() => settings.biometricEnabled);

  // Modals & Banners
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [isFocusTimerOpen, setIsFocusTimerOpen] = useState(false);
  const [activeFocusTimerText, setActiveFocusTimerText] = useState<string>('');

  // Online / Offline State
  const [isOnline, setIsOnline] = useState<boolean>(navigator.onLine);
  const [syncing, setSyncing] = useState<boolean>(false);

  // In-App Notification Toast
  const [activeNotification, setActiveNotification] = useState<{ task: Task; message: string } | null>(null);

  // Save tasks on change
  useEffect(() => {
    saveStoredTasks(tasks);
  }, [tasks]);

  // Save settings on change
  useEffect(() => {
    saveStoredSettings(settings);
  }, [settings]);

  // Save sync queue on change
  useEffect(() => {
    saveSyncQueue(syncQueue);
  }, [syncQueue]);

  // Online / Offline Listeners
  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  // Sync queue runner when back online
  useEffect(() => {
    if (isOnline && !settings.offlineModeForce && syncQueue.length > 0) {
      setSyncing(true);
      const timer = setTimeout(() => {
        setSyncQueue([]);
        setSyncing(false);
        addSyncLog({
          type: 'offline_queue',
          status: 'success',
          details: 'Cambios locales transmitidos a la nube'
        });
      }, 1500);
      return () => clearTimeout(timer);
    }
  }, [isOnline, settings.offlineModeForce, syncQueue]);

  // Update Settings Handler
  const handleUpdateSettings = (newSettings: Partial<UserSettings>) => {
    setSettings(prev => ({ ...prev, ...newSettings }));
  };

  // Task Operations
  const handleSaveTask = (taskToSave: Task) => {
    setTasks(prev => {
      const exists = prev.some(t => t.id === taskToSave.id);
      if (exists) {
        return prev.map(t => (t.id === taskToSave.id ? taskToSave : t));
      }
      return [taskToSave, ...prev];
    });

    // If offline, add to sync queue
    if (!isOnline || settings.offlineModeForce) {
      setSyncQueue(prev => [...prev.filter(q => q.id !== taskToSave.id), taskToSave]);
    }

    addSyncLog({
      type: 'calendar_sync',
      status: 'success',
      details: `Tarea guardada: "${taskToSave.title}"`
    });

    playNotificationSound('chime');
  };

  const handleToggleTaskComplete = (id: string) => {
    setTasks(prev =>
      prev.map(t => {
        if (t.id === id) {
          const updated = { ...t, completed: !t.completed, updatedAt: new Date().toISOString() };
          if (!isOnline || settings.offlineModeForce) {
            setSyncQueue(q => [...q.filter(item => item.id !== id), updated]);
          }
          return updated;
        }
        return t;
      })
    );
  };

  const handleDeleteTask = (id: string) => {
    setTasks(prev => prev.filter(t => t.id !== id));
    setSyncQueue(prev => prev.filter(t => t.id !== id));
    playNotificationSound('radar');
  };

  const handleToggleSubtask = (taskId: string, subtaskId: string) => {
    setTasks(prev =>
      prev.map(t => {
        if (t.id === taskId) {
          const updatedSubtasks = t.subtasks.map(st =>
            st.id === subtaskId ? { ...st, completed: !st.completed } : st
          );
          return { ...t, subtasks: updatedSubtasks, updatedAt: new Date().toISOString() };
        }
        return t;
      })
    );
  };

  const handleImportTasks = (importedTasks: Partial<Task>[]) => {
    const fullTasks: Task[] = importedTasks.map((imp, idx) => ({
      id: `imported-${Date.now()}-${idx}`,
      title: imp.title || 'Tarea Importada',
      description: imp.description || '',
      category: imp.category || 'work',
      priority: imp.priority || 'medium',
      completed: false,
      dueDate: imp.dueDate || new Date().toISOString().split('T')[0],
      dueTime: imp.dueTime || '12:00',
      reminderTime: imp.dueTime || '12:00',
      soundAlert: 'chime',
      criticalAlert: false,
      subtasks: [],
      tags: ['iCal', 'Importado'],
      recurrence: 'none',
      calendarSynced: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }));

    setTasks(prev => [...fullTasks, ...prev]);
    playNotificationSound('chime');
  };

  const handleTriggerTestNotification = () => {
    if (tasks.length > 0) {
      const sample = tasks[0];
      setActiveNotification({
        task: sample,
        message: '¡Notificación de prueba enviada desde iTask Pro iOS!'
      });
      playNotificationSound(sample.soundAlert || 'aurora');
    } else {
      const dummyTask: Task = {
        id: 'dummy',
        title: 'Reunión de seguimiento iOS 18',
        category: 'work',
        priority: 'high',
        completed: false,
        dueDate: new Date().toISOString().split('T')[0],
        dueTime: '14:00',
        subtasks: [],
        tags: [],
        recurrence: 'none',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      setActiveNotification({
        task: dummyTask,
        message: '¡Alerta de prueba configurada para recordatorios críticos!'
      });
      playNotificationSound('aurora');
    }
  };

  const handleTriggerCloudBackup = () => {
    setSettings(prev => ({ ...prev, lastCloudBackup: new Date().toISOString() }));
    addSyncLog({
      type: 'cloud_backup',
      status: 'success',
      details: 'Perfil y tareas respaldados en iTask Cloud'
    });
  };

  const pendingCount = tasks.filter(t => !t.completed).length;

  return (
    <div className={`min-h-screen font-sans bg-slate-950 text-slate-100 antialiased selection:bg-blue-500 selection:text-white transition-colors`}>
      {/* Dynamic Island Banner */}
      <DynamicIsland
        settings={settings}
        isOnline={isOnline}
        syncing={syncing}
        syncQueueCount={syncQueue.length}
        activeFocusTimer={activeFocusTimerText}
      />

      {/* Push Notification Toast Banner */}
      <PushNotificationBanner
        notification={activeNotification}
        onClose={() => setActiveNotification(null)}
        onActionClick={() => {
          setActiveTab('tasks');
          setActiveNotification(null);
        }}
      />

      {/* Biometric Unlock Modal */}
      <BiometricModal
        settings={settings}
        isLocked={isLocked}
        onUnlock={() => setIsLocked(false)}
      />

      {/* Focus Timer Modal */}
      <FocusTimerModal
        isOpen={isFocusTimerOpen}
        onClose={() => {
          setIsFocusTimerOpen(false);
          setActiveFocusTimerText('');
        }}
        onTimerTick={(timeFormatted) => setActiveFocusTimerText(timeFormatted)}
      />

      {/* Create / Edit Task Modal */}
      <TaskModal
        isOpen={isTaskModalOpen}
        onClose={() => {
          setIsTaskModalOpen(false);
          setEditingTask(null);
        }}
        onSave={handleSaveTask}
        initialTask={editingTask}
        settings={settings}
      />

      {/* App Main Layout Container */}
      {!isLocked && (
        <div className="max-w-4xl mx-auto min-h-screen flex flex-col relative">
          {/* Top Glass Header */}
          <Header
            settings={settings}
            onUpdateSettings={handleUpdateSettings}
            onLockApp={() => setIsLocked(true)}
            onOpenNewTask={() => {
              setEditingTask(null);
              setIsTaskModalOpen(true);
            }}
            onOpenFocusTimer={() => setIsFocusTimerOpen(true)}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            selectedFilter={selectedFilter}
            onFilterChange={setSelectedFilter}
          />

          {/* Tab Views */}
          <main className="flex-1 px-4 pt-4">
            <AnimatePresence mode="wait">
              {activeTab === 'tasks' && (
                <motion.div
                  key="tasks"
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 10 }}
                  transition={{ duration: 0.15 }}
                >
                  <TasksView
                    tasks={tasks}
                    settings={settings}
                    searchQuery={searchQuery}
                    selectedFilter={selectedFilter}
                    onToggleComplete={handleToggleTaskComplete}
                    onDelete={handleDeleteTask}
                    onEdit={(task) => {
                      setEditingTask(task);
                      setIsTaskModalOpen(true);
                    }}
                    onToggleSubtask={handleToggleSubtask}
                    onOpenNewTask={() => {
                      setEditingTask(null);
                      setIsTaskModalOpen(true);
                    }}
                  />
                </motion.div>
              )}

              {activeTab === 'calendar' && (
                <motion.div
                  key="calendar"
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 10 }}
                  transition={{ duration: 0.15 }}
                >
                  <CalendarView
                    tasks={tasks}
                    settings={settings}
                    onImportTasks={handleImportTasks}
                  />
                </motion.div>
              )}

              {activeTab === 'performance' && (
                <motion.div
                  key="performance"
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 10 }}
                  transition={{ duration: 0.15 }}
                >
                  <PerformanceView
                    settings={settings}
                    onUpdateSettings={handleUpdateSettings}
                    syncQueue={syncQueue}
                    onClearSyncQueue={() => setSyncQueue([])}
                  />
                </motion.div>
              )}

              {activeTab === 'settings' && (
                <motion.div
                  key="settings"
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 10 }}
                  transition={{ duration: 0.15 }}
                >
                  <SettingsView
                    settings={settings}
                    onUpdateSettings={handleUpdateSettings}
                    tasks={tasks}
                    onTriggerTestNotification={handleTriggerTestNotification}
                    onTriggerCloudBackup={handleTriggerCloudBackup}
                    syncLogsCount={0}
                  />
                </motion.div>
              )}
            </AnimatePresence>
          </main>

          {/* Bottom iOS Navigation Bar */}
          <TabBar
            activeTab={activeTab}
            onTabChange={setActiveTab}
            settings={settings}
            pendingCount={pendingCount}
          />
        </div>
      )}
    </div>
  );
}

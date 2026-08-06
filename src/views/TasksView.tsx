import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { LayoutGrid, List, CheckCircle2, AlertCircle, Sparkles, Filter } from 'lucide-react';
import { Task, UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { TaskCard } from '../components/TaskCard';
import { themeColorMap } from '../utils/theme';

interface TasksViewProps {
  tasks: Task[];
  settings: UserSettings;
  searchQuery: string;
  selectedFilter: string;
  onToggleComplete: (id: string) => void;
  onDelete: (id: string) => void;
  onEdit: (task: Task) => void;
  onToggleSubtask: (taskId: string, subtaskId: string) => void;
  onOpenNewTask: () => void;
}

export const TasksView: React.FC<TasksViewProps> = ({
  tasks,
  settings,
  searchQuery,
  selectedFilter,
  onToggleComplete,
  onDelete,
  onEdit,
  onToggleSubtask,
  onOpenNewTask
}) => {
  const [layoutMode, setLayoutMode] = useState<'list' | 'grid'>('list');
  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const todayStr = new Date().toISOString().split('T')[0];

  // Filter tasks
  const filteredTasks = tasks.filter(task => {
    // Search query
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      const matchTitle = task.title.toLowerCase().includes(q);
      const matchDesc = (task.description || '').toLowerCase().includes(q);
      const matchTag = task.tags.some(t => t.toLowerCase().includes(q));
      if (!matchTitle && !matchDesc && !matchTag) return false;
    }

    // Filter pill
    if (selectedFilter === 'today') {
      return task.dueDate === todayStr;
    }
    if (selectedFilter === 'upcoming') {
      return task.dueDate > todayStr && !task.completed;
    }
    if (selectedFilter === 'priority') {
      return task.priority === 'high' || task.priority === 'critical';
    }
    if (selectedFilter === 'completed') {
      return task.completed;
    }

    return true;
  });

  const pendingTasks = filteredTasks.filter(t => !t.completed);
  const completedTasks = filteredTasks.filter(t => t.completed);

  return (
    <div className="space-y-4 pb-24">
      {/* View Header Info */}
      <div className="flex items-center justify-between px-1">
        <div>
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
            {filteredTasks.length} {filteredTasks.length === 1 ? 'Recordatorio' : 'Recordatorios'}
          </span>
          <p className="text-xs text-slate-500">
            {pendingTasks.length} pendientes • {completedTasks.length} completadas
          </p>
        </div>

        {/* Layout Switcher */}
        <div className="flex items-center gap-1 bg-slate-900/80 p-1 rounded-xl border border-slate-800">
          <button
            onClick={() => setLayoutMode('list')}
            className={`p-1.5 rounded-lg transition-all ${
              layoutMode === 'list' ? `${theme.primary} text-white` : 'text-slate-400 hover:text-white'
            }`}
          >
            <List className="w-4 h-4" />
          </button>
          <button
            onClick={() => setLayoutMode('grid')}
            className={`p-1.5 rounded-lg transition-all ${
              layoutMode === 'grid' ? `${theme.primary} text-white` : 'text-slate-400 hover:text-white'
            }`}
          >
            <LayoutGrid className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Empty State */}
      {filteredTasks.length === 0 && (
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="py-16 px-6 text-center bg-slate-900/40 border border-slate-800/80 rounded-3xl flex flex-col items-center justify-center max-w-md mx-auto"
        >
          <div className="w-16 h-16 rounded-3xl bg-blue-500/10 border border-blue-500/20 text-blue-400 flex items-center justify-center mb-4">
            <Sparkles className="w-8 h-8 animate-pulse" />
          </div>
          <h3 className="text-base font-bold text-slate-200">
            {getTranslation(lang, 'noTasks')}
          </h3>
          <p className="text-xs text-slate-400 mt-1 max-w-xs leading-relaxed">
            {getTranslation(lang, 'noTasksSub')}
          </p>
          <button
            onClick={onOpenNewTask}
            className={`mt-6 px-5 py-2.5 rounded-xl ${theme.primary} text-white font-bold text-xs shadow-lg hover:brightness-110 active:scale-95 transition-all`}
          >
            + {getTranslation(lang, 'newTask')}
          </button>
        </motion.div>
      )}

      {/* Pending Tasks Section */}
      {pendingTasks.length > 0 && (
        <div className="space-y-3">
          <h2 className="text-xs font-bold text-slate-400 uppercase tracking-wider px-1 flex items-center gap-1.5">
            <AlertCircle className="w-3.5 h-3.5 text-amber-400" />
            Pendientes ({pendingTasks.length})
          </h2>
          <div className={layoutMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 gap-3' : 'space-y-3'}>
            <AnimatePresence mode="popLayout">
              {pendingTasks.map((t) => (
                <TaskCard
                  key={t.id}
                  task={t}
                  settings={settings}
                  onToggleComplete={onToggleComplete}
                  onDelete={onDelete}
                  onEdit={onEdit}
                  onToggleSubtask={onToggleSubtask}
                />
              ))}
            </AnimatePresence>
          </div>
        </div>
      )}

      {/* Completed Tasks Section */}
      {completedTasks.length > 0 && (
        <div className="space-y-3 pt-4 border-t border-slate-800/80">
          <h2 className="text-xs font-bold text-slate-400 uppercase tracking-wider px-1 flex items-center gap-1.5">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            Completadas ({completedTasks.length})
          </h2>
          <div className={layoutMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 gap-3' : 'space-y-3'}>
            <AnimatePresence mode="popLayout">
              {completedTasks.map((t) => (
                <TaskCard
                  key={t.id}
                  task={t}
                  settings={settings}
                  onToggleComplete={onToggleComplete}
                  onDelete={onDelete}
                  onEdit={onEdit}
                  onToggleSubtask={onToggleSubtask}
                />
              ))}
            </AnimatePresence>
          </div>
        </div>
      )}
    </div>
  );
};

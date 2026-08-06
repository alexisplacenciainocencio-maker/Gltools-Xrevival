import React, { useState } from 'react';
import { motion } from 'motion/react';
import { Check, Calendar, Bell, AlertTriangle, Trash2, Edit3, Tag, CalendarCheck2, ListTodo } from 'lucide-react';
import { Task, UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { themeColorMap } from '../utils/theme';
import { playNotificationSound } from '../utils/audio';

interface TaskCardProps {
  task: Task;
  settings: UserSettings;
  onToggleComplete: (id: string) => void;
  onDelete: (id: string) => void;
  onEdit: (task: Task) => void;
  onToggleSubtask: (taskId: string, subtaskId: string) => void;
}

export const TaskCard: React.FC<TaskCardProps> = ({
  task,
  settings,
  onToggleComplete,
  onDelete,
  onEdit,
  onToggleSubtask
}) => {
  const [showSubtasks, setShowSubtasks] = useState(false);
  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const handleCheckboxClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onToggleComplete(task.id);
    if (!task.completed) {
      playNotificationSound('chime');
    }
  };

  const completedSubtasksCount = task.subtasks.filter(s => s.completed).length;
  const totalSubtasksCount = task.subtasks.length;
  const subtasksPercent = totalSubtasksCount > 0 ? Math.round((completedSubtasksCount / totalSubtasksCount) * 100) : 0;

  const priorityColors = {
    critical: 'bg-rose-500/20 text-rose-400 border-rose-500/30',
    high: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
    medium: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
    low: 'bg-slate-500/20 text-slate-400 border-slate-500/30'
  };

  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.95 }}
      transition={{ duration: 0.2 }}
      className={`relative group bg-slate-900/70 dark:bg-slate-900/90 backdrop-blur-xl border rounded-2xl p-4 shadow-md transition-all hover:border-slate-700/80 ${
        task.completed ? 'opacity-65 border-slate-800' : 'border-slate-800/80'
      }`}
    >
      <div className="flex items-start gap-3">
        {/* Custom iOS Checkbox */}
        <button
          onClick={handleCheckboxClick}
          className={`mt-0.5 w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all ${
            task.completed
              ? `${theme.primary} border-transparent text-white`
              : 'border-slate-600 hover:border-blue-400 bg-slate-800/50'
          }`}
        >
          {task.completed && <Check className="w-3.5 h-3.5 stroke-[3]" />}
        </button>

        {/* Content Section */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <h3
              className={`text-sm font-bold tracking-tight text-slate-100 leading-snug cursor-pointer hover:text-blue-400 transition-colors ${
                task.completed ? 'line-through text-slate-400' : ''
              }`}
              onClick={() => onEdit(task)}
            >
              {task.title}
            </h3>

            {/* Quick Actions */}
            <div className="flex items-center gap-1 opacity-80 group-hover:opacity-100 transition-opacity">
              <button
                onClick={() => onEdit(task)}
                title={getTranslation(lang, 'editTask')}
                className="p-1 rounded-lg text-slate-400 hover:text-blue-400 hover:bg-slate-800 transition-all"
              >
                <Edit3 className="w-3.5 h-3.5" />
              </button>
              <button
                onClick={() => onDelete(task.id)}
                title={getTranslation(lang, 'deleteTask')}
                className="p-1 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-slate-800 transition-all"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>

          {/* Description */}
          {task.description && (
            <p className="text-xs text-slate-400 mt-1 line-clamp-2 leading-relaxed">
              {task.description}
            </p>
          )}

          {/* Meta Tags & Badges */}
          <div className="flex flex-wrap items-center gap-2 mt-3 text-[10px] font-medium text-slate-400">
            {/* Category Pill */}
            <span className="px-2 py-0.5 rounded-full bg-slate-800 border border-slate-700/80 text-slate-300">
              {getTranslation(lang, `cat_${task.category}`)}
            </span>

            {/* Priority Badge */}
            <span className={`px-2 py-0.5 rounded-full border flex items-center gap-1 ${priorityColors[task.priority]}`}>
              {task.priority === 'critical' && <AlertTriangle className="w-3 h-3 text-rose-400" />}
              {getTranslation(lang, `prio_${task.priority}`)}
            </span>

            {/* Due Date & Time */}
            <div className="flex items-center gap-1 text-slate-400">
              <Calendar className="w-3 h-3" />
              <span>{task.dueDate} {task.dueTime ? `@ ${task.dueTime}` : ''}</span>
            </div>

            {/* Calendar Synced Badge */}
            {task.calendarSynced && (
              <span className="flex items-center gap-1 text-emerald-400 bg-emerald-500/10 px-1.5 py-0.5 rounded-full border border-emerald-500/20">
                <CalendarCheck2 className="w-3 h-3" />
                <span>Sync</span>
              </span>
            )}

            {/* Tags */}
            {task.tags.map((tag, i) => (
              <span key={i} className="flex items-center gap-0.5 text-slate-400 bg-slate-800/40 px-1.5 py-0.5 rounded-md">
                <Tag className="w-2.5 h-2.5" />
                {tag}
              </span>
            ))}
          </div>

          {/* Subtasks Section Toggle */}
          {totalSubtasksCount > 0 && (
            <div className="mt-3 pt-2 border-t border-slate-800/60">
              <div className="flex items-center justify-between text-[11px] mb-1">
                <button
                  onClick={() => setShowSubtasks(!showSubtasks)}
                  className="flex items-center gap-1 text-slate-400 hover:text-blue-400 font-medium transition-colors"
                >
                  <ListTodo className="w-3.5 h-3.5" />
                  <span>
                    {getTranslation(lang, 'subtasks')} ({completedSubtasksCount}/{totalSubtasksCount})
                  </span>
                </button>
                <span className="text-slate-500 font-semibold">{subtasksPercent}%</span>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: `${subtasksPercent}%` }}
                  className={`h-full ${theme.primary}`}
                />
              </div>

              {/* Expanded Subtask List */}
              {showSubtasks && (
                <div className="mt-2 space-y-1.5 pl-1">
                  {task.subtasks.map((sub) => (
                    <div
                      key={sub.id}
                      onClick={() => onToggleSubtask(task.id, sub.id)}
                      className="flex items-center gap-2 cursor-pointer group/sub"
                    >
                      <div
                        className={`w-3.5 h-3.5 rounded border flex items-center justify-center transition-colors ${
                          sub.completed
                            ? `${theme.primary} border-transparent text-white`
                            : 'border-slate-600 bg-slate-800'
                        }`}
                      >
                        {sub.completed && <Check className="w-2.5 h-2.5 stroke-[3]" />}
                      </div>
                      <span
                        className={`text-xs ${
                          sub.completed ? 'line-through text-slate-500' : 'text-slate-300'
                        }`}
                      >
                        {sub.title}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </motion.div>
  );
};

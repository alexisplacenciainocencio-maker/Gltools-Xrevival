import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { X, Plus, Trash2, Calendar, Clock, Bell, AlertTriangle, Tag, Volume2, CalendarCheck2 } from 'lucide-react';
import { Task, Category, Priority, Recurrence, SoundOption, UserSettings, Language, Subtask } from '../types';
import { getTranslation } from '../translations';
import { themeColorMap } from '../utils/theme';
import { playNotificationSound } from '../utils/audio';

interface TaskModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (task: Task) => void;
  initialTask?: Task | null;
  settings: UserSettings;
}

export const TaskModal: React.FC<TaskModalProps> = ({
  isOpen,
  onClose,
  onSave,
  initialTask,
  settings
}) => {
  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState<Category>('work');
  const [priority, setPriority] = useState<Priority>('medium');
  const [dueDate, setDueDate] = useState(new Date().toISOString().split('T')[0]);
  const [dueTime, setDueTime] = useState('12:00');
  const [recurrence, setRecurrence] = useState<Recurrence>('none');
  const [soundAlert, setSoundAlert] = useState<SoundOption>('aurora');
  const [criticalAlert, setCriticalAlert] = useState(false);
  const [calendarSynced, setCalendarSynced] = useState(true);
  const [subtasks, setSubtasks] = useState<Subtask[]>([]);
  const [newSubtaskTitle, setNewSubtaskTitle] = useState('');
  const [tagsInput, setTagsInput] = useState('');

  useEffect(() => {
    if (initialTask) {
      setTitle(initialTask.title);
      setDescription(initialTask.description || '');
      setCategory(initialTask.category);
      setPriority(initialTask.priority);
      setDueDate(initialTask.dueDate);
      setDueTime(initialTask.dueTime || '12:00');
      setRecurrence(initialTask.recurrence);
      setSoundAlert(initialTask.soundAlert || 'aurora');
      setCriticalAlert(initialTask.criticalAlert || false);
      setCalendarSynced(initialTask.calendarSynced !== false);
      setSubtasks(initialTask.subtasks || []);
      setTagsInput((initialTask.tags || []).join(', '));
    } else {
      setTitle('');
      setDescription('');
      setCategory('work');
      setPriority('medium');
      setDueDate(new Date().toISOString().split('T')[0]);
      setDueTime('12:00');
      setRecurrence('none');
      setSoundAlert('aurora');
      setCriticalAlert(false);
      setCalendarSynced(true);
      setSubtasks([]);
      setTagsInput('');
    }
  }, [initialTask, isOpen]);

  const handleAddSubtask = () => {
    if (!newSubtaskTitle.trim()) return;
    setSubtasks(prev => [
      ...prev,
      { id: 'sub-' + Date.now(), title: newSubtaskTitle.trim(), completed: false }
    ]);
    setNewSubtaskTitle('');
  };

  const handleRemoveSubtask = (id: string) => {
    setSubtasks(prev => prev.filter(s => s.id !== id));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    const tagsArr = tagsInput
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0);

    const taskToSave: Task = {
      id: initialTask ? initialTask.id : 'task-' + Date.now(),
      title: title.trim(),
      description: description.trim(),
      category,
      priority,
      completed: initialTask ? initialTask.completed : false,
      dueDate,
      dueTime,
      reminderTime: dueTime,
      soundAlert,
      criticalAlert,
      subtasks,
      tags: tagsArr,
      recurrence,
      calendarSynced,
      createdAt: initialTask ? initialTask.createdAt : new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    onSave(taskToSave);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xl flex items-center justify-center p-4 overflow-y-auto">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 20 }}
          className="w-full max-w-lg bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden my-8"
        >
          {/* Header */}
          <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Calendar className="w-5 h-5 text-blue-400" />
              {initialTask ? getTranslation(lang, 'editTask') : getTranslation(lang, 'newTask')}
            </h2>
            <button
              onClick={onClose}
              className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="p-6 space-y-4 text-xs text-slate-200">
            {/* Title */}
            <div>
              <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                TÍTULO DE LA TAREA *
              </label>
              <input
                type="text"
                required
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Ej. Revisión de entregable y sincronización..."
                className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2.5 text-sm text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50"
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                DESCRIPCIÓN / NOTAS DETALLADAS
              </label>
              <textarea
                rows={2}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Añade contexto adicional, enlaces o notas..."
                className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 resize-none"
              />
            </div>

            {/* Grid Category & Priority */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  CATEGORÍA
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as Category)}
                  className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50"
                >
                  <option value="work">{getTranslation(lang, 'cat_work')}</option>
                  <option value="personal">{getTranslation(lang, 'cat_personal')}</option>
                  <option value="projects">{getTranslation(lang, 'cat_projects')}</option>
                  <option value="urgent">{getTranslation(lang, 'cat_urgent')}</option>
                  <option value="health">{getTranslation(lang, 'cat_health')}</option>
                  <option value="finance">{getTranslation(lang, 'cat_finance')}</option>
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  PRIORIDAD
                </label>
                <select
                  value={priority}
                  onChange={(e) => setPriority(e.target.value as Priority)}
                  className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50"
                >
                  <option value="low">{getTranslation(lang, 'prio_low')}</option>
                  <option value="medium">{getTranslation(lang, 'prio_medium')}</option>
                  <option value="high">{getTranslation(lang, 'prio_high')}</option>
                  <option value="critical">{getTranslation(lang, 'prio_critical')}</option>
                </select>
              </div>
            </div>

            {/* Grid Date & Time */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  FECHA LÍMITE
                </label>
                <input
                  type="date"
                  value={dueDate}
                  onChange={(e) => setDueDate(e.target.value)}
                  className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  HORA DE ALERTA
                </label>
                <input
                  type="time"
                  value={dueTime}
                  onChange={(e) => setDueTime(e.target.value)}
                  className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50"
                />
              </div>
            </div>

            {/* Sound Alert & Recurrence */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  SONIDO DE NOTIFICACIÓN
                </label>
                <div className="flex gap-2 items-center">
                  <select
                    value={soundAlert}
                    onChange={(e) => {
                      const sound = e.target.value as SoundOption;
                      setSoundAlert(sound);
                      playNotificationSound(sound);
                    }}
                    className="flex-1 bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none"
                  >
                    <option value="aurora">Aurora (iOS)</option>
                    <option value="chime">Chime</option>
                    <option value="apex">Apex Synth</option>
                    <option value="synth">Synth Wave</option>
                    <option value="radar">Radar Pulse</option>
                  </select>
                  <button
                    type="button"
                    onClick={() => playNotificationSound(soundAlert)}
                    className="p-2 bg-slate-800 hover:bg-slate-700 rounded-xl text-blue-400"
                  >
                    <Volume2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                  RECURRENCIA
                </label>
                <select
                  value={recurrence}
                  onChange={(e) => setRecurrence(e.target.value as Recurrence)}
                  className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none"
                >
                  <option value="none">{getTranslation(lang, 'none')}</option>
                  <option value="daily">{getTranslation(lang, 'daily')}</option>
                  <option value="weekly">{getTranslation(lang, 'weekly')}</option>
                  <option value="monthly">{getTranslation(lang, 'monthly')}</option>
                </select>
              </div>
            </div>

            {/* Subtasks Builder */}
            <div>
              <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                SUBTAREAS (LISTA DE VERIFICACIÓN)
              </label>
              <div className="flex gap-2 mb-2">
                <input
                  type="text"
                  value={newSubtaskTitle}
                  onChange={(e) => setNewSubtaskTitle(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleAddSubtask();
                    }
                  }}
                  placeholder="Añadir paso o subtarea..."
                  className="flex-1 bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-1.5 text-xs text-white focus:outline-none"
                />
                <button
                  type="button"
                  onClick={handleAddSubtask}
                  className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 rounded-xl text-xs font-semibold text-blue-400"
                >
                  +
                </button>
              </div>

              {subtasks.length > 0 && (
                <div className="space-y-1.5 max-h-32 overflow-y-auto pr-1">
                  {subtasks.map((st) => (
                    <div key={st.id} className="flex items-center justify-between bg-slate-800/40 px-3 py-1.5 rounded-lg border border-slate-800">
                      <span className="text-xs text-slate-300">{st.title}</span>
                      <button
                        type="button"
                        onClick={() => handleRemoveSubtask(st.id)}
                        className="text-slate-500 hover:text-rose-400"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Tags Input */}
            <div>
              <label className="block text-[11px] font-semibold text-slate-400 mb-1">
                ETIQUETAS (SEPARADAS POR COMAS)
              </label>
              <input
                type="text"
                value={tagsInput}
                onChange={(e) => setTagsInput(e.target.value)}
                placeholder="iOS, Trabajo, Urgente..."
                className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-white focus:outline-none"
              />
            </div>

            {/* Checkbox Options */}
            <div className="pt-2 border-t border-slate-800 space-y-2">
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={calendarSynced}
                  onChange={(e) => setCalendarSynced(e.target.checked)}
                  className="rounded bg-slate-800 border-slate-700 text-blue-500 focus:ring-0"
                />
                <CalendarCheck2 className="w-4 h-4 text-emerald-400" />
                <span className="text-xs text-slate-300">
                  Sincronizar automáticamente con Calendarios Externos (.ICS / iCal)
                </span>
              </label>

              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={criticalAlert}
                  onChange={(e) => setCriticalAlert(e.target.checked)}
                  className="rounded bg-slate-800 border-slate-700 text-rose-500 focus:ring-0"
                />
                <AlertTriangle className="w-4 h-4 text-rose-400" />
                <span className="text-xs text-slate-300">
                  Activar Alerta Crítica (Ignorar Modo Silencioso)
                </span>
              </label>
            </div>

            {/* Submit Action */}
            <div className="pt-4 flex justify-end gap-3">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold"
              >
                {getTranslation(lang, 'cancel')}
              </button>
              <button
                type="submit"
                className={`px-5 py-2 rounded-xl ${theme.primary} text-white text-xs font-bold shadow-lg hover:brightness-110`}
              >
                {getTranslation(lang, 'save')}
              </button>
            </div>
          </form>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};

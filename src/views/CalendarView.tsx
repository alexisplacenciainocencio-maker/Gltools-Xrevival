import React, { useState } from 'react';
import { motion } from 'motion/react';
import { Calendar, Download, Upload, Copy, ExternalLink, Check, RefreshCw, CalendarCheck, Sparkles } from 'lucide-react';
import { Task, UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { downloadICSFile, parseICSContent } from '../utils/exporter';
import { themeColorMap } from '../utils/theme';
import { playNotificationSound } from '../utils/audio';

interface CalendarViewProps {
  tasks: Task[];
  settings: UserSettings;
  onImportTasks: (newTasks: Partial<Task>[]) => void;
}

export const CalendarView: React.FC<CalendarViewProps> = ({
  tasks,
  settings,
  onImportTasks
}) => {
  const [copiedFeed, setCopiedFeed] = useState(false);
  const [syncingNow, setSyncingNow] = useState(false);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  // Calendar Feed URL
  const calFeedUrl = `https://itaskpro.app/api/calendar/feed.ics?token=usr_${Math.abs(settings.pinCode.length * 999888)}`;

  const handleCopyFeed = () => {
    navigator.clipboard.writeText(calFeedUrl);
    setCopiedFeed(true);
    playNotificationSound('chime');
    setTimeout(() => setCopiedFeed(false), 2000);
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        const content = event.target?.result as string;
        if (content) {
          const imported = parseICSContent(content);
          if (imported.length > 0) {
            onImportTasks(imported);
            playNotificationSound('chime');
          }
        }
      };
      reader.readAsText(file);
    }
  };

  const handleSimulateCalendarSync = () => {
    setSyncingNow(true);
    setTimeout(() => {
      setSyncingNow(false);
      playNotificationSound('chime');
    }, 1500);
  };

  // Build current month calendar dates
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const firstDayOfWeek = new Date(year, month, 1).getDay();

  const daysArray = Array.from({ length: daysInMonth }, (_, i) => {
    const d = i + 1;
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    const dayTasks = tasks.filter(t => t.dueDate === dateStr);
    return { day: d, dateStr, tasks: dayTasks };
  });

  const selectedDayTasks = tasks.filter(t => t.dueDate === selectedDate);

  return (
    <div className="space-y-6 pb-24 text-slate-100">
      {/* Top Banner */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl relative overflow-hidden">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <span className="text-[10px] font-bold text-blue-400 uppercase tracking-widest flex items-center gap-1">
              <CalendarCheck className="w-3.5 h-3.5" />
              Sincronización Automática
            </span>
            <h2 className="text-lg font-bold mt-1">
              {getTranslation(lang, 'calendarTitle')}
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">
              Conecta tus recordatorios con Apple Calendar, Google Calendar y Outlook vía .ICS
            </p>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={handleSimulateCalendarSync}
              disabled={syncingNow}
              className={`px-4 py-2 rounded-xl ${theme.primary} text-white font-bold text-xs flex items-center gap-2 shadow-lg active:scale-95 transition-all`}
            >
              <RefreshCw className={`w-3.5 h-3.5 ${syncingNow ? 'animate-spin' : ''}`} />
              <span>{syncingNow ? getTranslation(lang, 'syncingCalendar') : 'Sincronizar'}</span>
            </button>
          </div>
        </div>

        {/* Sync Feed Box */}
        <div className="mt-4 pt-4 border-t border-slate-800/80 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
          <div className="flex-1 bg-slate-950/80 border border-slate-800 rounded-xl px-3 py-2 text-xs font-mono text-slate-300 truncate">
            {calFeedUrl}
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleCopyFeed}
              className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-200 flex items-center gap-1.5 border border-slate-700"
            >
              {copiedFeed ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5 text-blue-400" />}
              <span>{copiedFeed ? getTranslation(lang, 'copiedUrl') : 'Copiar URL iCal'}</span>
            </button>
            <button
              onClick={() => downloadICSFile(tasks)}
              className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-emerald-400 flex items-center gap-1.5 border border-slate-700"
            >
              <Download className="w-3.5 h-3.5" />
              <span>{getTranslation(lang, 'exportIcs')}</span>
            </button>
            <label className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-purple-400 flex items-center gap-1.5 border border-slate-700 cursor-pointer">
              <Upload className="w-3.5 h-3.5" />
              <span>{getTranslation(lang, 'importIcs')}</span>
              <input type="file" accept=".ics" onChange={handleFileUpload} className="hidden" />
            </label>
          </div>
        </div>
      </div>

      {/* Interactive Calendar Month Grid */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-sm font-bold text-slate-200">
            {new Date().toLocaleDateString('es-ES', { month: 'long', year: 'numeric' }).toUpperCase()}
          </h3>
          <span className="text-xs text-slate-400 font-medium">
            Toca un día para ver sus eventos
          </span>
        </div>

        {/* Days Header */}
        <div className="grid grid-cols-7 gap-1 text-center text-[10px] font-bold text-slate-400 uppercase mb-2">
          <span>Dom</span><span>Lun</span><span>Mar</span><span>Mié</span><span>Jue</span><span>Vie</span><span>Sáb</span>
        </div>

        {/* Days Grid */}
        <div className="grid grid-cols-7 gap-1.5">
          {Array.from({ length: firstDayOfWeek }).map((_, i) => (
            <div key={`empty-${i}`} className="h-10 rounded-xl bg-slate-950/20" />
          ))}

          {daysArray.map((item) => {
            const isSelected = selectedDate === item.dateStr;
            const isToday = new Date().toISOString().split('T')[0] === item.dateStr;
            const hasTasks = item.tasks.length > 0;

            return (
              <button
                key={item.day}
                onClick={() => setSelectedDate(item.dateStr)}
                className={`h-12 rounded-xl flex flex-col items-center justify-center relative p-1 transition-all ${
                  isSelected
                    ? `${theme.primary} text-white shadow-lg`
                    : isToday
                    ? 'bg-blue-600/20 border border-blue-500/50 text-blue-300'
                    : 'bg-slate-800/40 hover:bg-slate-800 border border-slate-800 text-slate-300'
                }`}
              >
                <span className="text-xs font-bold">{item.day}</span>
                {hasTasks && (
                  <div className="flex gap-0.5 mt-1">
                    {item.tasks.slice(0, 3).map((t, idx) => (
                      <span
                        key={idx}
                        className={`w-1.5 h-1.5 rounded-full ${
                          t.priority === 'critical' ? 'bg-rose-400' : 'bg-emerald-400'
                        }`}
                      />
                    ))}
                  </div>
                )}
              </button>
            );
          })}
        </div>
      </div>

      {/* Selected Day Agenda */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3 flex items-center gap-2">
          <Calendar className="w-4 h-4 text-blue-400" />
          Agenda para el {selectedDate} ({selectedDayTasks.length} eventos)
        </h3>

        {selectedDayTasks.length === 0 ? (
          <p className="text-xs text-slate-500 py-4 text-center">
            Sin tareas programadas para esta fecha.
          </p>
        ) : (
          <div className="space-y-2">
            {selectedDayTasks.map((t) => (
              <div
                key={t.id}
                className="bg-slate-800/60 border border-slate-700/60 rounded-2xl p-3 flex items-center justify-between"
              >
                <div>
                  <h4 className="text-xs font-bold text-slate-100">{t.title}</h4>
                  <p className="text-[11px] text-slate-400">{t.dueTime || 'Sin hora específica'} • Prioridad: {t.priority}</p>
                </div>
                <span className={`text-[10px] px-2 py-0.5 rounded-full ${t.completed ? 'bg-emerald-500/20 text-emerald-400' : 'bg-amber-500/20 text-amber-400'}`}>
                  {t.completed ? 'Completada' : 'Pendiente'}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

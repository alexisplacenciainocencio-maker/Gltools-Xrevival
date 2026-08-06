import React from 'react';
import { Search, Lock, Zap, Plus, Timer } from 'lucide-react';
import { UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { themeColorMap } from '../utils/theme';

interface HeaderProps {
  settings: UserSettings;
  onUpdateSettings: (newSettings: Partial<UserSettings>) => void;
  onLockApp: () => void;
  onOpenNewTask: () => void;
  onOpenFocusTimer: () => void;
  searchQuery: string;
  onSearchChange: (query: string) => void;
  selectedFilter: string;
  onFilterChange: (filter: string) => void;
}

export const Header: React.FC<HeaderProps> = ({
  settings,
  onUpdateSettings,
  onLockApp,
  onOpenNewTask,
  onOpenFocusTimer,
  searchQuery,
  onSearchChange,
  selectedFilter,
  onFilterChange
}) => {
  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const filterOptions = [
    { id: 'all', labelKey: 'all' },
    { id: 'today', labelKey: 'today' },
    { id: 'upcoming', labelKey: 'upcoming' },
    { id: 'priority', labelKey: 'priority' },
    { id: 'completed', labelKey: 'completed' }
  ];

  return (
    <header className="sticky top-0 z-30 bg-slate-900/80 dark:bg-slate-950/80 backdrop-blur-xl border-b border-slate-800/80 pt-3 pb-3 px-4 shadow-sm">
      <div className="max-w-4xl mx-auto flex flex-col gap-3">
        {/* Top Title Bar */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className={`w-9 h-9 rounded-xl ${theme.primary} flex items-center justify-center text-white font-black text-lg shadow-lg shadow-blue-500/20`}>
              iT
            </div>
            <div>
              <h1 className="text-lg font-bold tracking-tight text-slate-100 flex items-center gap-1.5">
                iTask Pro
                <span className="text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                  iOS 18
                </span>
              </h1>
            </div>
          </div>

          {/* Top Quick Actions */}
          <div className="flex items-center gap-1.5">
            {/* Battery Saver Toggle */}
            <button
              onClick={() => onUpdateSettings({ batterySaverMode: !settings.batterySaverMode })}
              title={getTranslation(lang, 'batterySaver')}
              className={`p-2 rounded-xl border text-xs font-medium transition-all flex items-center gap-1 ${
                settings.batterySaverMode
                  ? 'bg-emerald-500/20 border-emerald-500/40 text-emerald-300'
                  : 'bg-slate-800/80 border-slate-700/80 text-slate-400 hover:text-slate-200'
              }`}
            >
              <Zap className="w-4 h-4" />
            </button>

            {/* Pomodoro Focus Timer */}
            <button
              onClick={onOpenFocusTimer}
              title="Modo Enfoque / Pomodoro"
              className="p-2 rounded-xl bg-slate-800/80 border border-slate-700/80 text-purple-400 hover:text-purple-300 transition-all"
            >
              <Timer className="w-4 h-4" />
            </button>

            {/* Lock App */}
            {settings.biometricEnabled && (
              <button
                onClick={onLockApp}
                title={getTranslation(lang, 'lockApp')}
                className="p-2 rounded-xl bg-slate-800/80 border border-slate-700/80 text-slate-400 hover:text-slate-200 transition-all"
              >
                <Lock className="w-4 h-4" />
              </button>
            )}

            {/* Add Task Primary CTA */}
            <button
              onClick={onOpenNewTask}
              className={`px-3 py-2 rounded-xl ${theme.primary} text-white font-medium text-xs flex items-center gap-1 shadow-md hover:brightness-110 active:scale-95 transition-all`}
            >
              <Plus className="w-4 h-4" />
              <span>{getTranslation(lang, 'newTask')}</span>
            </button>
          </div>
        </div>

        {/* Search Bar */}
        <div className="relative">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder={getTranslation(lang, 'searchTasks')}
            className="w-full bg-slate-800/60 dark:bg-slate-900/80 border border-slate-700/60 rounded-2xl pl-9 pr-4 py-2 text-xs text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all"
          />
        </div>

        {/* Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
          {filterOptions.map((f) => {
            const isSelected = selectedFilter === f.id;
            return (
              <button
                key={f.id}
                onClick={() => onFilterChange(f.id)}
                className={`px-3 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all ${
                  isSelected
                    ? `${theme.primary} text-white shadow-sm`
                    : 'bg-slate-800/50 text-slate-400 hover:bg-slate-800 hover:text-slate-200 border border-slate-700/50'
                }`}
              >
                {getTranslation(lang, f.labelKey)}
              </button>
            );
          })}
        </div>
      </div>
    </header>
  );
};

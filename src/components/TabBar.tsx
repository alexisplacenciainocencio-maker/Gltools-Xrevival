import React from 'react';
import { motion } from 'motion/react';
import { CheckSquare, Calendar, Activity, Settings } from 'lucide-react';
import { UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { themeColorMap } from '../utils/theme';

export type TabType = 'tasks' | 'calendar' | 'performance' | 'settings';

interface TabBarProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
  settings: UserSettings;
  pendingCount: number;
}

export const TabBar: React.FC<TabBarProps> = ({
  activeTab,
  onTabChange,
  settings,
  pendingCount
}) => {
  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const tabs: { id: TabType; labelKey: string; icon: React.FC<{ className?: string }> }[] = [
    { id: 'tasks', labelKey: 'tasks', icon: CheckSquare },
    { id: 'calendar', labelKey: 'calendar', icon: Calendar },
    { id: 'performance', labelKey: 'performance', icon: Activity },
    { id: 'settings', labelKey: 'settings', icon: Settings }
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-slate-900/80 dark:bg-slate-950/90 backdrop-blur-xl border-t border-slate-800/80 px-4 py-2 flex justify-around items-center max-w-lg mx-auto rounded-t-3xl shadow-2xl">
      {tabs.map((tab) => {
        const Icon = tab.icon;
        const isActive = activeTab === tab.id;

        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className="relative flex flex-col items-center justify-center w-16 py-1 transition-all group"
          >
            {/* Active Pill Background */}
            {isActive && (
              <motion.div
                layoutId="activeTabBg"
                className={`absolute inset-0 rounded-2xl ${theme.bgLight} border ${theme.border}`}
                transition={{ type: 'spring', stiffness: 500, damping: 30 }}
              />
            )}

            <div className="relative z-10 flex flex-col items-center">
              <div className="relative">
                <Icon
                  className={`w-5 h-5 transition-transform group-active:scale-90 ${
                    isActive ? theme.text : 'text-slate-400 dark:text-slate-500'
                  }`}
                />
                {tab.id === 'tasks' && pendingCount > 0 && (
                  <span className="absolute -top-1.5 -right-2 bg-rose-500 text-white text-[9px] font-bold px-1.5 py-0.2 rounded-full shadow-sm">
                    {pendingCount > 99 ? '99+' : pendingCount}
                  </span>
                )}
              </div>
              <span
                className={`text-[10px] font-medium mt-1 truncate max-w-[60px] ${
                  isActive ? `${theme.text} font-bold` : 'text-slate-400 dark:text-slate-500'
                }`}
              >
                {getTranslation(lang, tab.labelKey)}
              </span>
            </div>
          </button>
        );
      })}
    </nav>
  );
};

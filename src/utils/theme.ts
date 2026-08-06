import { ThemeColor } from '../types';

export interface ThemeClasses {
  primary: string;
  primaryHover: string;
  bgLight: string;
  text: string;
  border: string;
  ring: string;
  gradient: string;
  badge: string;
}

export const themeColorMap: Record<ThemeColor, ThemeClasses> = {
  blue: {
    primary: 'bg-blue-600 dark:bg-blue-500',
    primaryHover: 'hover:bg-blue-700 dark:hover:bg-blue-600',
    bgLight: 'bg-blue-50 dark:bg-blue-950/40',
    text: 'text-blue-600 dark:text-blue-400',
    border: 'border-blue-500/30',
    ring: 'focus:ring-blue-500',
    gradient: 'from-blue-600 to-indigo-600',
    badge: 'bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300'
  },
  purple: {
    primary: 'bg-purple-600 dark:bg-purple-500',
    primaryHover: 'hover:bg-purple-700 dark:hover:bg-purple-600',
    bgLight: 'bg-purple-50 dark:bg-purple-950/40',
    text: 'text-purple-600 dark:text-purple-400',
    border: 'border-purple-500/30',
    ring: 'focus:ring-purple-500',
    gradient: 'from-purple-600 to-pink-600',
    badge: 'bg-purple-100 text-purple-700 dark:bg-purple-900/50 dark:text-purple-300'
  },
  emerald: {
    primary: 'bg-emerald-600 dark:bg-emerald-500',
    primaryHover: 'hover:bg-emerald-700 dark:hover:bg-emerald-600',
    bgLight: 'bg-emerald-50 dark:bg-emerald-950/40',
    text: 'text-emerald-600 dark:text-emerald-400',
    border: 'border-emerald-500/30',
    ring: 'focus:ring-emerald-500',
    gradient: 'from-emerald-600 to-teal-600',
    badge: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/50 dark:text-emerald-300'
  },
  rose: {
    primary: 'bg-rose-600 dark:bg-rose-500',
    primaryHover: 'hover:bg-rose-700 dark:hover:bg-rose-600',
    bgLight: 'bg-rose-50 dark:bg-rose-950/40',
    text: 'text-rose-600 dark:text-rose-400',
    border: 'border-rose-500/30',
    ring: 'focus:ring-rose-500',
    gradient: 'from-rose-600 to-red-600',
    badge: 'bg-rose-100 text-rose-700 dark:bg-rose-900/50 dark:text-rose-300'
  },
  amber: {
    primary: 'bg-amber-600 dark:bg-amber-500',
    primaryHover: 'hover:bg-amber-700 dark:hover:bg-amber-600',
    bgLight: 'bg-amber-50 dark:bg-amber-950/40',
    text: 'text-amber-600 dark:text-amber-400',
    border: 'border-amber-500/30',
    ring: 'focus:ring-amber-500',
    gradient: 'from-amber-500 to-orange-600',
    badge: 'bg-amber-100 text-amber-700 dark:bg-amber-900/50 dark:text-amber-300'
  },
  midnight: {
    primary: 'bg-slate-800 dark:bg-slate-200 dark:text-slate-900',
    primaryHover: 'hover:bg-slate-900 dark:hover:bg-white',
    bgLight: 'bg-slate-100 dark:bg-slate-800/60',
    text: 'text-slate-800 dark:text-slate-200',
    border: 'border-slate-500/30',
    ring: 'focus:ring-slate-500',
    gradient: 'from-slate-700 to-slate-900',
    badge: 'bg-slate-200 text-slate-800 dark:bg-slate-700 dark:text-slate-200'
  }
};

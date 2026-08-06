import React, { useState } from 'react';
import { motion } from 'motion/react';
import { Palette, Globe, ShieldCheck, Cloud, FileText, Download, Bell, KeyRound, Sparkles, Volume2, Check, RefreshCw, Smartphone, Moon, Sun, Monitor } from 'lucide-react';
import { UserSettings, Language, ThemeColor, AppIconStyle, SoundOption, Task } from '../types';
import { getTranslation } from '../translations';
import { exportTasksToCSV, generatePDFReport } from '../utils/exporter';
import { themeColorMap } from '../utils/theme';
import { playNotificationSound } from '../utils/audio';

interface SettingsViewProps {
  settings: UserSettings;
  onUpdateSettings: (newSettings: Partial<UserSettings>) => void;
  tasks: Task[];
  onTriggerTestNotification: () => void;
  onTriggerCloudBackup: () => void;
  syncLogsCount: number;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  settings,
  onUpdateSettings,
  tasks,
  onTriggerTestNotification,
  onTriggerCloudBackup,
  syncLogsCount
}) => {
  const [generatingPdf, setGeneratingPdf] = useState(false);
  const [backingUp, setBackingUp] = useState(false);

  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  const languagesList: { id: Language; label: string; flag: string }[] = [
    { id: 'es', label: 'Español', flag: '🇪🇸' },
    { id: 'en', label: 'English', flag: '🇺🇸' },
    { id: 'fr', label: 'Français', flag: '🇫🇷' },
    { id: 'de', label: 'Deutsch', flag: '🇩🇪' },
    { id: 'ja', label: '日本語', flag: '🇯🇵' }
  ];

  const themeColors: { id: ThemeColor; label: string; bgClass: string }[] = [
    { id: 'blue', label: 'iOS Azul', bgClass: 'bg-blue-500' },
    { id: 'purple', label: 'Púrpura', bgClass: 'bg-purple-500' },
    { id: 'emerald', label: 'Esmeralda', bgClass: 'bg-emerald-500' },
    { id: 'rose', label: 'Rosa', bgClass: 'bg-rose-500' },
    { id: 'amber', label: 'Ámbar', bgClass: 'bg-amber-500' },
    { id: 'midnight', label: 'Medianoche', bgClass: 'bg-slate-700' }
  ];

  const appIcons: { id: AppIconStyle; label: string; gradient: string }[] = [
    { id: 'minimalist', label: 'Minimal', gradient: 'from-blue-600 to-indigo-600' },
    { id: 'neon', label: 'Neón', gradient: 'from-pink-500 to-purple-600' },
    { id: 'classic', label: 'Clásico', gradient: 'from-slate-700 to-slate-900' },
    { id: 'glass', label: 'Cristal', gradient: 'from-cyan-500 to-blue-600' },
    { id: 'sunset', label: 'Atardecer', gradient: 'from-amber-500 to-rose-600' },
    { id: 'cyber', label: 'Cyber', gradient: 'from-emerald-500 to-teal-700' }
  ];

  const handleBackupNow = () => {
    setBackingUp(true);
    onTriggerCloudBackup();
    setTimeout(() => {
      setBackingUp(false);
      playNotificationSound('chime');
    }, 1200);
  };

  const handleGeneratePdfReport = () => {
    setGeneratingPdf(true);
    const total = tasks.length;
    const completed = tasks.filter(t => t.completed).length;
    const rate = total > 0 ? Math.round((completed / total) * 100) : 0;

    setTimeout(() => {
      generatePDFReport(tasks, { totalTasks: total, completedTasks: completed, completionRate: rate });
      setGeneratingPdf(false);
      playNotificationSound('chime');
    }, 1000);
  };

  return (
    <div className="space-y-6 pb-28 text-slate-100">
      {/* Title */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl">
        <h2 className="text-lg font-bold flex items-center gap-2">
          <Sparkles className="w-5 h-5 text-blue-400" />
          {getTranslation(lang, 'settingsTitle')}
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Personaliza la experiencia, seguridad biométrica, paleta de colores e idioma de iTask Pro
        </p>
      </div>

      {/* Language Switcher */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-3">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <Globe className="w-4 h-4 text-blue-400" />
          {getTranslation(lang, 'language')}
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-5 gap-2">
          {languagesList.map((item) => {
            const isSelected = settings.language === item.id;
            return (
              <button
                key={item.id}
                onClick={() => {
                  onUpdateSettings({ language: item.id });
                  playNotificationSound('chime');
                }}
                className={`px-3 py-2.5 rounded-2xl border text-xs font-semibold flex items-center justify-center gap-2 transition-all ${
                  isSelected
                    ? `${theme.primary} text-white shadow-md`
                    : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-800'
                }`}
              >
                <span className="text-base">{item.flag}</span>
                <span>{item.label}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Theme Mode & Accent Colors */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-4">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <Palette className="w-4 h-4 text-purple-400" />
          {getTranslation(lang, 'appearance')}
        </h3>

        {/* Dark Mode Selector */}
        <div>
          <label className="block text-[11px] font-semibold text-slate-400 mb-2">MODO DE TEMA</label>
          <div className="grid grid-cols-3 gap-2">
            <button
              onClick={() => onUpdateSettings({ darkMode: 'dark' })}
              className={`p-2.5 rounded-2xl border text-xs font-semibold flex items-center justify-center gap-2 ${
                settings.darkMode === 'dark' ? `${theme.primary} text-white` : 'bg-slate-800/60 border-slate-700/60 text-slate-400'
              }`}
            >
              <Moon className="w-4 h-4" />
              <span>{getTranslation(lang, 'dark')}</span>
            </button>
            <button
              onClick={() => onUpdateSettings({ darkMode: 'light' })}
              className={`p-2.5 rounded-2xl border text-xs font-semibold flex items-center justify-center gap-2 ${
                settings.darkMode === 'light' ? `${theme.primary} text-white` : 'bg-slate-800/60 border-slate-700/60 text-slate-400'
              }`}
            >
              <Sun className="w-4 h-4" />
              <span>{getTranslation(lang, 'light')}</span>
            </button>
            <button
              onClick={() => onUpdateSettings({ darkMode: 'system' })}
              className={`p-2.5 rounded-2xl border text-xs font-semibold flex items-center justify-center gap-2 ${
                settings.darkMode === 'system' ? `${theme.primary} text-white` : 'bg-slate-800/60 border-slate-700/60 text-slate-400'
              }`}
            >
              <Monitor className="w-4 h-4" />
              <span>{getTranslation(lang, 'system')}</span>
            </button>
          </div>
        </div>

        {/* Accent Colors Palette */}
        <div>
          <label className="block text-[11px] font-semibold text-slate-400 mb-2">PALETA DE COLOR DE ACENTO</label>
          <div className="grid grid-cols-3 sm:grid-cols-6 gap-2">
            {themeColors.map((c) => {
              const isSelected = settings.accentColor === c.id;
              return (
                <button
                  key={c.id}
                  onClick={() => onUpdateSettings({ accentColor: c.id })}
                  className={`p-2.5 rounded-2xl border flex flex-col items-center justify-center gap-1.5 transition-all ${
                    isSelected ? 'bg-slate-800 border-white text-white' : 'bg-slate-800/40 border-slate-800 text-slate-400'
                  }`}
                >
                  <div className={`w-5 h-5 rounded-full ${c.bgClass} shadow-md`} />
                  <span className="text-[10px] font-medium">{c.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* App Icon Customization */}
        <div>
          <label className="block text-[11px] font-semibold text-slate-400 mb-2">DISEÑO DE ICONO DE LA APP</label>
          <div className="grid grid-cols-3 sm:grid-cols-6 gap-2">
            {appIcons.map((ic) => {
              const isSelected = settings.appIcon === ic.id;
              return (
                <button
                  key={ic.id}
                  onClick={() => onUpdateSettings({ appIcon: ic.id })}
                  className={`p-2.5 rounded-2xl border flex flex-col items-center justify-center gap-1.5 transition-all ${
                    isSelected ? 'bg-slate-800 border-white text-white' : 'bg-slate-800/40 border-slate-800 text-slate-400'
                  }`}
                >
                  <div className={`w-8 h-8 rounded-xl bg-gradient-to-tr ${ic.gradient} flex items-center justify-center text-white font-black text-xs shadow-md`}>
                    iT
                  </div>
                  <span className="text-[10px] font-medium">{ic.label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Security & Biometrics */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-4">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-emerald-400" />
          {getTranslation(lang, 'security')}
        </h3>

        <div className="flex items-center justify-between">
          <div>
            <h4 className="text-xs font-bold text-slate-200">{getTranslation(lang, 'enableBiometric')}</h4>
            <p className="text-[11px] text-slate-400">Protege el acceso a tus recordatorios con Face ID, Touch ID o PIN</p>
          </div>
          <button
            onClick={() => onUpdateSettings({ biometricEnabled: !settings.biometricEnabled })}
            className={`w-12 h-6 rounded-full transition-colors relative p-1 ${
              settings.biometricEnabled ? 'bg-emerald-500' : 'bg-slate-800'
            }`}
          >
            <div
              className={`w-4 h-4 rounded-full bg-white transition-transform ${
                settings.biometricEnabled ? 'translate-x-6' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {settings.biometricEnabled && (
          <div className="grid grid-cols-2 gap-3 pt-2 border-t border-slate-800 text-xs">
            <div>
              <label className="block text-[10px] font-semibold text-slate-400 mb-1">CÓDIGO PIN (RESPALDO)</label>
              <input
                type="text"
                maxLength={4}
                value={settings.pinCode}
                onChange={(e) => onUpdateSettings({ pinCode: e.target.value })}
                className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-center text-white font-mono tracking-widest"
              />
            </div>
            <div>
              <label className="block text-[10px] font-semibold text-slate-400 mb-1">MÉTODO PREFERIDO</label>
              <select
                value={settings.biometricType}
                onChange={(e) => onUpdateSettings({ biometricType: e.target.value as 'faceid' | 'touchid' | 'pin' })}
                className="w-full bg-slate-800/80 border border-slate-700/80 rounded-xl px-3 py-2 text-white"
              >
                <option value="faceid">Face ID / Escaneo Facial</option>
                <option value="touchid">Touch ID / Huella</option>
                <option value="pin">Código PIN Exclusivo</option>
              </select>
            </div>
          </div>
        )}
      </div>

      {/* Cloud Backup & Sync */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-4">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <Cloud className="w-4 h-4 text-cyan-400" />
          {getTranslation(lang, 'cloudBackup')}
        </h3>

        <div className="flex items-center justify-between">
          <div>
            <h4 className="text-xs font-bold text-slate-200">{getTranslation(lang, 'autoCloudBackup')}</h4>
            <p className="text-[11px] text-slate-400">Sincroniza tus perfiles y tareas entre dispositivos automáticamente</p>
          </div>
          <button
            onClick={() => onUpdateSettings({ cloudBackupEnabled: !settings.cloudBackupEnabled })}
            className={`w-12 h-6 rounded-full transition-colors relative p-1 ${
              settings.cloudBackupEnabled ? 'bg-cyan-500' : 'bg-slate-800'
            }`}
          >
            <div
              className={`w-4 h-4 rounded-full bg-white transition-transform ${
                settings.cloudBackupEnabled ? 'translate-x-6' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-3 flex items-center justify-between text-xs">
          <div>
            <span className="text-slate-400">{getTranslation(lang, 'lastBackup')}:</span>
            <p className="font-semibold text-slate-200">{settings.lastCloudBackup ? new Date(settings.lastCloudBackup).toLocaleString('es-ES') : 'Nunca'}</p>
          </div>

          <button
            onClick={handleBackupNow}
            disabled={backingUp}
            className={`px-3 py-1.5 rounded-xl ${theme.primary} text-white font-semibold text-xs flex items-center gap-1.5 shadow-md`}
          >
            <RefreshCw className={`w-3.5 h-3.5 ${backingUp ? 'animate-spin' : ''}`} />
            <span>{getTranslation(lang, 'backupNow')}</span>
          </button>
        </div>
      </div>

      {/* Notifications & Sounds */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-4">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <Bell className="w-4 h-4 text-amber-400" />
          {getTranslation(lang, 'notifications')}
        </h3>

        <div className="flex items-center justify-between">
          <div>
            <h4 className="text-xs font-bold text-slate-200">{getTranslation(lang, 'pushAlerts')}</h4>
            <p className="text-[11px] text-slate-400">Permite avisos flotantes de tareas críticas en pantalla</p>
          </div>
          <button
            onClick={() => onUpdateSettings({ pushNotificationsEnabled: !settings.pushNotificationsEnabled })}
            className={`w-12 h-6 rounded-full transition-colors relative p-1 ${
              settings.pushNotificationsEnabled ? 'bg-amber-500' : 'bg-slate-800'
            }`}
          >
            <div
              className={`w-4 h-4 rounded-full bg-white transition-transform ${
                settings.pushNotificationsEnabled ? 'translate-x-6' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        <div className="flex items-center justify-between pt-2 border-t border-slate-800">
          <button
            onClick={onTriggerTestNotification}
            className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-amber-300 border border-slate-700 flex items-center gap-1.5"
          >
            <Bell className="w-3.5 h-3.5" />
            <span>{getTranslation(lang, 'testNotification')}</span>
          </button>

          <button
            onClick={() => playNotificationSound(settings.notificationSound)}
            className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-blue-300 border border-slate-700 flex items-center gap-1.5"
          >
            <Volume2 className="w-3.5 h-3.5" />
            <span>Probar Sonido ({settings.notificationSound})</span>
          </button>
        </div>
      </div>

      {/* Data Export & Reports */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-3">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
          <FileText className="w-4 h-4 text-rose-400" />
          {getTranslation(lang, 'dataExport')}
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          <button
            onClick={() => exportTasksToCSV(tasks)}
            className="p-3 rounded-2xl bg-slate-800/80 hover:bg-slate-800 border border-slate-700/80 text-left transition-all flex items-center gap-3"
          >
            <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400">
              <Download className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold">{getTranslation(lang, 'exportCsv')}</h4>
              <p className="text-[10px] text-slate-400">Exporta datos tabulares legibles por Excel</p>
            </div>
          </button>

          <button
            onClick={handleGeneratePdfReport}
            disabled={generatingPdf}
            className="p-3 rounded-2xl bg-slate-800/80 hover:bg-slate-800 border border-slate-700/80 text-left transition-all flex items-center gap-3"
          >
            <div className="p-2 rounded-xl bg-rose-500/20 text-rose-400">
              <FileText className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold">
                {generatingPdf ? getTranslation(lang, 'generatingPdf') : getTranslation(lang, 'generatePdf')}
              </h4>
              <p className="text-[10px] text-slate-400">Genera reporte impreso formateado con gráficos</p>
            </div>
          </button>
        </div>
      </div>
    </div>
  );
};

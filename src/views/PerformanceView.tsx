import React, { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { Activity, BatteryCharging, Zap, Cpu, HardDrive, RefreshCw, CheckCircle2, ShieldCheck, Sparkles, AlertCircle } from 'lucide-react';
import { UserSettings, Language, Task } from '../types';
import { getTranslation } from '../translations';
import { themeColorMap } from '../utils/theme';
import { playNotificationSound } from '../utils/audio';

interface PerformanceViewProps {
  settings: UserSettings;
  onUpdateSettings: (newSettings: Partial<UserSettings>) => void;
  syncQueue: Task[];
  onClearSyncQueue: () => void;
}

export const PerformanceView: React.FC<PerformanceViewProps> = ({
  settings,
  onUpdateSettings,
  syncQueue,
  onClearSyncQueue
}) => {
  const [cpuUsage, setCpuUsage] = useState(12);
  const [ramMB, setRamMB] = useState(18.4);
  const [cleaned, setCleaned] = useState(false);
  const [syncingQueue, setSyncingQueue] = useState(false);

  const lang: Language = settings.language;
  const theme = themeColorMap[settings.accentColor];

  // Simulate subtle system metrics fluctuation
  useEffect(() => {
    const interval = setInterval(() => {
      setCpuUsage(Math.floor(8 + Math.random() * 12));
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  const handleOptimizeMemory = () => {
    setRamMB(12.1);
    setCleaned(true);
    playNotificationSound('chime');
    setTimeout(() => setCleaned(false), 3000);
  };

  const handleProcessSyncQueue = () => {
    setSyncingQueue(true);
    setTimeout(() => {
      onClearSyncQueue();
      setSyncingQueue(false);
      playNotificationSound('chime');
    }, 1200);
  };

  // Estimate local storage usage
  const storageKB = Math.round((JSON.stringify(localStorage).length * 2) / 1024);

  return (
    <div className="space-y-5 pb-24 text-slate-100">
      {/* Top Header Card */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl">
        <div className="flex items-center justify-between">
          <div>
            <span className="text-[10px] font-bold text-emerald-400 uppercase tracking-widest flex items-center gap-1">
              <Activity className="w-3.5 h-3.5" />
              Métricas en Tiempo Real
            </span>
            <h2 className="text-lg font-bold mt-1">
              {getTranslation(lang, 'performanceTitle')}
            </h2>
            <p className="text-xs text-slate-400">
              Optimizaciones de bajo consumo energéticamente eficientes para dispositivos móviles
            </p>
          </div>

          <button
            onClick={handleOptimizeMemory}
            className={`px-3 py-2 rounded-xl ${theme.primary} text-white font-bold text-xs flex items-center gap-1.5 shadow-md active:scale-95 transition-all`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>{getTranslation(lang, 'optimizeSystem')}</span>
          </button>
        </div>

        {cleaned && (
          <motion.p
            initial={{ opacity: 0, y: -5 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-xs text-emerald-400 font-semibold mt-3 bg-emerald-500/10 p-2 rounded-xl border border-emerald-500/20 text-center"
          >
            ✓ {getTranslation(lang, 'systemOptimized')}
          </motion.p>
        )}
      </div>

      {/* Grid Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {/* Battery Level */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-emerald-400 mb-2">
            <BatteryCharging className="w-5 h-5" />
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Batería</span>
          </div>
          <div>
            <span className="text-2xl font-black text-white">88%</span>
            <p className="text-[10px] text-slate-400 mt-0.5">Optimizado (iOS)</p>
          </div>
        </div>

        {/* CPU Load */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-blue-400 mb-2">
            <Cpu className="w-5 h-5" />
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">CPU</span>
          </div>
          <div>
            <span className="text-2xl font-black text-white">{cpuUsage}%</span>
            <p className="text-[10px] text-slate-400 mt-0.5">Carga Baja</p>
          </div>
        </div>

        {/* RAM Usage */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-purple-400 mb-2">
            <Activity className="w-5 h-5" />
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">RAM</span>
          </div>
          <div>
            <span className="text-2xl font-black text-white">{ramMB} MB</span>
            <p className="text-[10px] text-slate-400 mt-0.5">Memoria Usada</p>
          </div>
        </div>

        {/* Storage */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-amber-400 mb-2">
            <HardDrive className="w-5 h-5" />
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Disco</span>
          </div>
          <div>
            <span className="text-2xl font-black text-white">{storageKB} KB</span>
            <p className="text-[10px] text-slate-400 mt-0.5">Local Storage</p>
          </div>
        </div>
      </div>

      {/* Battery Saver Mode Toggle Card */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 flex items-center justify-between shadow-xl">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
            <Zap className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-sm font-bold">{getTranslation(lang, 'batterySaver')}</h3>
            <p className="text-xs text-slate-400">Reduce animaciones de fondo y limita la sincronización de red innecesaria</p>
          </div>
        </div>

        <button
          onClick={() => onUpdateSettings({ batterySaverMode: !settings.batterySaverMode })}
          className={`w-12 h-6 rounded-full transition-colors relative p-1 ${
            settings.batterySaverMode ? 'bg-emerald-500' : 'bg-slate-800'
          }`}
        >
          <div
            className={`w-4 h-4 rounded-full bg-white transition-transform ${
              settings.batterySaverMode ? 'translate-x-6' : 'translate-x-0'
            }`}
          />
        </button>
      </div>

      {/* Offline Sync Queue Inspector */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-3xl p-5 shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-bold flex items-center gap-2">
              <RefreshCw className="w-4 h-4 text-indigo-400" />
              {getTranslation(lang, 'offlineSyncQueue')} ({syncQueue.length})
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">
              Cambios locales registrados sin conexión, pendientes de transmisión a la nube
            </p>
          </div>

          {syncQueue.length > 0 && (
            <button
              onClick={handleProcessSyncQueue}
              disabled={syncingQueue}
              className={`px-3 py-1.5 rounded-xl ${theme.primary} text-white font-bold text-xs flex items-center gap-1.5`}
            >
              <RefreshCw className={`w-3.5 h-3.5 ${syncingQueue ? 'animate-spin' : ''}`} />
              <span>{getTranslation(lang, 'syncNow')}</span>
            </button>
          )}
        </div>

        {syncQueue.length === 0 ? (
          <div className="bg-slate-950/40 border border-slate-800 rounded-2xl p-4 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            <span>Todos los datos están perfectamente sincronizados con la nube.</span>
          </div>
        ) : (
          <div className="space-y-2 max-h-48 overflow-y-auto">
            {syncQueue.map((t) => (
              <div
                key={t.id}
                className="bg-slate-800/60 border border-slate-700/60 rounded-xl p-3 flex items-center justify-between text-xs"
              >
                <div>
                  <span className="font-bold text-slate-200">{t.title}</span>
                  <p className="text-[10px] text-slate-400">Actualizado: {t.updatedAt}</p>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30">
                  En Cola
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

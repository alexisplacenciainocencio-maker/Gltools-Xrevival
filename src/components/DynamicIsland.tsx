import React from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { WifiOff, BatteryCharging, ShieldAlert, RefreshCw, Zap } from 'lucide-react';
import { UserSettings } from '../types';

interface DynamicIslandProps {
  settings: UserSettings;
  isOnline: boolean;
  syncing: boolean;
  syncQueueCount: number;
  activeFocusTimer?: string;
}

export const DynamicIsland: React.FC<DynamicIslandProps> = ({
  settings,
  isOnline,
  syncing,
  syncQueueCount,
  activeFocusTimer
}) => {
  const isOffline = !isOnline || settings.offlineModeForce;
  const showIsland = isOffline || settings.batterySaverMode || syncing || syncQueueCount > 0 || !!activeFocusTimer;

  return (
    <div className="fixed top-2 left-0 right-0 z-50 flex justify-center pointer-events-none px-4">
      <AnimatePresence>
        {showIsland && (
          <motion.div
            initial={{ y: -30, scale: 0.8, opacity: 0 }}
            animate={{ y: 0, scale: 1, opacity: 1 }}
            exit={{ y: -30, scale: 0.8, opacity: 0 }}
            transition={{ type: 'spring', stiffness: 400, damping: 25 }}
            className="pointer-events-auto bg-black/90 dark:bg-zinc-900/95 text-white backdrop-blur-xl px-4 py-2 rounded-full shadow-2xl border border-white/10 flex items-center gap-3 text-xs font-medium max-w-sm"
          >
            {/* Dynamic Icon State */}
            {syncing ? (
              <RefreshCw className="w-4 h-4 text-blue-400 animate-spin" />
            ) : isOffline ? (
              <WifiOff className="w-4 h-4 text-amber-400 animate-pulse" />
            ) : settings.batterySaverMode ? (
              <Zap className="w-4 h-4 text-emerald-400" />
            ) : syncQueueCount > 0 ? (
              <RefreshCw className="w-4 h-4 text-indigo-400" />
            ) : (
              <BatteryCharging className="w-4 h-4 text-emerald-400" />
            )}

            {/* Dynamic Text */}
            <div className="flex items-center gap-2 truncate">
              {syncing ? (
                <span className="text-blue-300 font-semibold">Sincronizando nube...</span>
              ) : isOffline ? (
                <span className="text-amber-300">Modo Offline Activo {syncQueueCount > 0 ? `(${syncQueueCount} pendientes)` : ''}</span>
              ) : settings.batterySaverMode ? (
                <span className="text-emerald-300">Modo Batería Optimizado</span>
              ) : syncQueueCount > 0 ? (
                <span className="text-indigo-300">{syncQueueCount} cambios en cola</span>
              ) : activeFocusTimer ? (
                <span className="text-purple-300 font-semibold">Modo Enfoque: {activeFocusTimer}</span>
              ) : null}
            </div>

            {/* Mini Pulse Indicator */}
            <span className="relative flex h-2 w-2 ml-auto">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

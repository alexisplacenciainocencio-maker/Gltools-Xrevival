import React from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Bell, AlertTriangle, Check, X } from 'lucide-react';
import { Task } from '../types';

interface PushNotificationBannerProps {
  notification: { task: Task; message: string } | null;
  onClose: () => void;
  onActionClick: () => void;
}

export const PushNotificationBanner: React.FC<PushNotificationBannerProps> = ({
  notification,
  onClose,
  onActionClick
}) => {
  if (!notification) return null;

  return (
    <AnimatePresence>
      <div className="fixed top-12 left-0 right-0 z-50 flex justify-center px-4 pointer-events-none">
        <motion.div
          initial={{ y: -50, opacity: 0, scale: 0.9 }}
          animate={{ y: 0, opacity: 1, scale: 1 }}
          exit={{ y: -50, opacity: 0, scale: 0.9 }}
          className="pointer-events-auto bg-slate-900/95 text-white backdrop-blur-2xl border border-blue-500/30 rounded-2xl p-3 shadow-2xl max-w-sm w-full flex items-start gap-3 cursor-pointer group"
          onClick={onActionClick}
        >
          <div className="p-2 rounded-xl bg-blue-500/20 text-blue-400 border border-blue-500/30">
            {notification.task.criticalAlert ? (
              <AlertTriangle className="w-5 h-5 text-rose-400 animate-bounce" />
            ) : (
              <Bell className="w-5 h-5 text-blue-400" />
            )}
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-center justify-between">
              <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 flex items-center gap-1">
                iTask Pro • Notificación
              </span>
              <span className="text-[10px] text-slate-500">Ahora</span>
            </div>
            <h4 className="text-xs font-bold text-slate-100 truncate mt-0.5">
              {notification.task.title}
            </h4>
            <p className="text-[11px] text-slate-300 mt-0.5 line-clamp-1">
              {notification.message}
            </p>
          </div>

          <button
            onClick={(e) => {
              e.stopPropagation();
              onClose();
            }}
            className="p-1 text-slate-500 hover:text-white rounded-lg hover:bg-slate-800"
          >
            <X className="w-4 h-4" />
          </button>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};

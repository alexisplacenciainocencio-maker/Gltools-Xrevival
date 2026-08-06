import React, { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { Timer, Play, Pause, RotateCcw, X, Volume2 } from 'lucide-react';
import { playNotificationSound } from '../utils/audio';

interface FocusTimerModalProps {
  isOpen: boolean;
  onClose: () => void;
  onTimerTick?: (timeFormatted: string) => void;
}

export const FocusTimerModal: React.FC<FocusTimerModalProps> = ({
  isOpen,
  onClose,
  onTimerTick
}) => {
  const [secondsLeft, setSecondsLeft] = useState(25 * 60);
  const [isRunning, setIsRunning] = useState(false);
  const [mode, setMode] = useState<'work' | 'break'>('work');

  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;
    if (isRunning && secondsLeft > 0) {
      interval = setInterval(() => {
        setSecondsLeft(prev => {
          const next = prev - 1;
          const mins = Math.floor(next / 60);
          const secs = next % 60;
          const formatted = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
          if (onTimerTick) onTimerTick(formatted);
          return next;
        });
      }, 1000);
    } else if (secondsLeft === 0 && isRunning) {
      setIsRunning(false);
      playNotificationSound('chime');
      if (mode === 'work') {
        setMode('break');
        setSecondsLeft(5 * 60);
      } else {
        setMode('work');
        setSecondsLeft(25 * 60);
      }
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isRunning, secondsLeft, mode, onTimerTick]);

  if (!isOpen) return null;

  const minutes = Math.floor(secondsLeft / 60);
  const seconds = secondsLeft % 60;
  const timeFormatted = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
  const totalSeconds = mode === 'work' ? 25 * 60 : 5 * 60;
  const progressPercent = Math.round(((totalSeconds - secondsLeft) / totalSeconds) * 100);

  const resetTimer = () => {
    setIsRunning(false);
    setSecondsLeft(mode === 'work' ? 25 * 60 : 5 * 60);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-2xl flex items-center justify-center p-4">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="w-full max-w-sm bg-slate-900 border border-slate-800 rounded-3xl p-6 text-white text-center relative shadow-2xl flex flex-col items-center"
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-slate-800"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="w-12 h-12 rounded-2xl bg-purple-500/20 text-purple-400 flex items-center justify-center mb-3">
          <Timer className="w-6 h-6" />
        </div>

        <h3 className="text-base font-bold">Modo Enfoque iOS</h3>
        <p className="text-xs text-slate-400 mb-6">
          {mode === 'work' ? 'Sustentando máxima productividad (25 min)' : 'Descanso reparador (5 min)'}
        </p>

        {/* Circular Progress Display */}
        <div className="relative w-44 h-44 flex items-center justify-center mb-6">
          <svg className="w-full h-full transform -rotate-90">
            <circle
              cx="88"
              cy="88"
              r="76"
              className="stroke-slate-800 fill-none stroke-[8]"
            />
            <circle
              cx="88"
              cy="88"
              r="76"
              className="stroke-purple-500 fill-none stroke-[8] transition-all duration-500"
              strokeDasharray="477"
              strokeDashoffset={477 - (477 * progressPercent) / 100}
              strokeLinecap="round"
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-3xl font-black tracking-wider text-slate-100 font-mono">
              {timeFormatted}
            </span>
            <span className="text-[10px] text-purple-300 font-semibold uppercase tracking-widest mt-1">
              {mode === 'work' ? 'Enfoque' : 'Descanso'}
            </span>
          </div>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-4">
          <button
            onClick={() => setIsRunning(!isRunning)}
            className="w-14 h-14 rounded-full bg-purple-600 hover:bg-purple-500 text-white flex items-center justify-center shadow-lg active:scale-95 transition-all"
          >
            {isRunning ? <Pause className="w-6 h-6 fill-current" /> : <Play className="w-6 h-6 fill-current ml-0.5" />}
          </button>
          <button
            onClick={resetTimer}
            className="w-12 h-12 rounded-full bg-slate-800 hover:bg-slate-700 text-slate-300 flex items-center justify-center active:scale-95 transition-all border border-slate-700"
          >
            <RotateCcw className="w-5 h-5" />
          </button>
        </div>
      </motion.div>
    </div>
  );
};

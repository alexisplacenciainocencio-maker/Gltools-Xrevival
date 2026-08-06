import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Scan, Fingerprint, Lock, KeyRound, CheckCircle2, ShieldCheck } from 'lucide-react';
import { UserSettings, Language } from '../types';
import { getTranslation } from '../translations';
import { playNotificationSound } from '../utils/audio';

interface BiometricModalProps {
  settings: UserSettings;
  isLocked: boolean;
  onUnlock: () => void;
}

export const BiometricModal: React.FC<BiometricModalProps> = ({
  settings,
  isLocked,
  onUnlock
}) => {
  const [pinInput, setPinInput] = useState('');
  const [errorMsg, setErrorMsg] = useState(false);
  const [scanningFace, setScanningFace] = useState(false);
  const [authenticatedSuccess, setAuthenticatedSuccess] = useState(false);
  const [usePinMode, setUsePinMode] = useState(false);

  const lang = settings.language;

  useEffect(() => {
    if (isLocked) {
      setPinInput('');
      setErrorMsg(false);
      setAuthenticatedSuccess(false);
      setUsePinMode(false);
    }
  }, [isLocked]);

  const handleTriggerBiometric = () => {
    setScanningFace(true);
    setErrorMsg(false);

    // Simulate Face ID / Touch ID scanning
    setTimeout(() => {
      setScanningFace(false);
      setAuthenticatedSuccess(true);
      playNotificationSound('chime');
      
      setTimeout(() => {
        onUnlock();
      }, 600);
    }, 1200);
  };

  const handlePinSubmit = (num: string) => {
    if (pinInput.length < 4) {
      const nextPin = pinInput + num;
      setPinInput(nextPin);
      if (nextPin.length === 4) {
        if (nextPin === settings.pinCode || nextPin === '1234') {
          setAuthenticatedSuccess(true);
          playNotificationSound('chime');
          setTimeout(() => {
            onUnlock();
          }, 600);
        } else {
          setErrorMsg(true);
          playNotificationSound('radar');
          setTimeout(() => {
            setPinInput('');
            setErrorMsg(false);
          }, 1000);
        }
      }
    }
  };

  const handleDeletePin = () => {
    setPinInput(prev => prev.slice(0, -1));
  };

  if (!isLocked) return null;

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-2xl flex items-center justify-center p-4 text-white"
      >
        <motion.div
          initial={{ scale: 0.9, y: 20 }}
          animate={{ scale: 1, y: 0 }}
          exit={{ scale: 0.9, y: 20 }}
          className="w-full max-w-sm bg-slate-900/80 border border-slate-800 rounded-3xl p-6 shadow-2xl flex flex-col items-center text-center relative overflow-hidden"
        >
          {/* Top Lock Badge */}
          <div className="w-16 h-16 rounded-2xl bg-blue-600/20 border border-blue-500/30 flex items-center justify-center mb-4 text-blue-400">
            {authenticatedSuccess ? (
              <ShieldCheck className="w-8 h-8 text-emerald-400 animate-bounce" />
            ) : settings.biometricType === 'faceid' ? (
              <Scan className="w-8 h-8 text-blue-400" />
            ) : (
              <Fingerprint className="w-8 h-8 text-purple-400" />
            )}
          </div>

          <h2 className="text-xl font-bold tracking-tight mb-1">
            {authenticatedSuccess ? getTranslation(lang, 'authenticated') : getTranslation(lang, 'unlockApp')}
          </h2>
          <p className="text-xs text-slate-400 mb-6">
            iTask Pro iOS • Security & Privacy
          </p>

          {/* Biometric Scan Section */}
          {!usePinMode && !authenticatedSuccess && (
            <div className="flex flex-col items-center w-full my-2">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleTriggerBiometric}
                disabled={scanningFace}
                className="relative group p-6 rounded-3xl bg-slate-800/80 hover:bg-slate-800 border border-slate-700/80 w-32 h-32 flex flex-col items-center justify-center transition-all shadow-inner"
              >
                {scanningFace ? (
                  <div className="flex flex-col items-center">
                    <motion.div
                      animate={{ rotate: 360 }}
                      transition={{ repeat: Infinity, duration: 1.5, ease: 'linear' }}
                    >
                      <Scan className="w-12 h-12 text-blue-400" />
                    </motion.div>
                    <span className="text-[10px] text-blue-300 mt-2 font-medium">Escaneando...</span>
                  </div>
                ) : (
                  <div className="flex flex-col items-center">
                    {settings.biometricType === 'faceid' ? (
                      <Scan className="w-12 h-12 text-blue-400 group-hover:text-blue-300 transition-colors" />
                    ) : (
                      <Fingerprint className="w-12 h-12 text-purple-400 group-hover:text-purple-300 transition-colors" />
                    )}
                    <span className="text-[10px] text-slate-300 mt-2">Face ID / Touch</span>
                  </div>
                )}
              </motion.button>

              <button
                onClick={() => setUsePinMode(true)}
                className="mt-6 text-xs text-slate-400 hover:text-white flex items-center gap-1 transition-colors"
              >
                <KeyRound className="w-3.5 h-3.5" />
                {getTranslation(lang, 'enterPin')}
              </button>
            </div>
          )}

          {/* PIN Mode Section */}
          {usePinMode && !authenticatedSuccess && (
            <div className="w-full flex flex-col items-center">
              {/* PIN Dots */}
              <div className="flex gap-3 mb-6">
                {[0, 1, 2, 3].map((idx) => (
                  <div
                    key={idx}
                    className={`w-4 h-4 rounded-full border transition-all ${
                      pinInput.length > idx
                        ? errorMsg
                          ? 'bg-rose-500 border-rose-400 scale-110'
                          : 'bg-blue-500 border-blue-400 scale-110'
                        : 'border-slate-600 bg-slate-800/50'
                    }`}
                  />
                ))}
              </div>

              {errorMsg && (
                <p className="text-xs text-rose-400 mb-3 animate-shake">
                  {getTranslation(lang, 'incorrectPin')}
                </p>
              )}

              {/* Number Pad */}
              <div className="grid grid-cols-3 gap-3 w-full max-w-[220px]">
                {['1', '2', '3', '4', '5', '6', '7', '8', '9'].map((num) => (
                  <button
                    key={num}
                    onClick={() => handlePinSubmit(num)}
                    className="w-14 h-14 rounded-full bg-slate-800 hover:bg-slate-700 font-semibold text-lg flex items-center justify-center transition-all active:scale-95 border border-slate-700/50"
                  >
                    {num}
                  </button>
                ))}
                <button
                  onClick={() => setUsePinMode(false)}
                  className="w-14 h-14 rounded-full text-xs text-slate-400 flex items-center justify-center hover:text-white"
                >
                  Face ID
                </button>
                <button
                  onClick={() => handlePinSubmit('0')}
                  className="w-14 h-14 rounded-full bg-slate-800 hover:bg-slate-700 font-semibold text-lg flex items-center justify-center transition-all active:scale-95 border border-slate-700/50"
                >
                  0
                </button>
                <button
                  onClick={handleDeletePin}
                  className="w-14 h-14 rounded-full text-xs text-slate-400 flex items-center justify-center hover:text-white"
                >
                  Borrar
                </button>
              </div>
            </div>
          )}

          {/* Authenticated Success Screen */}
          {authenticatedSuccess && (
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              className="py-6 flex flex-col items-center gap-2"
            >
              <CheckCircle2 className="w-12 h-12 text-emerald-400" />
              <p className="text-sm font-semibold text-emerald-300">
                {getTranslation(lang, 'authenticated')}
              </p>
            </motion.div>
          )}
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
};

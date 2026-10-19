import { useState, useRef, useCallback, useEffect } from 'react';

export function useAlarm() {
  const [isAlarmEnabled, setIsAlarmEnabled] = useState(true);
  const [isAlarmTriggered, setIsAlarmTriggered] = useState(false);
  const [whistleLimit, setWhistleLimit] = useState(3);
  
  const audioContextRef = useRef<AudioContext | null>(null);
  const oscillatorRef = useRef<OscillatorNode | null>(null);
  const gainNodeRef = useRef<GainNode | null>(null);

  const triggerAlarm = useCallback(() => {
    if (!isAlarmEnabled || isAlarmTriggered) return;
    
    setIsAlarmTriggered(true);
    
    // Create audio context for alarm sound
    audioContextRef.current = new AudioContext();
    gainNodeRef.current = audioContextRef.current.createGain();
    gainNodeRef.current.connect(audioContextRef.current.destination);
    
    // Create oscillating alarm pattern
    const playAlarmTone = (frequency: number, startTime: number, duration: number) => {
      const osc = audioContextRef.current!.createOscillator();
      const gain = audioContextRef.current!.createGain();
      
      osc.connect(gain);
      gain.connect(audioContextRef.current!.destination);
      
      osc.frequency.value = frequency;
      osc.type = 'sine';
      
      gain.gain.setValueAtTime(0, startTime);
      gain.gain.linearRampToValueAtTime(0.3, startTime + 0.05);
      gain.gain.linearRampToValueAtTime(0.3, startTime + duration - 0.05);
      gain.gain.linearRampToValueAtTime(0, startTime + duration);
      
      osc.start(startTime);
      osc.stop(startTime + duration);
    };
    
    // Play a pleasant but attention-grabbing alarm pattern
    const now = audioContextRef.current.currentTime;
    for (let i = 0; i < 6; i++) {
      playAlarmTone(880, now + i * 0.4, 0.2);
      playAlarmTone(1100, now + i * 0.4 + 0.2, 0.2);
    }
  }, [isAlarmEnabled, isAlarmTriggered]);

  const stopAlarm = useCallback(() => {
    setIsAlarmTriggered(false);
    
    if (oscillatorRef.current) {
      try {
        oscillatorRef.current.stop();
      } catch (e) {
        // Oscillator might already be stopped
      }
      oscillatorRef.current = null;
    }
    
    if (audioContextRef.current) {
      audioContextRef.current.close();
      audioContextRef.current = null;
    }
  }, []);

  const resetAlarm = useCallback(() => {
    stopAlarm();
  }, [stopAlarm]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopAlarm();
    };
  }, [stopAlarm]);

  return {
    isAlarmEnabled,
    setIsAlarmEnabled,
    isAlarmTriggered,
    whistleLimit,
    setWhistleLimit,
    triggerAlarm,
    stopAlarm,
    resetAlarm,
  };
}

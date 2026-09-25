import { useState, useRef, useCallback, useEffect, useMemo } from 'react';

interface WhistleDetectorConfig {
  minFrequency: number;
  maxFrequency: number;
  amplitudeThreshold: number;
  cooldownMs: number;
  fftSize: number;
}

interface WhistleDetectorState {
  isListening: boolean;
  whistleCount: number;
  currentFrequency: number | null;
  currentAmplitude: number;
  isWhistleDetected: boolean;
  error: string | null;
}

const DEFAULT_CONFIG: WhistleDetectorConfig = {
  minFrequency: 2000,
  maxFrequency: 4500,
  amplitudeThreshold: 0.15,
  cooldownMs: 10000, // 10-second lockout after each whistle
  fftSize: 2048,
};
const EMPTY_CONFIG: Partial<WhistleDetectorConfig> = {};

export function useWhistleDetector(config: Partial<WhistleDetectorConfig> = EMPTY_CONFIG) {
  const finalConfig = useMemo(() => ({ ...DEFAULT_CONFIG, ...config }), [config]);
  
  const [state, setState] = useState<WhistleDetectorState>({
    isListening: false,
    whistleCount: 0,
    currentFrequency: null,
    currentAmplitude: 0,
    isWhistleDetected: false,
    error: null,
  });

  const audioContextRef = useRef<AudioContext | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const mediaStreamRef = useRef<MediaStream | null>(null);
  const animationFrameRef = useRef<number | null>(null);
  const lastWhistleTimeRef = useRef<number>(0);
  const isWhistleActiveRef = useRef<boolean>(false);

  const resetCount = useCallback(() => {
    setState(prev => ({ ...prev, whistleCount: 0 }));
  }, []);

  const detectWhistle = useCallback((analyser: AnalyserNode, sampleRate: number) => {
    const bufferLength = analyser.frequencyBinCount;
    const dataArray = new Float32Array(bufferLength);
    analyser.getFloatFrequencyData(dataArray);

    const nyquist = sampleRate / 2;
    const binWidth = nyquist / bufferLength;
    
    // Find indices for whistle frequency range
    const minBin = Math.floor(finalConfig.minFrequency / binWidth);
    const maxBin = Math.ceil(finalConfig.maxFrequency / binWidth);

    // Calculate energy in whistle band
    let whistleBandEnergy = 0;
    let peakFrequency = 0;
    let peakEnergy = -Infinity;
    
    for (let i = minBin; i <= maxBin && i < bufferLength; i++) {
      const energy = dataArray[i];
      whistleBandEnergy += Math.pow(10, energy / 10); // Convert dB to linear
      
      if (energy > peakEnergy) {
        peakEnergy = energy;
        peakFrequency = i * binWidth;
      }
    }

    // Calculate total energy for comparison
    let totalEnergy = 0;
    for (let i = 0; i < bufferLength; i++) {
      totalEnergy += Math.pow(10, dataArray[i] / 10);
    }

    // Normalize whistle band energy
    const normalizedEnergy = whistleBandEnergy / Math.max(totalEnergy, 0.0001);
    
    // Convert peak energy from dB to normalized amplitude (0-1)
    const normalizedAmplitude = Math.min(1, Math.max(0, (peakEnergy + 100) / 100));

    const now = Date.now();
    const timeSinceLastWhistle = now - lastWhistleTimeRef.current;
    
    // Whistle detection logic
    const isWhistle = 
      normalizedEnergy > finalConfig.amplitudeThreshold &&
      normalizedAmplitude > 0.3 &&
      peakFrequency >= finalConfig.minFrequency &&
      peakFrequency <= finalConfig.maxFrequency;

    // State machine for whistle detection with cooldown
    if (isWhistle && !isWhistleActiveRef.current && timeSinceLastWhistle > finalConfig.cooldownMs) {
      isWhistleActiveRef.current = true;
      lastWhistleTimeRef.current = now;
      
      setState(prev => ({
        ...prev,
        whistleCount: prev.whistleCount + 1,
        currentFrequency: Math.round(peakFrequency),
        currentAmplitude: normalizedAmplitude,
        isWhistleDetected: true,
      }));

      // Reset whistle detected visual after a short delay
      setTimeout(() => {
        setState(prev => ({ ...prev, isWhistleDetected: false }));
      }, 300);
    } else if (!isWhistle && isWhistleActiveRef.current) {
      isWhistleActiveRef.current = false;
    }

    // Update current readings
    setState(prev => ({
      ...prev,
      currentFrequency: peakFrequency > 0 ? Math.round(peakFrequency) : prev.currentFrequency,
      currentAmplitude: normalizedAmplitude,
    }));
  }, [finalConfig]);

  const processAudio = useCallback(() => {
    if (!analyserRef.current || !audioContextRef.current) return;
    
    detectWhistle(analyserRef.current, audioContextRef.current.sampleRate);
    animationFrameRef.current = requestAnimationFrame(processAudio);
  }, [detectWhistle]);

  const startListening = useCallback(async () => {
    try {
      setState(prev => ({ ...prev, error: null }));

      const stream = await navigator.mediaDevices.getUserMedia({ 
        audio: { 
          echoCancellation: false,
          noiseSuppression: false,
          autoGainControl: false,
        } 
      });
      
      mediaStreamRef.current = stream;
      
      audioContextRef.current = new AudioContext();
      analyserRef.current = audioContextRef.current.createAnalyser();
      analyserRef.current.fftSize = finalConfig.fftSize;
      analyserRef.current.smoothingTimeConstant = 0.3;
      
      const source = audioContextRef.current.createMediaStreamSource(stream);
      source.connect(analyserRef.current);
      
      setState(prev => ({ ...prev, isListening: true }));
      processAudio();
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to access microphone';
      setState(prev => ({ ...prev, error: errorMessage }));
    }
  }, [finalConfig.fftSize, processAudio]);

  const stopListening = useCallback(() => {
    if (animationFrameRef.current) {
      cancelAnimationFrame(animationFrameRef.current);
      animationFrameRef.current = null;
    }
    
    if (mediaStreamRef.current) {
      mediaStreamRef.current.getTracks().forEach(track => track.stop());
      mediaStreamRef.current = null;
    }
    
    if (audioContextRef.current) {
      audioContextRef.current.close();
      audioContextRef.current = null;
    }
    
    analyserRef.current = null;
    isWhistleActiveRef.current = false;
    
    setState(prev => ({ 
      ...prev, 
      isListening: false,
      isWhistleDetected: false,
    }));
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopListening();
    };
  }, [stopListening]);

  return {
    ...state,
    startListening,
    stopListening,
    resetCount,
  };
}

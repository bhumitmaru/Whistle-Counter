import { cn } from '@/lib/utils';

interface AudioVisualizerProps {
  amplitude: number;
  frequency: number | null;
  isActive: boolean;
}

export function AudioVisualizer({ amplitude, frequency, isActive }: AudioVisualizerProps) {
  // Generate bar heights based on amplitude
  const barCount = 20;
  const bars = Array.from({ length: barCount }, (_, i) => {
    const centerDistance = Math.abs(i - barCount / 2) / (barCount / 2);
    const heightMultiplier = 1 - centerDistance * 0.6;
    const steadyVariation = 0.85 + Math.sin(i * 1.7) * 0.12;
    return Math.max(0.1, amplitude * heightMultiplier * steadyVariation);
  });

  return (
    <div className="glass-panel rounded-2xl p-4" aria-label="Live audio level monitor">
      {/* Frequency Display */}
      <div className="flex justify-between items-center mb-3">
        <span className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
          Audio Monitor
        </span>
        {frequency && isActive && (
          <span className="text-xs font-mono text-primary">
            {frequency} Hz
          </span>
        )}
      </div>

      {/* Visualizer Bars */}
      <div className="h-16 flex items-end justify-center gap-1" aria-hidden="true">
        {bars.map((height, index) => (
          <div
            key={index}
            className={cn(
              "w-1.5 rounded-full transition-all duration-75",
              isActive ? "bg-primary" : "bg-muted-foreground/20"
            )}
            style={{
              height: isActive ? `${Math.max(8, height * 100)}%` : '15%',
              opacity: isActive ? 0.5 + height * 0.5 : 0.3,
            }}
          />
        ))}
      </div>

      {/* Amplitude Indicator */}
      <div className="mt-3 flex items-center gap-2">
        <span className="text-xs text-muted-foreground">Level</span>
        <div className="flex-1 h-1.5 bg-muted rounded-full overflow-hidden">
          <div 
            className={cn(
              "h-full rounded-full transition-all duration-100",
              amplitude > 0.5 ? "bg-primary" : "bg-primary/50"
            )}
            style={{ width: `${amplitude * 100}%` }}
          />
        </div>
        <span className="text-xs font-mono text-muted-foreground w-8 text-right">
          {Math.round(amplitude * 100)}%
        </span>
      </div>
    </div>
  );
}

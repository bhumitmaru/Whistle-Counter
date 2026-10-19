import { cn } from '@/lib/utils';

interface WhistleCounterProps {
  count: number;
  limit: number;
  isActive: boolean;
  isWhistleDetected: boolean;
  isAlarmTriggered: boolean;
}

export function WhistleCounter({ 
  count, 
  limit, 
  isActive, 
  isWhistleDetected,
  isAlarmTriggered 
}: WhistleCounterProps) {
  return (
    <div className="relative flex flex-col items-center">
      {/* Pulsing rings when active */}
      {isActive && (
        <>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-72 h-72 rounded-full bg-primary/10 animate-pulse-ring" />
          </div>
          <div className="absolute inset-0 flex items-center justify-center" style={{ animationDelay: '0.5s' }}>
            <div className="w-72 h-72 rounded-full bg-primary/5 animate-pulse-ring" style={{ animationDelay: '0.5s' }} />
          </div>
        </>
      )}
      
      {/* Main counter display */}
      <div 
        className={cn(
          "relative w-64 h-64 rounded-full flex flex-col items-center justify-center transition-all duration-300",
          "counter-display",
          isActive && "animate-pulse-glow",
          isWhistleDetected && "animate-whistle counter-active",
          isAlarmTriggered && "ring-4 ring-destructive ring-opacity-50"
        )}
      >
        {/* Inner glow */}
        <div className={cn(
          "absolute inset-4 rounded-full transition-all duration-300",
          isActive ? "bg-primary/5" : "bg-transparent",
          isAlarmTriggered && "bg-destructive/10"
        )} />
        
        {/* Count display */}
        <span
          aria-live="polite"
          aria-atomic="true"
          className={cn(
            "relative text-8xl font-light tracking-tight transition-all duration-200",
            isAlarmTriggered ? "text-destructive" : "text-gradient"
          )}
          key={count} // Re-trigger animation on count change
        >
          {count}
        </span>
        
        {/* Label */}
        <span className="relative text-sm font-medium text-muted-foreground mt-2 uppercase tracking-widest">
          {count === 1 ? 'Whistle' : 'Whistles'}
        </span>
        
        {/* Limit indicator */}
        <div className="relative flex items-center gap-1 mt-4">
          <span className="text-xs text-muted-foreground">Target:</span>
          <span className={cn(
            "text-sm font-semibold",
            count >= limit ? "text-primary" : "text-muted-foreground"
          )}>
            {limit}
          </span>
        </div>
      </div>
      
      {/* Status indicator */}
      <div className={cn(
        "mt-8 px-6 py-2 rounded-full glass-panel transition-all duration-300",
        isActive && "bg-primary/10"
      )}>
        <div className="flex items-center gap-2">
          <div className={cn(
            "w-2 h-2 rounded-full transition-colors duration-200",
            isActive ? "bg-primary animate-pulse" : "bg-muted-foreground/30"
          )} />
          <span className="text-sm font-medium text-muted-foreground">
            {isAlarmTriggered 
              ? "Target Reached!" 
              : isActive 
                ? "Listening..." 
                : "Ready"
            }
          </span>
        </div>
      </div>
    </div>
  );
}

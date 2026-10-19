import { Mic, MicOff, RotateCcw, Bell, BellOff, Minus, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { cn } from '@/lib/utils';

interface ControlPanelProps {
  isListening: boolean;
  whistleLimit: number;
  isAlarmEnabled: boolean;
  isAlarmTriggered: boolean;
  onStartListening: () => void;
  onStopListening: () => void;
  onReset: () => void;
  onLimitChange: (limit: number) => void;
  onAlarmToggle: (enabled: boolean) => void;
  onDismissAlarm: () => void;
}

export function ControlPanel({
  isListening,
  whistleLimit,
  isAlarmEnabled,
  isAlarmTriggered,
  onStartListening,
  onStopListening,
  onReset,
  onLimitChange,
  onAlarmToggle,
  onDismissAlarm,
}: ControlPanelProps) {
  const handleLimitDecrease = () => {
    if (whistleLimit > 1) {
      onLimitChange(whistleLimit - 1);
    }
  };

  const handleLimitIncrease = () => {
    if (whistleLimit < 20) {
      onLimitChange(whistleLimit + 1);
    }
  };

  return (
    <div className="w-full max-w-sm space-y-6">
      {/* Alarm Triggered Overlay */}
      {isAlarmTriggered && (
        <div className="glass-panel rounded-2xl p-4 bg-destructive/10 border-destructive/20 animate-scale-in">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-destructive/20 flex items-center justify-center">
                <Bell className="w-5 h-5 text-destructive animate-pulse" />
              </div>
              <div>
                <p className="font-semibold text-destructive">Target Reached!</p>
                <p className="text-xs text-muted-foreground">Tap to dismiss</p>
              </div>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={onDismissAlarm}
              className="border-destructive/30 text-destructive hover:bg-destructive/10"
            >
              Dismiss
            </Button>
          </div>
        </div>
      )}

      {/* Main Control Button */}
      <div className="flex justify-center">
        <Button
          size="lg"
          onClick={isListening ? onStopListening : onStartListening}
          className={cn(
            "w-full h-16 rounded-2xl text-lg font-semibold transition-all duration-300",
            "shadow-glass-lg",
            isListening 
              ? "bg-secondary text-secondary-foreground hover:bg-secondary/80" 
              : "bg-primary text-primary-foreground hover:bg-primary/90"
          )}
        >
          {isListening ? (
            <>
              <MicOff className="w-6 h-6 mr-3" />
              Stop Listening
            </>
          ) : (
            <>
              <Mic className="w-6 h-6 mr-3" />
              Start Listening
            </>
          )}
        </Button>
      </div>

      {/* Settings Panel */}
      <div className="glass-panel rounded-2xl p-5 space-y-5">
        {/* Whistle Limit Stepper */}
        <div className="flex items-center justify-between">
          <div>
            <p className="font-medium text-foreground">Whistle Limit</p>
            <p className="text-xs text-muted-foreground">Alert when reached</p>
          </div>
          <div className="flex items-center gap-3">
            <Button
              variant="outline"
              size="icon"
              onClick={handleLimitDecrease}
              disabled={whistleLimit <= 1}
              aria-label="Decrease whistle target"
              className="w-10 h-10 rounded-xl"
            >
              <Minus className="w-4 h-4" />
            </Button>
            <span className="w-8 text-center text-xl font-semibold text-foreground">
              {whistleLimit}
            </span>
            <Button
              variant="outline"
              size="icon"
              onClick={handleLimitIncrease}
              disabled={whistleLimit >= 20}
              aria-label="Increase whistle target"
              className="w-10 h-10 rounded-xl"
            >
              <Plus className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Divider */}
        <div className="h-px bg-border/50" />

        {/* Alarm Toggle */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            {isAlarmEnabled ? (
              <Bell className="w-5 h-5 text-primary" />
            ) : (
              <BellOff className="w-5 h-5 text-muted-foreground" />
            )}
            <div>
              <p className="font-medium text-foreground">Alarm Sound</p>
              <p className="text-xs text-muted-foreground">Play when limit reached</p>
            </div>
          </div>
          <Switch
            checked={isAlarmEnabled}
            onCheckedChange={onAlarmToggle}
            aria-label="Alarm sound"
          />
        </div>

        {/* Divider */}
        <div className="h-px bg-border/50" />

        {/* Reset Button */}
        <Button
          variant="ghost"
          onClick={onReset}
          className="w-full h-12 rounded-xl text-muted-foreground hover:text-foreground"
        >
          <RotateCcw className="w-4 h-4 mr-2" />
          Reset Counter
        </Button>
      </div>
    </div>
  );
}

import { useEffect } from "react";
import { ArrowDown, ArrowRight, ShieldCheck, Smartphone, Volume2 } from "lucide-react";
import { Link } from "react-router-dom";
import { useWhistleDetector } from "@/hooks/useWhistleDetector";
import { useAlarm } from "@/hooks/useAlarm";
import { WhistleCounter } from "@/components/WhistleCounter";
import { ControlPanel } from "@/components/ControlPanel";
import { AudioVisualizer } from "@/components/AudioVisualizer";
import { Seo } from "@/components/Seo";
import { toast } from "@/hooks/use-toast";

export default function Index() {
  const { isListening, whistleCount, currentFrequency, currentAmplitude, isWhistleDetected, error, startListening, stopListening, resetCount } = useWhistleDetector();
  const { isAlarmEnabled, setIsAlarmEnabled, isAlarmTriggered, whistleLimit, setWhistleLimit, triggerAlarm, resetAlarm } = useAlarm();

  useEffect(() => {
    if (whistleCount >= whistleLimit && isListening && !isAlarmTriggered) {
      triggerAlarm();
      stopListening();
    }
  }, [whistleCount, whistleLimit, isListening, isAlarmTriggered, triggerAlarm, stopListening]);

  useEffect(() => {
    if (error) toast({ title: "Microphone unavailable", description: error, variant: "destructive" });
  }, [error]);

  const handleReset = () => { resetCount(); resetAlarm(); if (isListening) stopListening(); };

  return (
    <>
      <Seo title="Whistle Counter | A little help in the kitchen" description="Count pressure cooker whistles with a simple browser-based audio helper. Set a target, keep cooking, and get an alert when you reach it." />
      <section className="hero-section" aria-labelledby="hero-title">
        <div className="hero-copy">
          <span className="eyebrow"><span className="eyebrow-dot" /> YOUR KITCHEN SIDEKICK</span>
          <h1 id="hero-title">Keep cooking.<br /><span>We’ll count.</span></h1>
          <p className="hero-intro">A little help keeping track of pressure cooker whistles, so you can get on with everything else happening in the kitchen.</p>
        <a className="hero-link" href="#counter">Try the whistle counter <ArrowDown size={17} aria-hidden="true" /></a>
          <div className="hero-proof"><span><ShieldCheck size={16} aria-hidden="true" /> Audio stays on this device</span><span><Smartphone size={16} aria-hidden="true" /> Works in your browser</span></div>
        </div>
        <div className="hero-art" aria-hidden="true">
          <div className="sun-disc" />
          <div className="steam steam-one" /><div className="steam steam-two" /><div className="steam steam-three" />
          <div className="pot"><div className="pot-lid" /><div className="pot-handle handle-left" /><div className="pot-handle handle-right" /><div className="pot-body"><div className="pot-shine" /><div className="pot-band" /></div><div className="pot-base" /></div>
          <div className="art-caption">A calmer kind of multitasking</div>
        </div>
      </section>

      <section id="counter" className="counter-section" aria-labelledby="counter-title">
        <div className="section-heading">
          <span className="eyebrow">THE COUNTER</span>
          <h2 id="counter-title">Set a target. Stay in the flow.</h2>
          <p>Allow microphone access to listen for whistle-like sounds. You’re always in control of when listening starts and stops.</p>
        </div>
        <div className="counter-card">
          <WhistleCounter count={whistleCount} limit={whistleLimit} isActive={isListening} isWhistleDetected={isWhistleDetected} isAlarmTriggered={isAlarmTriggered} />
          <div className="counter-tools">
            <AudioVisualizer amplitude={currentAmplitude} frequency={currentFrequency} isActive={isListening} />
            <ControlPanel isListening={isListening} whistleLimit={whistleLimit} isAlarmEnabled={isAlarmEnabled} isAlarmTriggered={isAlarmTriggered} onStartListening={startListening} onStopListening={stopListening} onReset={handleReset} onLimitChange={setWhistleLimit} onAlarmToggle={setIsAlarmEnabled} onDismissAlarm={resetAlarm} />
          </div>
        </div>
        <p className="privacy-note"><Volume2 size={15} aria-hidden="true" /> Your microphone is used only while the counter is listening. Audio is processed in your browser and is not uploaded.</p>
      </section>

      <section className="steps-section" aria-labelledby="steps-title">
        <div className="section-heading compact-heading"><span className="eyebrow">THREE SIMPLE STEPS</span><h2 id="steps-title">More hands for the rest of dinner.</h2></div>
        <div className="steps-grid">
          <article className="step-card"><span className="step-number">01</span><h3>Choose your count</h3><p>Set the whistle target that suits your recipe and cooker.</p></article>
          <article className="step-card"><span className="step-number">02</span><h3>Start listening</h3><p>Keep this page open and allow microphone access when prompted.</p></article>
          <article className="step-card"><span className="step-number">03</span><h3>Get a nudge</h3><p>The counter alerts you when it reaches your chosen number.</p></article>
        </div>
        <Link className="text-link" to="/how-it-works">See how the counter works <ArrowRight size={16} aria-hidden="true" /></Link>
      </section>

      <aside className="safety-note"><span className="safety-icon"><ShieldCheck aria-hidden="true" /></span><div><h2>A helpful reminder, not a replacement for attention.</h2><p>Stay in the kitchen, follow your pressure cooker’s instructions, and use your own judgement. Sound detection can miss a whistle or count another sound by mistake.</p></div></aside>
    </>
  );
}

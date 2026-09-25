import { Activity, BellRing, Mic, ShieldCheck } from "lucide-react";
import { Link } from "react-router-dom";
import { Seo } from "@/components/Seo";

const steps = [
  { icon: Mic, title: "You choose when to listen", body: "Press Start Listening and allow microphone access. The browser uses the sound around your device while the counter is active." },
  { icon: Activity, title: "The browser checks the sound", body: "A lightweight signal check looks for strong sounds in a whistle-like frequency range. Kitchen acoustics and device microphones vary, so results are an estimate." },
  { icon: BellRing, title: "The counter keeps your place", body: "A detected whistle increments the count. After your selected target, the counter stops listening and shows an alert." },
];

export default function HowItWorks() {
  return <>
    <Seo title="How it works | Whistle Counter" description="Learn how Whistle Counter listens for pressure cooker whistles, what happens to microphone audio, and where sound detection can fall short." />
    <section className="page-hero"><span className="eyebrow">A SIMPLE KITCHEN TOOL</span><h1>How it works</h1><p>Whistle Counter uses your device’s microphone to help you keep track. Here’s what happens when you press start.</p></section>
    <section className="content-section steps-detail">
      {steps.map(({ icon: Icon, title, body }, index) => <article className="detail-step" key={title}><span className="detail-icon"><Icon aria-hidden="true" /></span><span className="step-number">STEP 0{index + 1}</span><h2>{title}</h2><p>{body}</p></article>)}
    </section>
    <section className="content-callout"><ShieldCheck aria-hidden="true" /><div><h2>Your audio stays in your browser</h2><p>The app analyses microphone input on your device. It does not record or upload audio. Listening ends when you press Stop or when your target is reached.</p></div></section>
    <section className="content-section prose"><h2>A note about accuracy</h2><p>This is a helpful counter, not a certified cooking timer or safety device. Background noise, distance, room acoustics, and microphone differences can affect detection. Check the counter occasionally, stay nearby, and follow the cooker manufacturer’s instructions.</p><Link className="text-link" to="/faq">Read common questions <span aria-hidden="true">→</span></Link></section>
  </>;
}

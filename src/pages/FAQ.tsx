import { Link } from "react-router-dom";
import { Seo } from "@/components/Seo";

const questions = [
  ["Does it record or upload my audio?", "No. Audio is analysed in your browser while listening is active. It is not recorded or uploaded by this app."],
  ["Why does it need microphone access?", "The counter needs live audio input to check for whistle-like sounds. Your browser will ask before granting access. You can stop listening at any time."],
  ["Will it catch every whistle?", "It may miss a whistle or mistake another sound for one. Phone microphones, distance, background noise, and room acoustics all affect detection. Treat the count as a reminder, not a guarantee."],
  ["Why did my browser block the microphone?", "Microphone access generally requires a secure connection (HTTPS) and your permission. Check the site permissions in your browser, then try again."],
  ["Can I use it with the screen locked or another app open?", "Keep this page open and visible for the most reliable results. Browsers may pause microphone processing when a tab is backgrounded or the device sleeps."],
  ["How do I get help?", "A support email or contact destination has not been configured for this project yet. Add a real support address before publishing if you want visitors to contact you."],
];

export default function FAQ() {
  return <>
    <Seo title="FAQ | Whistle Counter" description="Answers about microphone access, privacy, sound detection accuracy, and using the Whistle Counter pressure cooker helper." />
    <section className="page-hero"><span className="eyebrow">GOOD TO KNOW</span><h1>Questions, answered.</h1><p>A few practical details to help you use the counter with confidence.</p></section>
    <section className="content-section faq-list">{questions.map(([question, answer]) => <details className="faq-item" key={question}><summary>{question}<span aria-hidden="true">+</span></summary><p>{answer}</p></details>)}</section>
    <section className="faq-cta"><h2>Ready to keep count?</h2><p>Set a target and start listening whenever you’re ready.</p><Link className="button-link" to="/#counter">Open the counter <span aria-hidden="true">→</span></Link></section>
  </>;
}

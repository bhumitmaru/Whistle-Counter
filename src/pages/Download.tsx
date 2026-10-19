import { BellRing, Download as DownloadIcon, Gauge, Lock, PackageCheck, ShieldCheck, Smartphone } from "lucide-react";
import { Link } from "react-router-dom";
import { Seo } from "@/components/Seo";

const APK_FILE = "Whistle-Counter-v1.0.0.apk";
const APK_URL = `${import.meta.env.BASE_URL}apk/${APK_FILE}`;
const REPO_APK_URL = `https://github.com/bhumitmaru/Whistle-Counter/raw/main/public/apk/${APK_FILE}`;

const apkFacts = [
  { icon: PackageCheck, label: "Version", value: "1.0.0" },
  { icon: Gauge, label: "Size", value: "1 MB" },
  { icon: Smartphone, label: "Requires", value: "Android 8.0+" },
];

const installSteps = [
  { icon: DownloadIcon, title: "Download the APK", body: "Tap the download button above and wait for the file to finish transferring to your phone." },
  { icon: Lock, title: "Allow the install", body: "Open the file from your notifications or Downloads folder. Android will ask you to allow installs from your browser — that is normal for an app downloaded outside an app store." },
  { icon: BellRing, title: "Open Whistle Counter", body: "Set your whistle target, press start, and let the app listen in the background — screen off, other apps open, alarm ready." },
];

export default function Download() {
  return <>
    <Seo title="Download the app | Whistle Counter" description="Get the Whistle Counter Android app. Whistle detection, background listening, and a full alarm even when the screen is off." />
    <section className="page-hero" aria-labelledby="download-title">
      <span className="eyebrow">ANDROID APP</span>
      <h1 id="download-title">Whistles, counted<br /><span>in your pocket.</span></h1>
      <p>The same friendly counter, packaged as a small app. It keeps listening when your screen is off and rings the phone’s alarm when your cooker reaches its target.</p>
      <div className="download-actions">
        <a className="button-link download-cta" href={APK_URL} download>
          <DownloadIcon size={17} aria-hidden="true" /> Download APK <span className="download-size">· 1 MB</span>
        </a>
        <span className="download-fallback">Direct download not working? <a href={REPO_APK_URL}>Get the file on GitHub</a>.</span>
      </div>
    </section>

    <section className="content-section">
      <div className="download-card" aria-label="App details">
        <span className="download-card-icon"><PackageCheck aria-hidden="true" /></span>
        <dl className="download-facts">
          {apkFacts.map(({ icon: Icon, label, value }) => (
            <div className="download-fact" key={label}><dt><Icon size={14} aria-hidden="true" /> {label}</dt><dd>{value}</dd></div>
          ))}
        </dl>
      </div>
    </section>

    <section className="content-section steps-detail" aria-label="How to install">
      {installSteps.map(({ icon: Icon, title, body }, index) => (
        <article className="detail-step" key={title}>
          <span className="detail-icon"><Icon aria-hidden="true" /></span>
          <span className="step-number">STEP 0{index + 1}</span>
          <h2>{title}</h2>
          <p>{body}</p>
        </article>
      ))}
    </section>

    <section className="content-callout">
      <ShieldCheck aria-hidden="true" />
      <div>
        <h2>Everything stays on your phone</h2>
        <p>The app runs entirely on your device. No account, no audio uploads, and whistle counting keeps working with the screen off or another app in front.</p>
      </div>
    </section>

    <section className="content-section prose">
      <h2>Why an app?</h2>
      <p>Browsers pause microphone work whenever a tab is backgrounded or the device sleeps, which makes them awkward companions for a cooker that takes twenty minutes. The Android app uses a foreground service instead: it keeps listening, tracks the count, and then switches to a full alarm with vibration when your target is reached.</p>
      <p>This release is signed for personal use, so Android will show one extra confirmation during install. It is normal, safe, and part of installing outside the Play Store. If you prefer not to install anything, the <Link to="/" className="text-inline-link">browser counter</Link> still works just as it always has.</p>
      <Link className="button-link" to="/#counter">Use the browser counter <span aria-hidden="true">→</span></Link>
    </section>
  </>;
}

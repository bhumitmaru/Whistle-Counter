import { Link } from "react-router-dom";
import { Seo } from "@/components/Seo";

export default function About() {
  return <>
    <Seo title="About | Whistle Counter" description="Whistle Counter is a small browser-based kitchen helper designed to make it easier to keep track of pressure cooker whistles." />
    <section className="page-hero"><span className="eyebrow">ABOUT WHISTLE COUNTER</span><h1>A little less<br />keeping track.</h1><p>Pressure cooker whistles are easy to lose count of when you’re cooking, helping, and doing three other things. This small tool is here to take one thing off your mental list.</p></section>
    <section className="content-section prose about-prose"><h2>Made for the rhythm of a real kitchen</h2><p>Set the number you’re watching for, start the counter, and let it listen while you work. No account is needed. The counter runs in your browser and only listens after you choose to start it.</p><h2>Designed to assist, not decide</h2><p>Every stove, cooker, room, and phone sounds a little different. Whistle Counter can be useful, but it cannot guarantee that every whistle is heard correctly. Keep an eye on your cooking and follow the guidance that came with your pressure cooker.</p><Link className="button-link" to="/#counter">Try the counter <span aria-hidden="true">→</span></Link></section>
  </>;
}

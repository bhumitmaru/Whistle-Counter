import { useEffect } from "react";

type SeoProps = { title: string; description: string };

export function Seo({ title, description }: SeoProps) {
  useEffect(() => {
    document.title = title;
    const setMeta = (selector: string, attribute: string, value: string) => {
      const element = document.querySelector<HTMLMetaElement>(selector);
      element?.setAttribute(attribute, value);
    };
    setMeta('meta[name="description"]', "content", description);
    setMeta('meta[property="og:title"]', "content", title);
    setMeta('meta[property="og:description"]', "content", description);
    setMeta('meta[name="twitter:title"]', "content", title);
    setMeta('meta[name="twitter:description"]', "content", description);
    setMeta('meta[property="og:url"]', "content", window.location.href);
    const socialImagePath = `${import.meta.env.BASE_URL}og-image.jpg`;
    setMeta('meta[property="og:image"]', "content", new URL(socialImagePath, window.location.origin).href);
    setMeta('meta[name="twitter:image"]', "content", new URL(socialImagePath, window.location.origin).href);
    let canonical = document.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    if (!canonical) {
      canonical = document.createElement("link");
      canonical.rel = "canonical";
      document.head.append(canonical);
    }
    canonical.href = window.location.href.split("#")[0];
  }, [title, description]);
  return null;
}

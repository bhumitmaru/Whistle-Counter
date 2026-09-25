import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";

const pages = [
  { path: "/", title: "Whistle Counter | A little help in the kitchen", description: "Count pressure cooker whistles with a simple browser-based audio helper. Set a target, keep cooking, and get an alert when you reach it." },
  { path: "/how-it-works", title: "How it works | Whistle Counter", description: "Learn how Whistle Counter listens for pressure cooker whistles, what happens to microphone audio, and where sound detection can fall short." },
  { path: "/about", title: "About | Whistle Counter", description: "Whistle Counter is a small browser-based kitchen helper designed to make it easier to keep track of pressure cooker whistles." },
  { path: "/faq", title: "FAQ | Whistle Counter", description: "Answers about microphone access, privacy, sound detection accuracy, and using the Whistle Counter pressure cooker helper." },
];

const basePath = `/${(process.env.BASE_PATH || "/").replace(/^\/+|\/+$/g, "")}`.replace(/^\/$/, "/");
const defaultOrigin = "https://YOUR-DOMAIN.example";
const siteUrl = (process.env.SITE_URL || `${defaultOrigin}${basePath === "/" ? "" : basePath}`).replace(/\/$/, "");
const template = await readFile("dist/index.html", "utf8");
for (const page of pages) {
    const canonical = `${siteUrl}${page.path}`;
    const image = `${siteUrl}/og-image.jpg`;
    const html = template
      .replace(/<title>[^<]*<\/title>/, `<title>${page.title}</title>`)
      .replace(/(<meta name="description" content=")[^"]*("\s*\/?>)/, `$1${page.description}$2`)
      .replace(/(<meta property="og:title" content=")[^"]*("\s*\/?>)/, `$1${page.title}$2`)
      .replace(/(<meta property="og:description" content=")[^"]*("\s*\/?>)/, `$1${page.description}$2`)
      .replace(/(<meta property="og:url" content=")[^"]*("\s*\/?>)/, `$1${canonical}$2`)
      .replace(/(<meta property="og:image" content=")[^"]*("\s*\/?>)/, `$1${image}$2`)
      .replace(/(<meta name="twitter:title" content=")[^"]*("\s*\/?>)/, `$1${page.title}$2`)
      .replace(/(<meta name="twitter:description" content=")[^"]*("\s*\/?>)/, `$1${page.description}$2`)
      .replace(/(<meta name="twitter:image" content=")[^"]*("\s*\/?>)/, `$1${image}$2`)
      .replace("</head>", `  <link rel="canonical" href="${canonical}" />\n  </head>`);
    const output = page.path === "/" ? "dist/index.html" : `dist${page.path}/index.html`;
    await mkdir(path.dirname(output), { recursive: true });
    await writeFile(output, html);
}
const sitemap = `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${pages.map(({ path: route }) => `  <url><loc>${siteUrl}${route}</loc></url>`).join("\n")}\n</urlset>\n`;
await writeFile("dist/sitemap.xml", sitemap);
await writeFile("dist/robots.txt", `User-agent: *\nAllow: /\nSitemap: ${siteUrl}/sitemap.xml\n`);

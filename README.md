# Whistle Counter

Count pressure cooker whistles with a simple browser-based kitchen helper. Set a target, let the counter listen while you cook, and get an alert when you reach it. Audio is processed on your device; sound detection can miss or miscount whistles.

## What it does

- Counts whistle-like sounds through your device’s microphone.
- Lets you choose a target and alerts you when the count reaches it.
- Runs in the browser without an account.
- Processes audio locally; the app does not record or upload microphone audio.
- Includes Home, How it works, About, and FAQ pages.

## Important to know

Whistle Counter is a cooking aid, not a timer or safety device. Background noise, room acoustics, distance, and microphone differences can affect detection. Keep the page open while listening, stay near your cooker, and follow the cooker manufacturer’s instructions. Microphone access requires permission and a secure connection (HTTPS) outside localhost.

## Run locally

Install [Node.js](https://nodejs.org/) and then run:

```sh
npm ci
npm run dev
```

To make and inspect a production build locally:

```sh
npm run build
npm run preview
```

The finished static site is written to `dist/`.

## Deploy to GitHub Pages

This repository includes a GitHub Actions workflow at `.github/workflows/deploy.yml`. It builds and deploys the site when changes are pushed to `main`, or when the workflow is run manually. It detects the repository name and configures the correct paths for both project sites and `username.github.io` sites.

1. Put the **contents** of this project folder in the root of your GitHub repository, including the hidden `.github` folder.
2. If your default branch is not `main`, update the branch name in `.github/workflows/deploy.yml`.
3. In GitHub, open **Settings → Pages** and set the source to **GitHub Actions**.
4. Push to `main`, then check the **Actions** tab for the deployment result and published link.

For a custom domain, set it in **Settings → Pages**, then update the workflow’s `site_url` to your domain and `base_path` to `/`. Point the domain’s DNS records to GitHub Pages and enable HTTPS.

## Other static hosts

Build with your site’s public origin so canonical links and the sitemap use the correct address:

```sh
SITE_URL=https://your-domain.com npm run build
```

The build generates route-specific page metadata, `sitemap.xml`, and `robots.txt`. Without `SITE_URL`, those files contain a `YOUR-DOMAIN.example` placeholder and need a production URL before publishing. The `public/_redirects` file provides a fallback rule for Netlify.

## Built by

[Bhumit Maru](https://www.linkedin.com/in/bhumitmaru/)

## License

No license has been selected for this project yet. Until one is added, others do not have permission to reuse, modify, or distribute the code just because the repository is public.

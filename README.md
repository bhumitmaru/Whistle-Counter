# Whistle Counter

A browser-based and Android application that automatically detects and counts pressure cooker whistles using your device's microphone. Set a target number of whistles, start listening, and get an alert when you reach it.

> **Note:** Whistle Counter is a cooking aid, not a timer or safety device. Sound detection may occasionally miss or miscount whistles depending on background noise, room acoustics, distance, and microphone quality.

**[Try it in your browser →](https://bhumitmaru.github.io/Whistle-Counter/)** · **[Download the Android app →](https://bhumitmaru.github.io/Whistle-Counter/download)**

## Features

- Automatic whistle detection using the device microphone
- Real-time whistle counting with a custom target
- Alert when the target count is reached (web) / full alarm with vibration (Android)
- Android app keeps listening with the screen off via a foreground service
- Audio is processed locally — never recorded or uploaded
- No account required
- Built-in usage guide and FAQ

## What's in this repo

| Path | What it is |
| --- | --- |
| `src/` | Web app — React + TypeScript + Vite |
| `android/` | Native Android app — Kotlin + Jetpack Compose (see [android/README.md](android/README.md)) |
| `public/apk/` | Pre-built release APK (`Whistle-Counter-v1.0.0.apk`) |
| `scripts/` | Post-build prerendering for GitHub Pages SEO |
| `.github/workflows/` | Deploy-to-GitHub-Pages workflow |

## Web app

### Getting started

Requirements: Node.js 20+ and a browser with microphone access.

```bash
git clone https://github.com/bhumitmaru/Whistle-Counter.git
cd Whistle-Counter
npm ci
npm run dev      # dev server at http://localhost:8080
```

### Build and deploy

```bash
npm run build    # outputs to dist/ and prerenders each route
npm run preview  # serve the production build locally
```

Pushes to `main` are built and published to GitHub Pages automatically via [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml).

## Android app

The ready-to-install APK lives at `public/apk/Whistle-Counter-v1.0.0.apk` and is served from the website's [download page](https://bhumitmaru.github.io/Whistle-Counter/download). Requires Android 8.0 (API 26) or newer.

Build it yourself:

```bash
cd android
./gradlew :app:assembleRelease      # APK at app/build/outputs/apk/release/
./gradlew :app:testDebugUnitTest    # detection-logic unit tests
```

Requirements: JDK 17, Android SDK (Platform 35).

## How detection works

- 44.1 kHz mono capture, 2048-sample FFT (Hann windowed, smoothed 0.3)
- Whistle band **2–4.5 kHz**
- Detects when band energy ratio ≥ **0.15** of total energy and normalised peak ≥ **0.30**
- **10 s cooldown** between counted whistles

## Tech stack

React · TypeScript · Vite · Tailwind CSS · React Router · Web Audio API · Kotlin · Jetpack Compose

## License

[MIT](LICENSE)

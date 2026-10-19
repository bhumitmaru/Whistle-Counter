# Whistle Counter — Android App

Native Android version of [Whistle Counter](../README.md) built with **Kotlin + Jetpack Compose**.
Same warm UI, same detection logic — but it keeps listening when the screen is off and rings your
phone's alarm when the target is reached. Everything runs locally on the device.

## Install

The ready-to-install APK ships with this repository at `public/apk/Whistle-Counter-v1.0.0.apk` and is
downloadable from the website's [download page](https://bhumitmaru.github.io/Whistle-Counter/download).

Copy it to your phone (or `adb install Whistle-Counter-v1.0.0.apk`) and install.
Requires **Android 8.0 (API 26)** or newer.

> The release APK is signed with the debug key for personal use. Use your own keystore before
> publishing.

## Build it yourself

```bash
cd android
./gradlew :app:assembleRelease        # APK at app/build/outputs/apk/release/
./gradlew :app:testDebugUnitTest      # detection-logic unit tests
```

Requirements: JDK 17, Android SDK (Platform 35). `local.properties` points at the SDK.

## How the background listening works

| Concern | How it is handled |
| --- | --- |
| Screen off / locked / other app | A **foreground service** (`foregroundServiceType="microphone"`) owns the mic, plus a partial wake lock so the CPU keeps processing |
| Persistent notification | Android requires it for background microphone use; shows live count and a Stop action |
| Target reached | Mic stops, the service switches to a **mediaPlayback** foreground state, plays the phone's own alarm ringtone on the **alarm audio stream**, vibrates, and posts a full-screen alarm notification |
| Lock screen | Full-screen intent wakes the screen; the alarm screen stays visible over the lock screen |
| Safety | The alarm auto-silences after 5 minutes if dismissed |
| Battery limits | The reliability card checks battery-optimization, notification, and full-screen-intent permissions and deep-links to fix them |

> **Note:** no app can run while the phone is powered off. "Screen-off" means locked/sleeping,
> which is fully supported.

## Detection logic (same as the web app)

- 44.1 kHz mono capture, 2048-sample FFT (Hann windowed, smoothed 0.3)
- Whistle band **2–4.5 kHz**
- Detect when band energy ratio ≥ **0.15** of total energy and normalised peak ≥ **0.30**
- **10 s cooldown** between counted whistles
- Whistle target 1–20, alarm sound toggle, reset — all persisted locally

The pure-Kotlin detector (`WhistleSignalProcessor`) is unit tested:
see `app/src/test/java/.../WhistleSignalProcessorTest.kt`.

## Project layout

```
app/src/main/java/com/whistlecounter/app/
├── MainActivity.kt              # Compose host + alarm wake/lock-screen handling
├── audio/
│   ├── Fft.kt                   # radix-2 FFT
│   ├── WhistleSignalProcessor.kt# pure detection logic (unit tested)
│   ├── WhistleDetector.kt       # AudioRecord loop feeding the processor
│   └── AlarmPlayer.kt           # alarm ringtone + vibration + tone fallback
├── data/                        # shared UI/service state + preferences
├── service/
│   ├── WhistleService.kt        # foreground service (mic → alarm)
│   ├── NotificationHelper.kt    # listening / alarm / error notifications
│   └── CounterController.kt     # UI → service commands
└── ui/                          # Compose UI (Counter, Guide, FAQ, About)
```

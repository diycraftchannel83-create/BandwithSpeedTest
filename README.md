# Bandwidth Speed Test — Wins Mediatama

Android bandwidth measurement application with a Cloudflare-backed web measurement engine and a Cloudflare Pages site.

## Architecture
- Android: Kotlin + Jetpack Compose shell, package `com.winsmediatama.speedtest`.
- Measurement: Cloudflare's open-source `@cloudflare/speedtest` engine, which measures against Cloudflare's edge network.
- Pages: static web app in `pages/`, deployable to Cloudflare Pages Free.
- Backend (optional later): ByetHost PHP/MySQL for user accounts and saved test history. It is not used as the throughput measurement server.

## Measurement integrity
- Display measured values without artificial multipliers or score-based inflation.
- Convert bits per second to decimal Mbps using 1,000,000.
- Show the measurement endpoint/provider and disclose that Cloudflare receives measurement telemetry.
- A result is a measurement to Cloudflare's edge, not a guaranteed measure of every route or every ISP destination.
- The Cloudflare engine may consume substantial mobile data (Cloudflare states a test can use up to 200 MB). Show a data-use warning before starting.

## Cloudflare Pages deployment
1. In Cloudflare Dashboard, open Workers & Pages → Create → Pages → Connect to Git.
2. Select this repository and branch `main`.
3. Framework preset: None.
4. Build command: leave empty.
5. Build output directory: `pages`.
6. Deploy. Static assets on Pages Free are free; Pages Functions, if added later, have Workers quotas.

## Android build
Open the repository in Android Studio and sync Gradle. The first commit establishes the project scaffold; run a build before distributing an APK/AAB.

## Privacy
See [Privacy Policy](pages/privacy.html). Cloudflare's speed test engine documents that measurement results are collected for aggregated Internet quality insights.

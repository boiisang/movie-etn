# Joel Stream

Joel Stream is a clean native Android media catalog and video player developed by Joel Dongthansang.

It is designed for authorized sample, public-domain, open, or otherwise properly licensed video streams. The app is self-contained and does not depend on private media backends, paid unlock systems, advertising networks, remote configuration services, or third-party account services.

## Features

- Clean media catalog and native video playback.
- Local curated catalog in Java source.
- Search and saved-library views.
- Settings/About screen with content-rights and privacy notes.
- No monetization screens, payment flows, analytics SDKs, or remote config dependencies.
- No Firebase, CodePush, TMDB, Trakt, or third-party content backend.
- Only `INTERNET` and `ACCESS_NETWORK_STATE` Android permissions.

## Content Policy

The bundled catalog uses public sample/open media URLs for testing. Add only content that you own, created, licensed, or have permission to distribute.

This repository does not include copyrighted movie catalogs, private API keys, unauthorized streaming endpoints, APK signing keys, or generated build artifacts.

## Project Structure

```text
app/src/main/
  AndroidManifest.xml
  java/com/joel/stream/
  res/
docs/
scripts/
```

Generated folders such as `build`, `builds`, `deliverables`, `logs`, `reports`, and `emulator` are intentionally ignored by git.

## Build

Run:

```powershell
.\scripts\build_apk.ps1
```

The final signed internal APK is copied locally to `deliverables\latest`. Build outputs and signing material are not committed.

## Copyright

Copyright (c) 2026 Joel Dongthansang. All rights reserved.

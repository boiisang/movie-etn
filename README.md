# Movie Etna

Movie Etna is a clean native Android media catalog and video player developed by Joel Dongthansang.

It is designed for authorized sample, public-domain, open, or otherwise properly licensed video streams. The app can load live movie and series metadata from TMDB when a local API config is present, while playback remains limited to bundled authorized sample/open streams unless you add licensed media URLs.

## Features

- Clean media catalog and native video playback.
- Live TMDB movie/series metadata when `app/src/main/assets/tmdb_config.json` is present.
- Offline fallback catalog in Java source.
- Search and saved-library views.
- Settings/About screen with content-rights and privacy notes.
- No monetization screens, payment flows, analytics SDKs, or remote config dependencies.
- No Firebase, CodePush, Trakt, ad network, billing SDK, or private content backend.
- Only `INTERNET` and `ACCESS_NETWORK_STATE` Android permissions.

## Content Policy

TMDB is used only for metadata. The bundled playback URLs use public sample/open media for testing. Add only content that you own, created, licensed, or have permission to distribute.

This repository does not include copyrighted movie catalogs, private API keys, unauthorized streaming endpoints, APK signing keys, or generated build artifacts. The tracked `tmdb_config.example.json` shows the expected local config format. The real `tmdb_config.json` is git-ignored.

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

Movie Etna and its app source are maintained by Joel Dongthansang. Third-party sample/open media remains subject to its respective licenses and owners.

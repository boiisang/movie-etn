# Content License Notes

This app is designed as a legal internal/demo media app. It does not connect to private content backends, paid media services, remote configuration systems, analytics SDKs, or advertising networks.

The default catalog uses direct HTTPS sample/open-media streams that are commonly used for playback validation:

- W3C open media examples
- MDN CC0 video samples
- Short public sample MP4 clips for player validation

Before publishing publicly, replace or verify every media URL against your own content licenses. The app code intentionally keeps catalog data in `Catalog.java` so future maintenance is explicit and reviewable.

# Content License Notes

Movie Etna is designed as a legal internal/demo media app. It does not connect to private content backends, paid media services, remote configuration systems, analytics SDKs, or advertising networks.

TMDB integration is limited to movie and series metadata such as title, year, overview, and media type. TMDB metadata is not a video license and does not provide permission to stream full movies or series.

The default catalog uses direct HTTPS streams from public-domain, Creative Commons, and open-media sources:

- Internet Archive public-domain and open-media files
- Blender Foundation / Blender open movie project Creative Commons films
- W3C and MDN open/CC0 sample clips for fallback validation

Before publishing publicly, replace or verify every media URL against your own content licenses. The app code intentionally keeps fallback catalog data in `Catalog.java`, and the live metadata client in `TmdbClient.java`, so future maintenance is explicit and reviewable.

package com.joel.stream;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;

final class TmdbClient {
    private static final String API = "https://api.themoviedb.org/3";

    private TmdbClient() {}

    static MediaItem[] trending(TmdbConfig config) throws Exception {
        return fetchItems(config, API + "/trending/all/week?language=en-US", "Trending");
    }

    static MediaItem[] search(TmdbConfig config, String query) throws Exception {
        String encoded = URLEncoder.encode(query, "UTF-8");
        return fetchItems(config, API + "/search/multi?language=en-US&include_adult=false&query=" + encoded, "Search");
    }

    private static MediaItem[] fetchItems(TmdbConfig config, String endpoint, String source) throws Exception {
        String json = get(config, endpoint);
        JSONArray results = new JSONObject(json).optJSONArray("results");
        ArrayList<MediaItem> items = new ArrayList<MediaItem>();
        if (results == null) return new MediaItem[0];
        int limit = Math.min(results.length(), 16);
        for (int i = 0; i < limit; i++) {
            JSONObject obj = results.optJSONObject(i);
            if (obj == null) continue;
            String mediaType = obj.optString("media_type", "movie");
            if (!"movie".equals(mediaType) && !"tv".equals(mediaType)) continue;
            String title = firstNonEmpty(obj.optString("title"), obj.optString("name"), "Untitled");
            String release = firstNonEmpty(obj.optString("release_date"), obj.optString("first_air_date"), "");
            String year = release.length() >= 4 ? release.substring(0, 4) : "TMDB";
            String overview = obj.optString("overview");
            if (overview.trim().length() == 0) {
                overview = "Metadata loaded from TMDB. Playback uses authorized sample streams in this internal build.";
            }
            String category = "tv".equals(mediaType) ? "TMDB Series" : "TMDB Movie";
            int colorA = colorFor(i, 0);
            int colorB = colorFor(i, 1);
            items.add(new MediaItem(
                title,
                category,
                year,
                "sample play",
                overview,
                SampleStreams.forIndex(i),
                colorA,
                colorB
            ));
        }
        return items.toArray(new MediaItem[0]);
    }

    private static String get(TmdbConfig config, String endpoint) throws Exception {
        String url = endpoint;
        if (config.readAccessToken.length() == 0 && config.apiKey.length() > 0) {
            url += endpoint.indexOf('?') >= 0 ? "&" : "?";
            url += "api_key=" + URLEncoder.encode(config.apiKey, "UTF-8");
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(12000);
        conn.setReadTimeout(12000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (config.readAccessToken.length() > 0) {
            conn.setRequestProperty("Authorization", "Bearer " + config.readAccessToken);
        }
        int code = conn.getResponseCode();
        InputStream in = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
        String body = readFully(in);
        conn.disconnect();
        if (code < 200 || code >= 300) {
            throw new IllegalStateException("TMDB request failed with HTTP " + code);
        }
        return body;
    }

    private static String readFully(InputStream in) throws Exception {
        if (in == null) return "";
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), "UTF-8");
        } finally {
            in.close();
        }
    }

    private static String firstNonEmpty(String a, String b, String fallback) {
        if (a != null && a.trim().length() > 0) return a.trim();
        if (b != null && b.trim().length() > 0) return b.trim();
        return fallback;
    }

    private static int colorFor(int index, int offset) {
        int[] colors = new int[] {
            0xfff25f4c, 0xff5cc8ff, 0xff7d6cff, 0xffff6ca8,
            0xff3ddc97, 0xff2077ff, 0xffffc857, 0xff3b3b98,
            0xffff7a59, 0xff202124, 0xff64d2ff, 0xff2d3436
        };
        return colors[Math.abs(index * 2 + offset) % colors.length];
    }
}

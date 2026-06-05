package com.joel.stream;

import android.content.Context;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

final class TmdbConfig {
    final String apiKey;
    final String readAccessToken;

    private TmdbConfig(String apiKey, String readAccessToken) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.readAccessToken = readAccessToken == null ? "" : readAccessToken.trim();
    }

    static TmdbConfig load(Context context) {
        try {
            InputStream in = context.getAssets().open("tmdb_config.json");
            String raw = readFully(in);
            JSONObject json = new JSONObject(raw);
            return new TmdbConfig(json.optString("api_key"), json.optString("read_access_token"));
        } catch (Exception ignored) {
            return new TmdbConfig("", "");
        }
    }

    boolean isConfigured() {
        return readAccessToken.length() > 0 || apiKey.length() > 0;
    }

    private static String readFully(InputStream in) throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), "UTF-8");
        } finally {
            in.close();
        }
    }
}


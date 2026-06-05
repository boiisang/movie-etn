package com.joel.stream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int BG = 0xff101112;
    private static final int SURFACE = 0xff181a1d;
    private static final int TEXT = 0xffffffff;
    private static final int MUTED = 0xffa9adb3;
    private static final int ACCENT = 0xffff5b45;

    private LinearLayout content;
    private EditText search;
    private SharedPreferences prefs;
    private String mode = "home";
    private MediaItem[] items = Catalog.ITEMS;
    private TmdbConfig tmdbConfig;
    private String catalogStatus = "Sample catalog ready";
    private final java.util.HashMap<String, TextView> navItems = new java.util.HashMap<String, TextView>();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(0xff101112);
        getWindow().setNavigationBarColor(0xff101112);
        prefs = getSharedPreferences("library", MODE_PRIVATE);
        tmdbConfig = TmdbConfig.load(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(20), dp(18), dp(18));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(bottomNav(), new LinearLayout.LayoutParams(-1, dp(78)));
        setContentView(root);

        render();
        loadTrendingCatalog();
    }

    private void render() {
        content.removeAllViews();
        updateNav();
        if ("settings".equals(mode)) {
            renderSettings();
            return;
        }
        renderHeader();
        if ("library".equals(mode)) {
            renderLibrary();
            return;
        }
        if ("search".equals(mode)) {
            renderSearch();
            search.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(search, 0);
            return;
        }
        renderHome();
    }

    private void renderHeader() {
        TextView title = text("Movie Etna", 34, TEXT, true);
        content.addView(title, lp(-1, -2, 0, 0, 0, 12));
        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Search movies and sample videos");
        search.setHintTextColor(0xff777c84);
        search.setTextColor(TEXT);
        search.setTextSize(15);
        search.setPadding(dp(16), 0, dp(16), 0);
        search.setBackground(round(0xff202327, dp(14)));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ("search".equals(mode)) drawSearchResults(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });
        content.addView(search, lp(-1, dp(52), 0, 0, 0, 20));
        TextView status = text(catalogStatus, 13, MUTED, false);
        content.addView(status, lp(-1, -2, 0, -8, 0, 18));
    }

    private void renderHome() {
        MediaItem[] current = currentItems();
        featured(current[0]);
        section("Latest Movies and Series", "", current);
        section("Latest Movies", "TMDB Movie", current);
        section("Latest Series", "TMDB Series", current);
        section("Playback Samples", "", Catalog.ITEMS);
        section("Short Samples", "Sample Short", Catalog.ITEMS);
    }

    private void renderLibrary() {
        TextView heading = text("Library", 30, TEXT, true);
        content.addView(heading, lp(-1, -2, 0, 4, 0, 8));
        TextView sub = text("Saved videos stay here for quick access. Streaming still uses the authorized public sample URLs.", 15, MUTED, false);
        content.addView(sub, lp(-1, -2, 0, 0, 0, 18));
        MediaItem[] saved = savedItems();
        if (saved.length == 0) {
            TextView empty = text("No saved videos yet. Open a video and tap Save.", 18, MUTED, false);
            empty.setGravity(Gravity.CENTER);
            empty.setBackground(round(SURFACE, dp(16)));
            content.addView(empty, lp(-1, dp(180), 0, 10, 0, 0));
        } else {
            section("Saved", "", saved);
        }
    }

    private void renderSearch() {
        TextView heading = text("Search", 30, TEXT, true);
        content.addView(heading, lp(-1, -2, 0, 4, 0, 12));
        drawSearchResults(search == null ? "" : search.getText().toString());
    }

    private void drawSearchResults(String query) {
        while (content.getChildCount() > 4) content.removeViewAt(4);
        String q = query == null ? "" : query.trim().toLowerCase();
        LinearLayout results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        int count = 0;
        for (MediaItem item : currentItems()) {
            if (q.length() == 0 || item.title.toLowerCase().contains(q) || item.category.toLowerCase().contains(q)) {
                results.addView(row(item), lp(-1, dp(106), 0, 0, 0, 12));
                count++;
            }
        }
        if (count == 0) {
            results.addView(text("No results found.", 18, MUTED, false), lp(-1, -2, 0, 10, 0, 0));
        }
        content.addView(results, lp(-1, -2, 0, 0, 0, 0));
    }

    private void renderSettings() {
        content.addView(text("Settings", 34, TEXT, true), lp(-1, -2, 0, 6, 0, 12));
        content.addView(settingsCard("About", "Movie Etna is a clean internal media app built by Joel Dongthansang for authorized sample and public-domain streams."));
        content.addView(settingsCard("Copyright", "Copyright (c) 2026 Joel Dongthansang. All rights reserved. Movie Etna and its app source are maintained by Joel Dongthansang."));
        content.addView(settingsCard("Live catalog", tmdbConfig.isConfigured() ? "Movie and series metadata loads from TMDB when internet is available. Playback remains limited to authorized sample streams in this internal build." : "No TMDB config is bundled. The app is using its offline sample catalog."));
        content.addView(settingsCard("Clean build", "Monetization screens, payment flows, analytics SDKs, and remote config dependencies are not included."));
        content.addView(settingsCard("Content rights", "The bundled catalog uses public sample/open movie streams. Add only content you own or are licensed to distribute."));
        content.addView(settingsCard("Privacy", "This build does not request accounts, location, camera, microphone, contacts, or marketing identifiers."));
    }

    private void featured(final MediaItem item) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackground(round(0xff1b1e22, dp(20)));
        PosterView poster = new PosterView(this, item);
        box.addView(poster, new LinearLayout.LayoutParams(-1, dp(210)));
        box.addView(text(item.title, 26, TEXT, true), lp(-1, -2, 0, 14, 0, 4));
        box.addView(text(item.description, 15, MUTED, false), lp(-1, -2, 0, 0, 0, 12));
        TextView play = button("Play now");
        play.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { openPlayer(item); }});
        box.addView(play, lp(-1, dp(48), 0, 0, 0, 0));
        content.addView(box, lp(-1, -2, 0, 0, 0, 24));
    }

    private void section(String title, String category, MediaItem[] source) {
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout rail = new LinearLayout(this);
        rail.setOrientation(LinearLayout.HORIZONTAL);
        int count = 0;
        for (MediaItem item : source) {
            if (category.length() == 0 || item.category.equals(category)) {
                rail.addView(card(item), lp(dp(174), dp(292), 0, 0, 14, 0));
                count++;
            }
        }
        if (count == 0) return;
        content.addView(text(title, 24, TEXT, true), lp(-1, -2, 0, 8, 0, 12));
        hsv.addView(rail, new ViewGroup.LayoutParams(-2, -2));
        content.addView(hsv, lp(-1, -2, 0, 0, 0, 22));
    }

    private View card(final MediaItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setClickable(true);
        card.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { openPlayer(item); }});
        card.addView(new PosterView(this, item), new LinearLayout.LayoutParams(-1, dp(230)));
        TextView title = text(item.title, 15, TEXT, true);
        title.setMaxLines(1);
        card.addView(title, lp(-1, -2, 0, 10, 0, 2));
        card.addView(text(item.year + "  " + item.duration, 12, MUTED, false), lp(-1, -2, 0, 0, 0, 0));
        return card;
    }

    private View row(final MediaItem item) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(10), dp(10), dp(10), dp(10));
        row.setBackground(round(SURFACE, dp(16)));
        PosterView poster = new PosterView(this, item);
        row.addView(poster, new LinearLayout.LayoutParams(dp(72), dp(86)));
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(14), 0, 0, 0);
        info.addView(text(item.title, 18, TEXT, true));
        info.addView(text(item.category + "  " + item.year + "  " + item.duration, 13, MUTED, false));
        row.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
        row.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { openPlayer(item); }});
        return row;
    }

    private LinearLayout bottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(0, dp(4), 0, dp(4));
        nav.setBackgroundColor(0xff151719);
        nav.addView(navItem("Home", "home"), new LinearLayout.LayoutParams(0, -1, 1));
        nav.addView(navItem("Library", "library"), new LinearLayout.LayoutParams(0, -1, 1));
        nav.addView(navItem("Search", "search"), new LinearLayout.LayoutParams(0, -1, 1));
        nav.addView(navItem("Settings", "settings"), new LinearLayout.LayoutParams(0, -1, 1));
        return nav;
    }

    private TextView navItem(final String label, final String nextMode) {
        TextView tv = text(label, 13, nextMode.equals(mode) ? ACCENT : MUTED, false);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                mode = nextMode;
                render();
            }
        });
        navItems.put(nextMode, tv);
        return tv;
    }

    private void updateNav() {
        for (String key : navItems.keySet()) {
            navItems.get(key).setTextColor(key.equals(mode) ? ACCENT : MUTED);
        }
    }

    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focus.getWindowToken(), 0);
            focus.clearFocus();
        }
    }

    private TextView settingsCard(String title, String body) {
        TextView tv = text(title + "\n" + body, 16, TEXT, false);
        tv.setLineSpacing(dp(3), 1.0f);
        tv.setPadding(dp(16), dp(16), dp(16), dp(16));
        tv.setBackground(round(SURFACE, dp(16)));
        return tv;
    }

    private void openPlayer(MediaItem item) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("title", item.title);
        intent.putExtra("category", item.category);
        intent.putExtra("year", item.year);
        intent.putExtra("duration", item.duration);
        intent.putExtra("description", item.description);
        intent.putExtra("videoUrl", item.videoUrl);
        intent.putExtra("colorA", item.colorA);
        intent.putExtra("colorB", item.colorB);
        startActivity(intent);
    }

    private MediaItem[] savedItems() {
        java.util.ArrayList<MediaItem> list = new java.util.ArrayList<MediaItem>();
        for (MediaItem item : currentItems()) {
            if (prefs.getBoolean("saved_" + item.title, false)) list.add(item);
        }
        return list.toArray(new MediaItem[0]);
    }

    private MediaItem[] currentItems() {
        return items == null || items.length == 0 ? Catalog.ITEMS : items;
    }

    private void loadTrendingCatalog() {
        if (!tmdbConfig.isConfigured()) {
            catalogStatus = "Offline sample catalog";
            render();
            return;
        }
        catalogStatus = "Loading live movie metadata...";
        render();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final MediaItem[] loaded = TmdbClient.trending(tmdbConfig);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (loaded.length > 0) {
                                items = loaded;
                                catalogStatus = "Live movie metadata loaded";
                            } else {
                                catalogStatus = "Sample catalog ready";
                            }
                            render();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            catalogStatus = "Sample catalog ready";
                            render();
                        }
                    });
                }
            }
        }).start();
    }

    static TextView text(Context c, String value, int sp, int color, boolean bold) {
        TextView tv = new TextView(c);
        tv.setText(value);
        tv.setTextColor(color);
        tv.setTextSize(sp);
        tv.setIncludeFontPadding(true);
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        return text(this, value, sp, color, bold);
    }

    private TextView button(String label) {
        TextView tv = text(label, 16, Color.WHITE, true);
        tv.setGravity(Gravity.CENTER);
        tv.setBackground(round(ACCENT, dp(14)));
        return tv;
    }

    private GradientDrawable round(int color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private LinearLayout.LayoutParams lp(int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(l, t, r, b);
        return p;
    }

    int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

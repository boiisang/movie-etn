package com.joel.stream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.graphics.drawable.GradientDrawable;

public class PlayerActivity extends Activity {
    private static final int BG = 0xff101112;
    private static final int TEXT = 0xffffffff;
    private static final int MUTED = 0xffa9adb3;
    private static final int ACCENT = 0xffff5b45;

    private MediaItem item;
    private VideoView video;
    private ProgressBar loading;
    private TextView status;
    private SharedPreferences prefs;
    private Handler handler;
    private String[] streamCandidates;
    private int streamIndex;
    private boolean streamReady;
    private final Runnable startupTimeout = new Runnable() {
        public void run() {
            if (!streamReady) {
                playNextCandidate("Stream timed out. Trying backup...");
            }
        }
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        handler = new Handler(Looper.getMainLooper());
        prefs = getSharedPreferences("library", MODE_PRIVATE);
        item = itemFromIntent();
        streamCandidates = SampleStreams.fallbacks(item.videoUrl);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));
        root.setBackgroundColor(BG);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = MainActivity.text(this, "Back", 15, ACCENT, true);
        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { finish(); }});
        top.addView(back, new LinearLayout.LayoutParams(dp(72), dp(44)));
        TextView title = MainActivity.text(this, item.title, 21, TEXT, true);
        title.setMaxLines(1);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(top, new LinearLayout.LayoutParams(-1, dp(52)));

        FrameLayout playerBox = new FrameLayout(this);
        playerBox.setBackgroundColor(Color.BLACK);
        video = new VideoView(this);
        playerBox.addView(video, new FrameLayout.LayoutParams(-1, -1));
        loading = new ProgressBar(this);
        FrameLayout.LayoutParams spinnerLp = new FrameLayout.LayoutParams(dp(54), dp(54), Gravity.CENTER);
        playerBox.addView(loading, spinnerLp);
        status = MainActivity.text(this, "Loading stream...", 15, 0xffd7d9dd, false);
        status.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams statusLp = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM);
        statusLp.setMargins(dp(12), 0, dp(12), dp(18));
        playerBox.addView(status, statusLp);
        root.addView(playerBox, new LinearLayout.LayoutParams(-1, dp(260)));

        TextView meta = MainActivity.text(this, item.category + "  " + item.year + "  " + item.duration, 14, MUTED, false);
        root.addView(meta, lp(-1, -2, 0, 18, 0, 8));
        TextView desc = MainActivity.text(this, item.description, 16, TEXT, false);
        desc.setLineSpacing(dp(3), 1.0f);
        root.addView(desc, lp(-1, -2, 0, 0, 0, 18));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        TextView save = actionButton(isSaved() ? "Saved" : "Save");
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean next = !isSaved();
                prefs.edit().putBoolean("saved_" + item.title, next).apply();
                ((TextView) v).setText(next ? "Saved" : "Save");
                Toast.makeText(PlayerActivity.this, next ? "Saved to Library" : "Removed from Library", Toast.LENGTH_SHORT).show();
            }
        });
        TextView replay = actionButton("Replay");
        replay.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { startVideo(); }});
        actions.addView(save, new LinearLayout.LayoutParams(0, dp(48), 1));
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(dp(12), 1);
        TextView spacer = new TextView(this);
        actions.addView(spacer, gap);
        actions.addView(replay, new LinearLayout.LayoutParams(0, dp(48), 1));
        root.addView(actions, lp(-1, -2, 0, 0, 0, 18));

        TextView note = MainActivity.text(this, "This player uses direct HTTPS sample streams. Add only media you own or are licensed to distribute.", 13, MUTED, false);
        note.setLineSpacing(dp(2), 1.0f);
        root.addView(note, lp(-1, -2, 0, 0, 0, 0));

        setContentView(root);
        startVideo();
    }

    private void startVideo() {
        streamIndex = 0;
        playCurrentCandidate("Loading stream...");
    }

    private MediaItem itemFromIntent() {
        String title = getIntent().getStringExtra("title");
        String videoUrl = getIntent().getStringExtra("videoUrl");
        if (videoUrl == null || videoUrl.trim().length() == 0) {
            return Catalog.findByTitle(title);
        }
        return new MediaItem(
            value("title", "Untitled"),
            value("category", "Media"),
            value("year", "TMDB"),
            value("duration", "sample play"),
            value("description", "Metadata loaded for this title. Playback uses authorized sample streams in this internal build."),
            videoUrl,
            getIntent().getIntExtra("colorA", 0xfff25f4c),
            getIntent().getIntExtra("colorB", 0xff5cc8ff)
        );
    }

    private String value(String key, String fallback) {
        String value = getIntent().getStringExtra(key);
        return value == null || value.trim().length() == 0 ? fallback : value;
    }

    private void playCurrentCandidate(String message) {
        streamReady = false;
        handler.removeCallbacks(startupTimeout);
        loading.setVisibility(View.VISIBLE);
        status.setText(message);
        video.stopPlayback();
        MediaController controller = new MediaController(this);
        controller.setAnchorView(video);
        video.setMediaController(controller);
        video.setVideoURI(Uri.parse(streamCandidates[streamIndex]));
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                streamReady = true;
                handler.removeCallbacks(startupTimeout);
                loading.setVisibility(View.GONE);
                status.setText("Playing");
                mp.setLooping(false);
                video.start();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                status.setText("Playback finished");
            }
        });
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                playNextCandidate("Playback error. Trying backup...");
                return true;
            }
        });
        video.requestFocus();
        video.start();
        handler.postDelayed(startupTimeout, 12000);
    }

    private void playNextCandidate(String message) {
        handler.removeCallbacks(startupTimeout);
        if (streamIndex + 1 < streamCandidates.length) {
            streamIndex++;
            playCurrentCandidate(message);
            return;
        }
        loading.setVisibility(View.GONE);
        status.setText("Playback error. Check network or stream availability.");
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(startupTimeout);
        super.onDestroy();
    }

    private boolean isSaved() {
        return prefs.getBoolean("saved_" + item.title, false);
    }

    private TextView actionButton(String label) {
        TextView tv = MainActivity.text(this, label, 15, Color.WHITE, true);
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

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

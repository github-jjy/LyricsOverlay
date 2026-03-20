package com.example.lyricoverlay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends Activity {
    public static final String EXTRA_LYRICS = "extra_lyrics";

    // Minimal demo text (no networking, no ads).
    private static final String DEFAULT_LYRICS =
            "LyricsOverlay\nDrag to move\n(GalaxyOS demo)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ensureOverlayOrRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.canDrawOverlays(this)) {
            startOverlay();
            finish();
        }
    }

    private void ensureOverlayOrRequest() {
        if (Settings.canDrawOverlays(this)) {
            startOverlay();
            finish();
            return;
        }
        openOverlayPermissionSettings();
    }

    private void openOverlayPermissionSettings() {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startOverlay() {
        Intent serviceIntent = new Intent(this, OverlayService.class);
        serviceIntent.putExtra(EXTRA_LYRICS, DEFAULT_LYRICS);
        startService(serviceIntent);
    }
}


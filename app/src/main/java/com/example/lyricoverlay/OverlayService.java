package com.example.lyricoverlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private TextView overlayView;
    private WindowManager.LayoutParams layoutParams;
    private boolean isAdded;

    private int initialX;
    private int initialY;
    private float touchStartX;
    private float touchStartY;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        overlayView = new TextView(this);
        overlayView.setTextSize(14f);
        overlayView.setTextColor(Color.WHITE);
        overlayView.setBackgroundColor(0x66000000);
        overlayView.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        overlayView.setGravity(Gravity.CENTER);

        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (layoutParams == null) return false;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        touchStartX = event.getRawX();
                        touchStartY = event.getRawY();
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - touchStartX);
                        int dy = (int) (event.getRawY() - touchStartY);
                        layoutParams.x = initialX + dx;
                        layoutParams.y = initialY + dy;
                        windowManager.updateViewLayout(overlayView, layoutParams);
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (intent != null) {
            String lyrics = intent.getStringExtra(MainActivity.EXTRA_LYRICS);
            if (lyrics != null) overlayView.setText(lyrics);
        }

        if (layoutParams == null) {
            layoutParams = createLayoutParams();
        }

        addOverlayIfNeeded();
        return START_NOT_STICKY;
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = dpToPx(120);
        return params;
    }

    private void addOverlayIfNeeded() {
        if (isAdded) return;

        try {
            windowManager.addView(overlayView, layoutParams);
            isAdded = true;
        } catch (Exception ignored) {
            // Typically: missing permission or invalid window state.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isAdded && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
            } catch (Exception ignored) {
            }
        }
        isAdded = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}


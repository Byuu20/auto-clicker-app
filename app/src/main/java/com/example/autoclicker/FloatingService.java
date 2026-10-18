package com.example.autoclicker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private LinearLayout controlPanel;
    private Button btnStart, btnTarget;
    
    private boolean isRunning = false;
    private int currentClickCount = 0;
    private Handler clickHandler = new Handler();

    private Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                AutoClickService service = AutoClickService.getInstance();
                if (service != null && btnTarget != null) {
                    int[] location = new int[2];
                    btnTarget.getLocationOnScreen(location);
                    // Point sentuhan di tengah target
                    service.doClick(location[0] + (btnTarget.getWidth() / 2), location[1] + (btnTarget.getHeight() / 2));
                }

                currentClickCount++;

                // Cek batas aman klik
                if (MainActivity.maxClicks > 0 && currentClickCount >= MainActivity.maxClicks) {
                    stopAutoClick();
                } else {
                    // Loop aman pake interval minimal 50ms
                    clickHandler.postDelayed(this, Math.max(MainActivity.clickInterval, 50));
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Panel Kontrol Melayang (Tombol Start/Stop & Target)
        controlPanel = new LinearLayout(this);
        controlPanel.setOrientation(LinearLayout.VERTICAL);

        btnStart = new Button(this);
        btnStart.setText("▶");
        btnStart.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnStart.setTextColor(Color.WHITE);

        btnTarget = new Button(this);
        btnTarget.setText("🎯");
        btnTarget.setBackgroundColor(Color.parseColor("#E91E63"));
        btnTarget.setTextColor(Color.WHITE);

        controlPanel.addView(btnStart);
        controlPanel.addView(btnTarget);

        int layoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 300;

        btnStart.setOnClickListener(v -> {
            if (isRunning) {
                stopAutoClick();
            } else {
                startAutoClick();
            }
        });

        // Fitur geser panel
        controlPanel.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(controlPanel, params);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(controlPanel, params);
    }

    private void startAutoClick() {
        isRunning = true;
        currentClickCount = 0;
        btnStart.setText("⏸");
        btnStart.setBackgroundColor(Color.RED);
        clickHandler.post(clickRunnable);
    }

    private void stopAutoClick() {
        isRunning = false;
        btnStart.setText("▶");
        btnStart.setBackgroundColor(Color.parseColor("#4CAF50"));
        clickHandler.removeCallbacks(clickRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoClick();
        if (controlPanel != null) windowManager.removeView(controlPanel);
    }
}

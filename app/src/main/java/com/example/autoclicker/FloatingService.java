package com.example.autoclicker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private Button floatingButton;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingButton = new Button(this);
        floatingButton.setText("CLICK!");
        floatingButton.setBackgroundColor(Color.RED);
        floatingButton.setTextColor(Color.WHITE);

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 200;
        params.y = 400;

        floatingButton.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(floatingButton, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        int diffX = Math.abs((int) (event.getRawX() - initialTouchX));
                        int diffY = Math.abs((int) (event.getRawY() - initialTouchY));
                        if (diffX < 10 && diffY < 10) {
                            AutoClickService service = AutoClickService.getInstance();
                            if (service != null) {
                                service.doClick(params.x + 50, params.y + 100);
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(floatingButton, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButton != null) windowManager.removeView(floatingButton);
    }
}

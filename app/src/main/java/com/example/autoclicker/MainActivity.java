package com.example.autoclicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. Izin Tombol Melayang");
        btnOverlay.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Izin Overlay Aktif!", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnAccessibility = new Button(this);
        btnAccessibility.setText("2. Izin Aksesibilitas");
        btnAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        Button btnStart = new Button(this);
        btnStart.setText("3. Munculkan Tombol Melayang");
        btnStart.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Aktifkan Izin Overlay Dulu!", Toast.LENGTH_SHORT).show();
            } else {
                startService(new Intent(this, FloatingService.class));
            }
        });

        layout.addView(btnOverlay);
        layout.addView(btnAccessibility);
        layout.addView(btnStart);

        setContentView(layout);
    }
                  }

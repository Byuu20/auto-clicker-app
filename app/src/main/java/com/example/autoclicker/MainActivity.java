package com.example.autoclicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static int clickInterval = 100; // Default 100ms
    public static int maxClicks = 0;       // 0 = Tanpa batas (pake stop manual)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#121212")); // Tema Gelap

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);

        // Header Title
        TextView title = new TextView(this);
        title.setText("Auto Clicker * (Free)");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setPadding(0, 0, 0, 40);
        root.addView(title);

        // CARD 1: MODE SATU TARGET
        root.addView(createSectionCard("MODE SATU TARGET", "Target tunggal untuk klik berulang pada satu titik.", v -> startAutoClicker()));

        // CARD 2: PENGATURAN BATAS AMAN HP
        LinearLayout safetyCard = createCardLayout();
        
        TextView safetyTitle = new TextView(this);
        safetyTitle.setText("BATAS AMAN HP (BATAS KINERJA)");
        safetyTitle.setTextColor(Color.parseColor("#FF4081"));
        safetyTitle.setTextSize(16);
        safetyTitle.setPadding(0, 0, 0, 20);
        safetyCard.addView(safetyTitle);

        TextView labelDelay = new TextView(this);
        labelDelay.setText("Interval Klik (milidetik, Minimal 50ms):");
        labelDelay.setTextColor(Color.LTGRAY);
        safetyCard.addView(labelDelay);

        EditText inputDelay = new EditText(this);
        inputDelay.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputDelay.setText("100");
        inputDelay.setTextColor(Color.WHITE);
        safetyCard.addView(inputDelay);

        TextView labelLimit = new TextView(this);
        labelLimit.setText("Batas Maksimal Klik (0 = Tanpa Batas):");
        labelLimit.setTextColor(Color.LTGRAY);
        safetyCard.addView(labelLimit);

        EditText inputLimit = new EditText(this);
        inputLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputLimit.setText("500");
        inputLimit.setTextColor(Color.WHITE);
        safetyCard.addView(inputLimit);

        Button btnSave = new Button(this);
        btnSave.setText("SIMPAN PENGATURAN");
        btnSave.setBackgroundColor(Color.parseColor("#333333"));
        btnSave.setTextColor(Color.WHITE);
        btnSave.setOnClickListener(v -> {
            try {
                int valDelay = Integer.parseInt(inputDelay.getText().toString());
                int valLimit = Integer.parseInt(inputLimit.getText().toString());
                
                // Kunci batas aman minimal 50ms agar HP tidak ngehang
                clickInterval = Math.max(valDelay, 50);
                maxClicks = Math.max(valLimit, 0);

                Toast.makeText(this, "Pengaturan Disimpan! Interval: " + clickInterval + "ms", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Input tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });
        safetyCard.addView(btnSave);

        root.addView(safetyCard);

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private LinearLayout createSectionCard(String titleText, String descText, android.view.View.OnClickListener onActivate) {
        LinearLayout card = createCardLayout();

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        card.addView(title);

        TextView desc = new TextView(this);
        desc.setText(descText);
        desc.setTextColor(Color.GRAY);
        desc.setPadding(0, 10, 0, 20);
        card.addView(desc);

        Button btnAktifkan = new Button(this);
        btnAktifkan.setText("AKTIFKAN");
        btnAktifkan.setBackgroundColor(Color.parseColor("#E91E63"));
        btnAktifkan.setTextColor(Color.WHITE);
        btnAktifkan.setOnClickListener(onActivate);
        card.addView(btnAktifkan);

        return card;
    }

    private LinearLayout createCardLayout() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.parseColor("#2A2A2A"));
        card.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30);
        card.setLayoutParams(params);
        return card;
    }

    private void startAutoClicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else if (AutoClickService.getInstance() == null) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Aktifkan Izin Aksesibilitas Auto Clicker!", Toast.LENGTH_LONG).show();
        } else {
            startService(new Intent(this, FloatingService.class));
            Toast.makeText(this, "Tombol Melayang Aktif!", Toast.LENGTH_SHORT).show();
        }
    }
    }

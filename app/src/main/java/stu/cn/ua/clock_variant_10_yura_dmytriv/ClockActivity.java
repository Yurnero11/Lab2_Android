package stu.cn.ua.clock_variant_10_yura_dmytriv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ClockActivity extends AppCompatActivity {
    private TextView textClock;
    private TextView textDate;
    private TextView textTimeZone;
    private Button buttonTimer;
    private Button buttonSettings;

    private Handler timeHandler = new Handler();
    private static final int TIME_UPDATE_INTERVAL = 1000; // Оновлення кожну секунду

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        // Знайдіть компоненти в макеті за їх ID
        textClock = findViewById(R.id.textClock);
        textDate = findViewById(R.id.textDate);
        textTimeZone = findViewById(R.id.textTimeZone);
        buttonTimer = findViewById(R.id.buttonTimer);
        buttonSettings = findViewById(R.id.buttonSettings);

        // Встановіть обробники подій для кнопок
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Перехід на екран таймера
                Intent intent = new Intent(ClockActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Перехід на екран налаштувань
                Intent intent = new Intent(ClockActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Отримайте поточний час і дату і відображайте їх
        updateTimeAndDate();

        // Запустіть таймер для автоматичного оновлення часу
        startTimer();
    }

    private void startTimer() {
        // Створити Runnable, який буде оновлювати час
        final Runnable updateTimer = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDate();
                timeHandler.postDelayed(this, TIME_UPDATE_INTERVAL);
            }
        };

        // Перший запуск оновлення часу
        timeHandler.post(updateTimer);
    }

    private void updateTimeAndDate() {
        // Отримайте поточний час, дату і часовий пояс і встановіть їх у відповідні тексти
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        SharedPreferences settings = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String selectedTimeZone = settings.getString("timeZone", "GMT");
        TimeZone timeZone = TimeZone.getTimeZone(selectedTimeZone);

        timeFormat.setTimeZone(timeZone);

        textClock.setText(timeFormat.format(new Date()));
        textDate.setText(dateFormat.format(new Date()));
        textTimeZone.setText("Time Zone: " + selectedTimeZone);
    }
}

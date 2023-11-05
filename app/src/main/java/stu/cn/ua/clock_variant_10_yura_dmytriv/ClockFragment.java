package stu.cn.ua.clock_variant_10_yura_dmytriv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ClockActivity extends Fragment {
    private TextView textClock;
    private TextView textDate;
    private TextView textTimeZone;
    private Button buttonTimer;
    private Button buttonSettings;

    private Handler timeHandler = new Handler();
    private static final int TIME_UPDATE_INTERVAL = 1000; // Обновление каждую секунду

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clock_fragment, container, false);

        // Найти компоненты в макете по их ID
        textClock = view.findViewById(R.id.textClock);
        textDate = view.findViewById(R.id.textDate);
        textTimeZone = view.findViewById(R.id.textTimeZone);
        buttonTimer = view.findViewById(R.id.buttonTimer);
        buttonSettings = view.findViewById(R.id.buttonSettings);

        // Установить обработчики событий для кнопок
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на экран таймера
                // Здесь вам нужно будет использовать активность (Activity) для навигации
                Intent intent = new Intent(getActivity(), TimerActivity.class);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на экран настроек (SettingsActivity)
                Intent intent = new Intent(getActivity(), SettingsFragment.class);
                startActivity(intent);
            }
        });

        // Получите текущее время и дату и отобразите их
        updateTimeAndDate();
        // Запустите таймер для автоматического обновления времени
        startTimer();

        return view;
    }

    private void startTimer() {
        // Создать Runnable, который будет обновлять время
        final Runnable updateTimer = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDate();
                timeHandler.postDelayed(this, TIME_UPDATE_INTERVAL);
            }
        };

        // Первый запуск обновления времени
        timeHandler.post(updateTimer);
    }

    private void updateTimeAndDate() {
        // Получите текущее время, дату и часовой пояс и установите их в соответствующие текстовые поля
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        SharedPreferences settings = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String selectedTimeZone = settings.getString("timeZone", "GMT");
        TimeZone timeZone = TimeZone.getTimeZone(selectedTimeZone);

        timeFormat.setTimeZone(timeZone);

        textClock.setText(timeFormat.format(new Date()));
        textDate.setText(dateFormat.format(new Date()));
        textTimeZone.setText("Time Zone: " + selectedTimeZone);
    }
}

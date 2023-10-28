package stu.cn.ua.clock_variant_10_yura_dmytriv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TIME_ZONE_KEY = "timeZone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner timeZoneSpinner = findViewById(R.id.timeZoneSpinner);

        // Ось приклад створення списку часових поясів у Spinner.
        String[] timeZones = {"Europe/Kiev", "America/New_York", "Europe/London", "Europe/Warsaw", "Asia/Shanghai"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeZones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeZoneSpinner.setAdapter(adapter);

        // Отримайте поточний вибраний часовий пояс і встановіть його у Spinner
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String selectedTimeZone = settings.getString(TIME_ZONE_KEY, "Europe/Kiev");
        int position = adapter.getPosition(selectedTimeZone);
        timeZoneSpinner.setSelection(position);

        Button backButton = findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Збережіть вибраний часовий пояс у налаштуваннях
        Spinner timeZoneSpinner = findViewById(R.id.timeZoneSpinner);
        String selectedTimeZone = (String) timeZoneSpinner.getSelectedItem();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TIME_ZONE_KEY, selectedTimeZone);
        editor.apply();
    }
}

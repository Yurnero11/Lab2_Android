package stu.cn.ua.clock_variant_10_yura_dmytriv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TIME_ZONE_KEY = "timeZone";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        Spinner timeZoneSpinner = view.findViewById(R.id.timeZoneSpinner);

        // Пример создания списка часовых поясов в Spinner.
        String[] timeZones = {"Europe/Kyiv", "America/New_York", "Europe/London", "Europe/Warsaw", "Asia/Shanghai"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, timeZones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeZoneSpinner.setAdapter(adapter);

        // Получите текущий выбранный часовой пояс и установите его в Spinner
        SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedTimeZone = settings.getString(TIME_ZONE_KEY, "Europe/Kiev");
        int position = adapter.getPosition(selectedTimeZone);
        timeZoneSpinner.setSelection(position);

        Button backButton = view.findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Сохраните выбранный часовой пояс в настройках
        Spinner timeZoneSpinner = requireView().findViewById(R.id.timeZoneSpinner);
        String selectedTimeZone = (String) timeZoneSpinner.getSelectedItem();
        SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TIME_ZONE_KEY, selectedTimeZone);
        editor.apply();
    }
}

package stu.cn.ua.clock_variant_10_yura_dmytriv;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_fragment, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Замените фрагмент на фрагмент ClockFragment после задержки
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ClockFragment())
                        .commit();
            }
        }, 2000);

        return view;
    }
}
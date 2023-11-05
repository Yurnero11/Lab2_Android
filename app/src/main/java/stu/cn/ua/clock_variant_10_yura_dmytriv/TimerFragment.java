    package stu.cn.ua.clock_variant_10_yura_dmytriv;

    import android.app.Activity;
    import android.content.ComponentName;
    import android.content.Intent;
    import android.content.ServiceConnection;
    import android.os.Bundle;
    import android.os.IBinder;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ScrollView;
    import android.widget.TextView;

    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentTransaction;

    import java.util.ArrayList;

    import stu.cn.ua.clock_variant_10_yura_dmytriv.Service.ITimerObserver;
    import stu.cn.ua.clock_variant_10_yura_dmytriv.Service.TimerService;

    public class TimerFragment extends Fragment implements ITimerObserver {
        private TimerService timerService;
        private boolean isServiceBound = false;

        private TextView timerDisplay;
        private Button startButton;
        private Button stopButton;
        private Button pauseButton;
        private Button lapButton;
        private Button resetButton;
        private long pausedTime;

        private boolean isRunning = false;
        private int lapNumber = 1;
        private ArrayList<Long> lapTimes = new ArrayList<>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.timer_fragment, container, false);

            timerDisplay = view.findViewById(R.id.timerDisplay);
            startButton = view.findViewById(R.id.startButton);
            stopButton = view.findViewById(R.id.stopButton);
            pauseButton = view.findViewById(R.id.pauseButton);
            lapButton = view.findViewById(R.id.lapButton);
            resetButton = view.findViewById(R.id.resetButton);

            // Установите начальное состояние кнопок
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(false);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer();
                }
            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                }
            });

            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseOrResumeTimer();
                }
            });

            lapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordLapTime();
                }
            });

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTimer();
                }
            });

            Button backButton = view.findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new ClockFragment());
                    transaction.addToBackStack(null);  // Добавьте эту строку, чтобы фрагмент можно было вернуть кнопкой "назад"
                    transaction.commit();
                }
            });

            return view;
        }

        private void startTimer() {
            if (isServiceBound) {
                timerService.startTimer();
            }
        }

        private void stopTimer() {
            if (isServiceBound) {
                timerService.stopTimer();
            }
        }

        private void pauseOrResumeTimer() {
            if (isServiceBound) {
                timerService.pauseOrResumeTimer();
            }
        }

        private void recordLapTime() {
            if (isServiceBound) {
                timerService.recordLapTime();
            }
        }

        private void resetTimer() {
            if (isServiceBound) {
                timerService.resetTimer();
            }
        }

        @Override
        public void onTimerTick(long elapsedTime) {
            timerDisplay.setText(formatTime(elapsedTime));
        }

        @Override
        public void onLapTime(int lapNumber, long lapTime) {
            lapTimes.add(lapTime);

            String lapTimeStr = formatTime(lapTime);
            TextView lapList = requireView().findViewById(R.id.lapList);
            lapList.append("Коло " + lapNumber + ": " + lapTimeStr + "\n");
            lapNumber++;

            final ScrollView lapListScrollView = requireView().findViewById(R.id.lapListScrollView);
            lapListScrollView.post(new Runnable() {
                @Override
                public void run() {
                    lapListScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            bindTimerService();
        }

        @Override
        public void onStop() {
            super.onStop();
            unbindTimerService();
        }

        private void bindTimerService() {
            Intent intent = new Intent(requireActivity(), TimerService.class);
            requireActivity().bindService(intent, serviceConnection, Activity.BIND_AUTO_CREATE);
        }

        private void unbindTimerService() {
            if (isServiceBound) {
                requireActivity().unbindService(serviceConnection);
                isServiceBound = false;
                timerService = null; // Установите timerService в null после отвязки службы
            }
        }

        private ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
                timerService = binder.getService();
                timerService.registerObserver(TimerFragment.this);
                isServiceBound = true;

                // После успешной привязки, установите кнопки в службе
                timerService.setButtons(startButton, stopButton, pauseButton, lapButton, resetButton);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                timerService.unregisterObserver(TimerFragment.this);
                isServiceBound = false;
                timerService = null; // Установите timerService в null при отвязке службы
            }
        };

        private String formatTime(long elapsedTime) {
            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) (elapsedTime % 60000 / 1000);
            int milliseconds = (int) (elapsedTime % 1000 / 10);
            return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
        }
    }

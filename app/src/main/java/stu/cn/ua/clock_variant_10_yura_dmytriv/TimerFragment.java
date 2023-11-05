    package stu.cn.ua.clock_variant_10_yura_dmytriv;

    import android.app.Activity;
    import android.content.ComponentName;
    import android.content.Intent;
    import android.content.ServiceConnection;
    import android.os.Bundle;
    import android.os.IBinder;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ScrollView;
    import android.widget.TextView;

    import java.util.ArrayList;

    import stu.cn.ua.clock_variant_10_yura_dmytriv.Service.ITimerObserver;
    import stu.cn.ua.clock_variant_10_yura_dmytriv.Service.TimerService;

    public class TimerActivity extends Activity implements ITimerObserver {
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
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.timer_fragment);

            timerDisplay = findViewById(R.id.timerDisplay);
            startButton = findViewById(R.id.startButton);
            stopButton = findViewById(R.id.stopButton);
            pauseButton = findViewById(R.id.pauseButton);
            lapButton = findViewById(R.id.lapButton);
            resetButton = findViewById(R.id.resetButton);

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


            Button backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
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
            TextView lapList = findViewById(R.id.lapList);
            lapList.append("Коло " + lapNumber + ": " + lapTimeStr + "\n");
            lapNumber++;

            final ScrollView lapListScrollView = findViewById(R.id.lapListScrollView);
            lapListScrollView.post(new Runnable() {
                @Override
                public void run() {
                    lapListScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        @Override
        protected void onStart() {
            super.onStart();
            bindTimerService();
        }

        @Override
        protected void onStop() {
            super.onStop();
            unbindTimerService();
        }

        private void bindTimerService() {
            Intent intent = new Intent(this, TimerService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        private void unbindTimerService() {
            if (isServiceBound) {
                unbindService(serviceConnection);
                isServiceBound = false;
                timerService = null; // Установите timerService в null после отвязки службы
            }
        }

        private ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
                timerService = binder.getService();
                timerService.registerObserver(TimerActivity.this);
                isServiceBound = true;

                // После успешной привязки, установите кнопки в службе
                timerService.setButtons(startButton, stopButton, pauseButton, lapButton, resetButton);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                timerService.unregisterObserver(TimerActivity.this);
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

    package stu.cn.ua.clock_variant_10_yura_dmytriv;

    import android.app.Activity;
    import android.os.Bundle;
    import android.os.Handler;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ScrollView;
    import android.widget.TextView;

    import java.util.ArrayList;

    public class TimerActivity extends Activity {
        private TextView timerDisplay;
        private Button startButton;
        private Button stopButton;
        private Button pauseButton;
        private Button lapButton;
        private Button resetButton;
        private boolean isRunning = false;
        private long startTime;
        private long pausedTime;
        private Handler handler = new Handler();
        private int lapNumber = 1;
        private ArrayList<Long> lapTimes = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_timer);

            timerDisplay = findViewById(R.id.timerDisplay);
            startButton = findViewById(R.id.startButton);
            stopButton = findViewById(R.id.stopButton);
            pauseButton = findViewById(R.id.pauseButton);
            lapButton = findViewById(R.id.lapButton);
            resetButton = findViewById(R.id.resetButton);

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

        private Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                if (isRunning) {
                    timerDisplay.setText(formatTime(elapsedTime));
                }
                handler.postDelayed(this, 10);
            }
        };

        private void startTimer() {
            if (!isRunning) {
                isRunning = true;
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                pauseButton.setEnabled(true);
                lapButton.setEnabled(true);
                resetButton.setEnabled(true);
                startTime = System.currentTimeMillis() - pausedTime;
                handler.post(timerRunnable);
            }
        }

        private void stopTimer() {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(true);
            pausedTime = 0;
        }

        private void pauseOrResumeTimer() {
            if (isRunning) {
                isRunning = false;
                pausedTime = System.currentTimeMillis() - startTime;
                pauseButton.setText("Продовжити");
            } else {
                isRunning = true;
                startTime = System.currentTimeMillis() - pausedTime;
                pauseButton.setText("Пауза");
            }
        }

        private long previousLapTime = 0; // Добавьте это поле в класс
        private void recordLapTime() {
            if (isRunning) {
                long currentTime = System.currentTimeMillis();
                long lapTime = currentTime - startTime - previousLapTime;

                if (lapTimes.isEmpty()) {
                    // Добавляем первое кругло со значением текущего времени
                    lapTimes.add(currentTime - startTime);
                    previousLapTime = 0; // Сбрасываем предыдущее время
                }

                lapTimes.add(lapTime);
                previousLapTime += lapTime;

                String lapTimeStr = formatTime(lapTime);
                TextView lapList = findViewById(R.id.lapList);
                lapList.append("Коло " + lapNumber + ": " + lapTimeStr + "\n");
                lapNumber++;

                // Прокручиваем ScrollView вниз, чтобы отобразить новый круг
                final ScrollView lapListScrollView = findViewById(R.id.lapListScrollView);
                lapListScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        lapListScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        }

        private void resetTimer() {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(false);
            pausedTime = 0;
            timerDisplay.setText("00:00.00");
            TextView lapList = findViewById(R.id.lapList);
            lapList.setText("");
            lapNumber = 1;
            lapTimes.clear();
        }

        private String formatTime(long elapsedTime) {
            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) (elapsedTime % 60000 / 1000);
            int milliseconds = (int) (elapsedTime % 1000 / 10);
            return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
        }
    }

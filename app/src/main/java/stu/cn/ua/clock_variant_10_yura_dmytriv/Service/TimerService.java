package stu.cn.ua.clock_variant_10_yura_dmytriv.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimerService extends Service {
    private final IBinder binder = new TimerBinder();
    private Handler handler = new Handler();
    private ArrayList<ITimerObserver> observers = new ArrayList<>();

    private boolean isRunning = false;
    private long startTime;
    private long pausedTime;
    private long previousLapTime = 0;
    private int lapNumber = 1;
    private ArrayList<Long> lapTimes = new ArrayList<>();

    private Button startButton;
    private Button stopButton;
    private Button pauseButton;
    private Button lapButton;
    private Button resetButton;

    public TimerService() {
    }

    public void setButtons(Button start, Button stop, Button pause, Button lap, Button reset) {
        startButton = start;
        stopButton = stop;
        pauseButton = pause;
        lapButton = lap;
        resetButton = reset;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    public void registerObserver(ITimerObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(ITimerObserver observer) {
        observers.remove(observer);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            if (isRunning) {
                notifyObservers(elapsedTime);
            }
            handler.postDelayed(this, 10);
        }
    };

    public void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.currentTimeMillis() - pausedTime;
            handler.post(timerRunnable);
            notifyObservers(startTime);
            enableButtons();
        }
    }

    public void stopTimer() {
        isRunning = false;
        pausedTime = 0;
        handler.removeCallbacks(timerRunnable);
        disableButtons1();
    }

    public void pauseOrResumeTimer() {
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

    public void recordLapTime() {
        if (isRunning) {
            long currentTime = System.currentTimeMillis();
            long lapTime = currentTime - startTime - previousLapTime;

            if (lapTimes.isEmpty()) {
                lapTimes.add(currentTime - startTime);
                previousLapTime = 0;
            }

            lapTimes.add(lapTime);
            previousLapTime += lapTime;
            notifyLapTime(lapTime);
        }
    }

    public void resetTimer() {
        isRunning = false;
        pausedTime = 0;
        handler.removeCallbacks(timerRunnable);
        lapTimes.clear();
        lapNumber = 1;
        notifyObservers(0);
        disableButtons1();
    }

    private void notifyObservers(long elapsedTime) {
        for (ITimerObserver observer : observers) {
            observer.onTimerTick(elapsedTime);
        }
    }

    private void notifyLapTime(long lapTime) {
        for (ITimerObserver observer : observers) {
            observer.onLapTime(lapNumber, lapTime);
        }
    }

    public void enableButtons() {
        if (startButton != null && stopButton != null && pauseButton != null && lapButton != null && resetButton != null) {
            startButton.setEnabled(true);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(true);
            lapButton.setEnabled(true);
            resetButton.setEnabled(true);
        }
    }

    public void disableButtons() {
        if (startButton != null && stopButton != null && pauseButton != null && lapButton != null && resetButton != null) {
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(false);
        }
    }

    public void disableButtons1() {
        if (startButton != null && stopButton != null && pauseButton != null && lapButton != null && resetButton != null) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(false);
        }
    }

}

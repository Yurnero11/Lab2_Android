package stu.cn.ua.clock_variant_10_yura_dmytriv.Service;

public interface ITimerObserver {
    // Цей метод викликається при оновленні таймера
    void onTimerTick(long elapsedTime);

    // Цей метод викликається при записі кола
    void onLapTime(int lapNumber, long lapTime);
}

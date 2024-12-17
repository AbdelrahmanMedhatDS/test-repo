package mazegame.logic;

public class Timer {
    private long startTime;
    private int totalSeconds;
    private boolean isPaused = false;
    private long pauseStartTime = 0;
    private long totalPausedTime = 0;

    public Timer(int seconds) {
        this.totalSeconds = seconds;
        this.startTime = System.currentTimeMillis();
    }

    public int getTimeLeft() {
        if (isPaused) {
            return calculateRemainingTime();
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime - totalPausedTime;
        int remainingTime = totalSeconds - (int)(elapsedTime / 1000);

        return Math.max(remainingTime, 0);
    }

    private int calculateRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime - totalPausedTime - (currentTime - pauseStartTime);
        int remainingTime = totalSeconds - (int)(elapsedTime / 1000);

        return Math.max(remainingTime, 0);
    }

    public boolean hasExpired() {
        return getTimeLeft() <= 0;
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            pauseStartTime = System.currentTimeMillis();
        }
    }

    public void resume() {
        if (isPaused) {
            totalPausedTime += System.currentTimeMillis() - pauseStartTime;
            isPaused = false;
            pauseStartTime = 0;
        }
    }

    public int reduceTime(int seconds) {
        // Calculate how many seconds can actually be reduced
        int currentTimeLeft = getTimeLeft();
        int actualReduction = Math.min(seconds, currentTimeLeft);

        // Adjust the total seconds to effectively reduce the time
        totalSeconds -= actualReduction;

        return actualReduction;
    }


    public int addTime(int seconds) {
        // Add the specified number of seconds to total seconds
        totalSeconds += seconds;
        return seconds;
    }


    public int getTotalTime() {
        return totalSeconds + (int)(totalPausedTime / 1000);
    }
}
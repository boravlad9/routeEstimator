package code.roadEstimator.entities;

public class RouteCaracteristics {
    private int nightCount = 0;
    private int weekDayCount = 0;
    private int[] hourCount = new int[24];
    private int[] dayCount = new int[7];
    private int speed = 0;

    public RouteCaracteristics() {
    }

    public int getNightCount() {
        return nightCount;
    }

    public void setNightCount(int nightCount) {
        this.nightCount = nightCount;
    }

    public int getWeekDayCount() {
        return weekDayCount;
    }

    public void setWeekDayCount(int weekDayCount) {
        this.weekDayCount = weekDayCount;
    }

    public int[] getHourCount() {
        return hourCount;
    }

    public void setHourCount(int[] hourCount) {
        this.hourCount = hourCount;
    }

    public int[] getDayCount() {
        return dayCount;
    }

    public void setDayCount(int[] dayCount) {
        this.dayCount = dayCount;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}

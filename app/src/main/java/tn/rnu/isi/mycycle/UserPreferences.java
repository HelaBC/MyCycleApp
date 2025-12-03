package tn.rnu.isi.mycycle;

public class UserPreferences {
    private long userId;
    private int cycleLength; // Average cycle length in days
    private int periodLength; // Average period length in days
    private String lastPeriodStart; // Last period start date
    private String trackedSymptoms; // Comma-separated or JSON string of symptoms to track
    private boolean notificationsEnabled;
    private String theme; // Light, Dark
    private String language; // English, etc.

    public UserPreferences(long userId, int cycleLength, int periodLength, String lastPeriodStart,
                          String trackedSymptoms, boolean notificationsEnabled, String theme, String language) {
        this.userId = userId;
        this.cycleLength = cycleLength;
        this.periodLength = periodLength;
        this.lastPeriodStart = lastPeriodStart;
        this.trackedSymptoms = trackedSymptoms;
        this.notificationsEnabled = notificationsEnabled;
        this.theme = theme;
        this.language = language;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }

    public String getLastPeriodStart() {
        return lastPeriodStart;
    }

    public void setLastPeriodStart(String lastPeriodStart) {
        this.lastPeriodStart = lastPeriodStart;
    }

    public String getTrackedSymptoms() {
        return trackedSymptoms;
    }

    public void setTrackedSymptoms(String trackedSymptoms) {
        this.trackedSymptoms = trackedSymptoms;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}


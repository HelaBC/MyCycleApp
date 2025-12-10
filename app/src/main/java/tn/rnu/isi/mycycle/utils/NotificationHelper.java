package tn.rnu.isi.mycycle.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import tn.rnu.isi.mycycle.receivers.NotificationReceiver;

public class NotificationHelper {

    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String NOTIFICATION_TITLE = "notification_title";
    public static final String NOTIFICATION_MESSAGE = "notification_message";

    public static final String TYPE_PERIOD = "period_reminder";
    public static final String TYPE_FERTILE = "fertile_window";
    public static final String TYPE_DAILY_LOG = "daily_log";

    private static final int REQUEST_CODE_PERIOD = 1001;
    private static final int REQUEST_CODE_FERTILE = 1002;
    private static final int REQUEST_CODE_DAILY = 1003;

    /**
     * Schedule period reminder notification
     * 
     * @param context         Application context
     * @param daysUntilPeriod Number of days until expected period
     */
    public static void schedulePeriodReminder(Context context, int daysUntilPeriod) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NOTIFICATION_TYPE, TYPE_PERIOD);
        intent.putExtra(NOTIFICATION_TITLE, "Period Reminder");
        intent.putExtra(NOTIFICATION_MESSAGE, "Your period is expected in " + daysUntilPeriod + " days");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PERIOD,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule for 2 days before period (or sooner if period is very close)
        int daysBeforeToNotify = Math.min(2, daysUntilPeriod);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, daysBeforeToNotify);
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 9 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Schedule fertile window notification
     * 
     * @param context          Application context
     * @param daysUntilFertile Number of days until fertile window starts
     */
    public static void scheduleFertileWindowReminder(Context context, int daysUntilFertile) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NOTIFICATION_TYPE, TYPE_FERTILE);
        intent.putExtra(NOTIFICATION_TITLE, "Fertile Window");
        intent.putExtra(NOTIFICATION_MESSAGE, "Your fertile window starts in " + daysUntilFertile + " days");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_FERTILE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule for 1 day before fertile window
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, Math.max(1, daysUntilFertile));
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 9 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Schedule daily log reminder (24 hours from now, repeating daily)
     * 
     * @param context Application context
     */
    public static void scheduleDailyLogReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NOTIFICATION_TYPE, TYPE_DAILY_LOG);
        intent.putExtra(NOTIFICATION_TITLE, "Daily Log Reminder");
        intent.putExtra(NOTIFICATION_MESSAGE, "Don't forget to log your symptoms and mood today!");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_DAILY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule for 24 hours from now, then repeat daily
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    /**
     * Cancel period reminder
     */
    public static void cancelPeriodReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PERIOD,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Cancel fertile window reminder
     */
    public static void cancelFertileReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_FERTILE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Cancel daily log reminder
     */
    public static void cancelDailyLogReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_DAILY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}

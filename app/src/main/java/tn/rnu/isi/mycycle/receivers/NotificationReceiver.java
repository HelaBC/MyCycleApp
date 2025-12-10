package tn.rnu.isi.mycycle.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.activities.HomeActivity;
import tn.rnu.isi.mycycle.utils.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MyCycleNotifications";
    private static final String CHANNEL_NAME = "MyCycle Reminders";
    private static final int NOTIFICATION_ID_PERIOD = 100;
    private static final int NOTIFICATION_ID_FERTILE = 101;
    private static final int NOTIFICATION_ID_DAILY = 102;

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(NotificationHelper.NOTIFICATION_TYPE);
        String title = intent.getStringExtra(NotificationHelper.NOTIFICATION_TITLE);
        String message = intent.getStringExtra(NotificationHelper.NOTIFICATION_MESSAGE);

        createNotificationChannel(context);
        showNotification(context, type, title, message);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Reminders for period tracking and daily logs");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, String type, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null)
            return;

        // Intent to open app when notification is clicked
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.local_florist) // Using app icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Determine notification ID based on type
        int notificationId = NOTIFICATION_ID_DAILY;
        if (NotificationHelper.TYPE_PERIOD.equals(type)) {
            notificationId = NOTIFICATION_ID_PERIOD;
        } else if (NotificationHelper.TYPE_FERTILE.equals(type)) {
            notificationId = NOTIFICATION_ID_FERTILE;
        }

        notificationManager.notify(notificationId, builder.build());
    }
}

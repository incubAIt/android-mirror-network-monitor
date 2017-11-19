package com.trinitymirror.networkmonitor.sample;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;

import com.trinitymirror.networkmonitor.NetworkMonitor;
import com.trinitymirror.networkmonitor.NetworkMonitorServiceLocator;
import com.trinitymirror.networkmonitor.UsageListener;

import java.util.concurrent.TimeUnit;

public class SampleApp extends Application {

    static final Long PERIOD_IN_MILLIS = TimeUnit.HOURS.toMillis(12);

    @Override
    public void onCreate() {
        super.onCreate();

        new NetworkMonitorServiceLocator.Config(this)
                .withJobExecutionWindow(
                        (int) TimeUnit.MINUTES.toSeconds(2),
                        (int) TimeUnit.MINUTES.toSeconds(1));

        NetworkMonitor.with()
                .registerListener(createListener());
    }

    private UsageListener createListener() {
        return new UsageListener(1,
                new UsageListener.Params(
                        mb(40),
                        mb(80),
                        PERIOD_IN_MILLIS,
                        UsageListener.NetworkType.WIFI
                ),
                this::showNotification);
    }

    private void showNotification(UsageListener.Result result) {
        showNotification(this);
    }

    private static int mb(int bytes) {
        return bytes * 1024 * 1024;
    }

    public static void showNotification(Context context) {
        Intent resultIntent = new Intent(context, SampleActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context, 0,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int notifyID = 1;
        String channelId = "network-monitor-sample";
        CharSequence channelName = "Network Monitoring channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.alien)
                .setContentTitle("Warning")
                .setContentText("you're eating too many cookies!")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        // Issue the notification.
        mNotificationManager.notify(notifyID, notification);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

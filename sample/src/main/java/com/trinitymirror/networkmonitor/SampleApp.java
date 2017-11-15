package com.trinitymirror.networkmonitor;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class SampleApp extends Application {

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private UsageListener createListener() {
        return new UsageListener(1,
                new UsageListener.Params(
                        mb(10),
                        mb(20),
                        TimeUnit.DAYS.toMillis(1),
                        UsageListener.NetworkType.MOBILE
                ),
                this::showNotification);
    }

    private void showNotification(UsageListener.Result result) {
        showNotification(this);
    }

    private int mb(int bytes) {
        return bytes * 1024 * 1024;
    }

    public static void showNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "network-monitor-sample")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Warning")
                        .setContentText("you're eating too many cookies");

        Intent resultIntent = new Intent();//TODO

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, mBuilder.build());
    }

}

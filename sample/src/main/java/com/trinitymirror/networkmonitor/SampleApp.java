package com.trinitymirror.networkmonitor;

import android.app.Application;
import android.support.annotation.NonNull;

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

    private UsageListener createListener() {
        return new UsageListener(1,
                new UsageListener.Params(
                        mb(10),
                        mb(20),
                        TimeUnit.DAYS.toMillis(1),
                        UsageListener.NetworkType.MOBILE
                ),
                new UsageListener.Callback() {
                    @Override
                    public void onMaxBytesReached(@NonNull UsageListener.Result result) {
                        showNotification(result);
                    }
                });
    }

    private void showNotification(UsageListener.Result result) {
        //TODO
    }

    private int mb(int bytes) {
        return bytes * 1024 * 1024;
    }
}

package com.trinitymirror.networkmonitor.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.drive.sample.quickstart.UploadPictureActivity;
import com.trinitymirror.networkmonitor.NetworkMonitor;
import com.trinitymirror.networkmonitor.UsageListener;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatsActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private TextView rxMobileTextView;
    private TextView rxWifiTextView;
    private TextView rxTotalTextView;
    private TextView txMobileTextView;
    private TextView txWifiTextView;
    private TextView txTotalTextView;
    private TextView totalEstimated;
    private Button downloadButton;
    private Button uploadButton;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        bindViews();
        hookButtons();
        fillStats();
    }

    private void bindViews() {
        rxMobileTextView = findViewById(R.id.rxMobile);
        rxWifiTextView = findViewById(R.id.rxWifi);
        rxTotalTextView = findViewById(R.id.rxTotal);

        txMobileTextView = findViewById(R.id.txMobile);
        txWifiTextView = findViewById(R.id.txWifi);
        txTotalTextView = findViewById(R.id.txTotal);
        totalEstimated = findViewById(R.id.total_estimated);

        downloadButton = findViewById(R.id.btn_download);
        uploadButton = findViewById(R.id.btn_upload);
    }

    private void hookButtons() {
        downloadButton.setOnClickListener(v -> downloadFakeData());

        uploadButton.setOnClickListener(v -> openUploadActivity());
    }

    private void fillStats() {
        UsageListener.Result result = NetworkMonitor.with().obtainCurrentStats(
                new UsageListener.Params(0, 0,
                        SampleApp.PERIOD_IN_MILLIS, UsageListener.NetworkType.WIFI));

        long mobileRx = result.getExtras().getRxMobile();
        rxMobileTextView.setText(formatBytes(mobileRx));

        long wifiRx = result.getExtras().getRxWifi();
        rxWifiTextView.setText(formatBytes(wifiRx));

        long mobileWifiRx = result.getExtras().getRxBytes();
        rxTotalTextView.setText(formatBytes(mobileWifiRx));

        long mobileTx = result.getExtras().getTxMobile();
        txMobileTextView.setText(formatBytes(mobileTx));

        long wifiTx = result.getExtras().getTxWifi();
        txWifiTextView.setText(formatBytes(wifiTx));

        long mobileWifiTx = result.getExtras().getTxBytes();
        txTotalTextView.setText(formatBytes(mobileWifiTx));

        long totalEstimatedBytes = result.getExtras().getEstimatedBytes();
        Log.d("TAG", "estimated bytes: " + totalEstimatedBytes);
        totalEstimated.setText(formatBytes(totalEstimatedBytes));
    }

    public static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("i");
        return String.format(Locale.UK, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    private void openUploadActivity() {
        startActivity(new Intent(StatsActivity.this, UploadPictureActivity.class));
    }

    private void downloadFakeData() {
        new Thread(() -> {
            setDownloadEnabled(false);

            try {
                Response response = executeRequest("https://upload.wikimedia.org/wikipedia/commons/d/dd/Big_%26_Small_Pumkins.JPG");
                long contentLength = response.body() != null ? response.body().contentLength() : -1L;
                Log.d(TAG, "Response code: " + response.code() + ", length = " + formatBytes(contentLength));
            } catch (IOException e) {
                e.printStackTrace();
            }

            setDownloadEnabled(true);
        }).start();
    }

    private void setDownloadEnabled(final boolean enabled) {
        handler.post(() -> downloadButton.setEnabled(enabled));
    }

    private Response executeRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request).execute();
    }
}

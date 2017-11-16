package com.trinitymirror.networkmonitor.sample;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.trinitymirror.networkmonitor.R;
import com.trinitymirror.networkmonitor.permission.PermissionHelper;

public class SampleActivity extends AppCompatActivity {

    private TextView hasPermissionsTextView;
    private TextView androidApiTextView;
    private Button appStatsButton;
    private Button requestPermissionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        findViews();
        hookListeners();
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindViews();
    }

    private void findViews() {
        requestPermissionsButton = findViewById(R.id.requestButton);
        hasPermissionsTextView = findViewById(R.id.textView4);
        androidApiTextView = findViewById(R.id.textView8);
        appStatsButton = findViewById(R.id.button2);
    }

    private void hookListeners() {
        appStatsButton.setOnClickListener(v -> onAppStatsClicked());
        requestPermissionsButton.setOnClickListener(v -> onRequestClicked());
    }

    private void bindViews() {
        boolean hasPermissions = new PermissionHelper().hasPermissions(this);
        hasPermissionsTextView.setText(getString(R.string.has_permissions, hasPermissions));
        androidApiTextView.setText(getString(R.string.android_api, Build.VERSION.SDK_INT));
        appStatsButton.setEnabled(hasPermissions);
    }

    private void onAppStatsClicked() {
        startActivity(
                new Intent(this, StatsActivity.class));
    }

    private void onRequestClicked() {
        PermissionsDialogActivity.open(this, "SampleApp");
    }
}

package com.trinitymirror.networkmonitor.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.trinitymirror.networkmonitor.R

class UsageAccessView : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private lateinit var appNameTextView: TextView
    private lateinit var appIconImageView: ImageView
    private lateinit var switch: Switch

    private fun init() {
        LayoutInflater.from(context)
                .inflate(R.layout.activity_permissions_dialog_permissions_frame, this, true)

        appNameTextView = findViewById(R.id.mirror_network_monitor_permisssion_frame_app_name)
        appIconImageView = findViewById(R.id.mirror_network_monitor_permisssion_frame_icon)
        switch = findViewById(R.id.mirror_network_monitor_permisssion_frame_switch)

        postOnAnimationDelayed({
            switch.isChecked = true
        }, 1000)
    }

    fun bind(appName: String, iconResourceId: Int) {
        appNameTextView.text = appName
        appIconImageView.setImageResource(iconResourceId)
    }

}
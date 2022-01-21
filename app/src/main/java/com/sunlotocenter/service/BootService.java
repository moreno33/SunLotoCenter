package com.sunlotocenter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunlotocenter.activity.LoginActivity;

public class BootService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(context, LoginActivity.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(context, LoginActivity.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
        }
    }
}
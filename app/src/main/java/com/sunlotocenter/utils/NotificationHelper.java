package com.sunlotocenter.utils;


import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class NotificationHelper{

    private Context context = null;
    private String channelId= null;


    public NotificationHelper(Context context, String channelId){
        this.context= context;
        this.channelId= channelId;
    }


    public NotificationCompat.Builder createNotification(CharSequence title, CharSequence contentText, int smallIcon, Bitmap bigIcon,
                                                 int priority, Integer deft, boolean isCancelable){
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, channelId);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setPriority(priority)
                .setAutoCancel(isCancelable);

        if(bigIcon!= null){
            builder.setLargeIcon(bigIcon);
        }
        if(deft != null) builder.setDefaults(deft);

        return builder;
    }



    public NotificationChannel createNotificationChannel(String channelName, int importance,
                                                         boolean isLight, int lightColor){

        NotificationChannel channel= null;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            channel= new NotificationChannel(this.channelId, channelName, importance);
            channel.enableLights(isLight);
            if (isLight)channel.setLightColor(lightColor);
        }

        return channel;
    }

}

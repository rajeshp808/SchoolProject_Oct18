package com.rajesh.zphschoolemani;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

public class NotificationHelper {
    public static void displayNotification(Context context,String title,String body){

        PendingIntent pi=PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Uri path=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(path);
        NotificationManagerCompat mNotifManager=NotificationManagerCompat.from(context);
        mNotifManager.notify(1,mBuilder.build());
    }
    public static void displayPictureNotification(Context context,String title,String body){
        Bitmap picture=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.mainbuild);
        PendingIntent pi=PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(picture))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Uri path=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(path);
        NotificationManagerCompat mNotifManager=NotificationManagerCompat.from(context);
        mNotifManager.notify(1,mBuilder.build());
    }
}

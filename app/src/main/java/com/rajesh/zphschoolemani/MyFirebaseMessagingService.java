package com.rajesh.zphschoolemani;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "sowmuch onMessageReceived: ");
        if(remoteMessage.getNotification()!=null) {
            String title=remoteMessage.getNotification().getTitle();
            String text=remoteMessage.getNotification().getBody();
            NotificationHelper.displayNotification(getApplicationContext(),title,text);
            NotificationHelper.displayPictureNotification(getApplicationContext(),title,text);
        }
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }
}

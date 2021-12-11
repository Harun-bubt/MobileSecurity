package com.mobilesecurity.mobileapp.receiver;


import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.os.IBinder;

/**
 * Created by dell on 12/10/2017.
 */

public class NotificationReceiver extends NotificationListenerService {
    private MediaPlayer mediaPlayer;
    Context context;
    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static final class ApplicationPackageNames {

        public static final String ABCMOBILESECURITY_PACK_NAME = "com.abc.security";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int ABCMOBILESECURITY_CODE = 1;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);






        //Log.v("Notification title is:", title);
            //Log.v("Notification text is:", text);
            //Log.v("Notification Package Name is:", package_name);


        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {



            Intent i = new  Intent("com.abc.security.NOTIFICATION_LISTENER");

            Bundle extras = sbn.getNotification().extras;

            if(sbn.getPackageName().contains("com.abc.security"))
            {
                String title = extras.getString(Notification.EXTRA_TITLE);
                String summary = extras.getString(Notification.EXTRA_SUMMARY_TEXT);
                String msg = extras.getString(Notification.EXTRA_TEXT);

                if(msg != null)
                {
                    i.putExtra("notification_event", msg);
                }
                else
                {
                    i.putExtra("notification_event", summary);
                }

            }
            else
            {
                i.putExtra("notification_event","...");
            }
            sendBroadcast(i);




        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        Intent intent = new  Intent("com.abc.security");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.ABCMOBILESECURITY_PACK_NAME)){


            return(InterceptedNotificationCode.ABCMOBILESECURITY_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }


}
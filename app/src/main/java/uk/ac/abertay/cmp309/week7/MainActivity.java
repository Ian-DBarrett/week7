package uk.ac.abertay.cmp309.week7;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int NOTIFICATION_ID_TEXT = 1;
    public static final String ACTION_RESET = "uk.ac.abertay.cmp309.ACTION_RESET";
    public static final String CHANNEL_ID_IMPORTANT = "week7_important";
    public static final String CHANNEL_ID_NORMAL = "week7_normal";

    TimeReceiver timeReceiver;
    NotificationManager notificationManager;
    Notification.Builder timerNotification;
    Notification.Builder textNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Initialise an instance of the receiver for runtime use. This is a private class declared at the bottom. */
        timeReceiver = new TimeReceiver();
        /* Initialise a notification manager. */
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* !Make sure to initialise all channels before using them! */
        initNotificationChannels();

        /* Create intent filter and register the receiver. */
        IntentFilter timerFilter = new IntentFilter();
        /* We want to listen to 2 different events */
        timerFilter.addAction(Intent.ACTION_TIME_TICK); /* For timer update. */
        timerFilter.addAction(ACTION_RESET); /* Custom action for timer reset. */
        this.registerReceiver(timeReceiver, timerFilter);

        /* Create the timer notification. */
        Intent resetIntent = new Intent();
        resetIntent.setAction(ACTION_RESET); /* Use custom reset action for "RESET" button intent. */
        PendingIntent resetPI = PendingIntent.getBroadcast(getApplicationContext(), 0, resetIntent, 0);
        Icon resetIcon = Icon.createWithResource(this, android.R.drawable.ic_menu_revert);
        Notification.Action resetAction = new Notification.Action.Builder(resetIcon, "Reset", resetPI).build();
        timerNotification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_IMPORTANT)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle("App timer")
                .addAction(resetAction);

        /* Create the text notification. */
        /* The press on the notification will open MainActivity. */
        Intent openIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent openPI = PendingIntent.getActivity(getApplicationContext(), 0, openIntent, 0);
        textNotification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_IMPORTANT)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Text notification")
                .setContentIntent(openPI)
                .setAutoCancel(true);

        //display full text
        Intent fullintent = new Intent();
        PendingIntent fullPI = PendingIntent.
    }

    /* Initialises notification channels */
    private void initNotificationChannels(){
        /* If using older version which does not support channels, ignore this */
        if(Build.VERSION.SDK_INT < 26){
            return;
        }

        /* Create all channels and add them to the list */
        ArrayList<NotificationChannel> channelList = new ArrayList<>();
        channelList.add(new NotificationChannel(CHANNEL_ID_IMPORTANT,"IMPORTANT", NotificationManager.IMPORTANCE_HIGH));
        channelList.add(new NotificationChannel(CHANNEL_ID_NORMAL,"DEFAULT", NotificationManager.IMPORTANCE_DEFAULT));

        /* Register all channels from the list. */
        if(notificationManager != null)
            notificationManager.createNotificationChannels(channelList);
    }

    /* Handles button clicks. */
    public void buttonHandler(View view){
        if(view.getId() == R.id.btnNotify) {
            /* Set notification text and send it. */
            textNotification.setContentText(((EditText) findViewById(R.id.etNotificationText)).getText().toString());
            notificationManager.notify(NOTIFICATION_ID_TEXT, textNotification.build());
        } else if(view.getId() == R.id.btnHideTimer){
            /* Remove timer notification from drawer. */
            notificationManager.cancel(TimeReceiver.NOTIFICATION_ID_TIMER);
        }
    }

    @Override
    protected void onDestroy() {
        /* !Always unregister receivers before exiting the application! */
        this.unregisterReceiver(timeReceiver);
        super.onDestroy();
    }

    private class TimeReceiver extends BroadcastReceiver {
        final static int NOTIFICATION_ID_TIMER = 2;
        private int time = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Intent.ACTION_TIME_TICK: time++; break; /* This event will occur every minute. */
                case ACTION_RESET: time = 0; break; /* This will when "RESET" button in notification is pressed. */
            }
            /* Update the timer text and send notification. */
            timerNotification.setContentText("App lifetime is: "+time+" minute(s)");
            notificationManager.notify(NOTIFICATION_ID_TIMER, timerNotification.build());
        }
    }
}




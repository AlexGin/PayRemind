package android.alexgin.payremind;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import android.graphics.Color;
import android.os.Build;

import android.util.Log;

import java.util.UUID;

public class PRMessageService extends IntentService {

    private static final String TAG = "PRMessageService";
    public static final String EXTRA_PAY_INFO = "pay_info";
    public static final String CHANNEL_ID = "android.alexgin.payremind";
    public static final int NOTIFICATION_ID = 5459;

    public PRMessageService() {
        super("PRMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UUID id = (UUID)intent.getSerializableExtra(EXTRA_PAY_INFO);
        PaymentLab lab = PaymentLab.get(this);

        Pay pay = lab.getPay(id); // intent.getParcelableExtra(EXTRA_PAY_INFO);
        if (pay == null)
        {
            Log.d(TAG, "onHandleIntent: pay NULL");
            return;
        }
        int n_day = pay.getDayOfMonth();
        synchronized (this) {
            try {
                wait(1000 * n_day); // One day -> 1 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String strText = pay.getTitle(); // "НЕ ЗАБЫТЬ ОПЛАТИТЬ!";
        String strTitle = "НЕ ЗАБЫТЬ ОПЛАТИТЬ!";
        Log.d(TAG, "onHandleIntent: before 'showText' = " + strText);
        showText(strText, strTitle);
        Log.d(TAG, "onHandleIntent: executed 'showText'");
    }

    private void showText(final String text, final String title) {
        Log.d(TAG,"showText: " + text);
        long thread_id = Utils.getThreadId();
        Log.d(TAG, "showText_0: thread-id =" + thread_id + "<" + text + ">");

        // see:
        // https://startandroid.ru/ru/uroki/vse-uroki-spiskom/515-urok-190-notifications-kanaly.html
        // https://www.tabnine.com/code/java/methods/android.app.NotificationManager/getNotificationChannel
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O  &&
                notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PayRemind channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("PayRemind channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }

        //Create a notification builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVibrate(new long[] {0, 1000});

        //Create an action
        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                0,
                actionIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(actionPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}

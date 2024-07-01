package sg.edu.np.mad.travelhub;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



public class ReminderBroadcast extends BroadcastReceiver {
    private static final int reminderID = 1;
    private static final String channelID = "PlanHub";
    private static final String titleExtra = "titleExtra";
    private static final String messageExtra = "messageExtra";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "onReceive: Created NOTIFICATION");
        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.planhub_icon)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(reminderID, notification);



    }
}

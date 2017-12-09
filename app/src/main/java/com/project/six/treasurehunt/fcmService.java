package com.project.six.treasurehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//FCM을 이용하여 보물을 발견시 보물을 숨긴 사람에게 메세지를 보냅니다.
//fcmService class에서는 그 메세지를 받아 숨겨뒀던 보물이 발견됬음을 알리도록 합니다.
public class fcmService extends FirebaseMessagingService {
    private final static String TAG = "FCM_MESSAGE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Body: " + body);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.box) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0x1001, notificationBuilder.build());
        }
    }
}

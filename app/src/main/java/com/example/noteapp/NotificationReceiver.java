// NotificationReceiver.java
package com.example.noteapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (hasVibratePermission(context)) {
            // Tạo NotificationChannel nếu chưa tồn tại
            createNotificationChannel(context);

            // Lấy title của note từ Intent
            String noteTitle = intent.getStringExtra("noteTitle");

            // Hiển thị thông báo với title là tên của note
            showNotification(context, noteTitle);
        }
    }

    private boolean hasVibratePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Kiểm tra quyền VIBRATE
            return context.checkSelfPermission(android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Cho các phiên bản Android cũ hơn với manifest đã khai báo quyền
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Tạo NotificationChannel chỉ cho phiên bản Android Oreo trở lên
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);

            // Cài đặt các tùy chọn khác cho channel nếu cần
            // channel.setDescription("Description");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager.getNotificationChannel("channel_id") == null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, String noteTitle) {
        // Tạo thông báo ở đây
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Thông báo") // Set the note title as the notification title
                .setContentText(noteTitle) // Set the content text using the note title
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}

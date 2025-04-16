package com.example.notificationdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    /*The notification manager requires to be called from a method .That is the reason a custom method is created
    *
    * */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SnapeChannel";
            String description = "Channel for Severous Snape's notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //A new notification channel object is created with id, name , importance and description
            NotificationChannel channel = new NotificationChannel("snape_channel", name, importance);
            channel.setDescription(description);
            //Now the  channel object  is passed on to Notification manager which can create notifications
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //now the function call creates the channel
        createNotificationChannel();
        Button sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(v -> {
            //this is the android notification builder. It lets us create Title , ,essage icon etc..
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "snape_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Dark Arts Alert")
                    .setContentText("The Dark Mark has been seen in the sky.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
            //notification sent

            notificationManager.notify(1, builder.build());
        });
    }
}
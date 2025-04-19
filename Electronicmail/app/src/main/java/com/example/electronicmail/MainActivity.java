package com.example.electronicmail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText etTo, etSubject, etMessage;
    Button btnSend, btnReceive;
    TextView tvReceived;

    String userEmail = "reddyravindra290@gmail.com"; // Use app password
    String appPassword = "R@vi2004";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.etTo);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnReceive = findViewById(R.id.btnReceive);
        tvReceived = findViewById(R.id.tvReceived);

        btnSend.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    EmailUtil.sendEmail(
                            userEmail,
                            appPassword,
                            etTo.getText().toString(),
                            etSubject.getText().toString(),
                            etMessage.getText().toString()
                    );
                    runOnUiThread(() -> Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        btnReceive.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    String emailContent = EmailFetchUtil.fetchLatestEmail(userEmail, appPassword);
                    runOnUiThread(() -> tvReceived.setText(emailContent));
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}
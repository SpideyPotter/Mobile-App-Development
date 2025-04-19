package com.example.sendingemail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiveEmailActivity extends AppCompatActivity {

    private EditText etGmailUsername, etGmailPassword;
    private Button btnLogin;
    private TextView tvInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_email);

        etGmailUsername = findViewById(R.id.etGmailUsername);
        etGmailPassword = findViewById(R.id.etGmailPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvInbox = findViewById(R.id.tvInbox);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etGmailUsername.getText().toString();
                String password = etGmailPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ReceiveEmailActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                // In a real app, you would use JavaMail or Gmail API here
                // For demo, we'll simulate received emails
                simulateReceivedEmails();
            }
        });

        // Add a direct Gmail button
        Button btnOpenGmail = findViewById(R.id.btnOpenGmail);
        btnOpenGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGmailApp();
            }
        });
    }

    private void simulateReceivedEmails() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        StringBuilder inbox = new StringBuilder();
        inbox.append("From: john.doe@example.com\n");
        inbox.append("Subject: Meeting Tomorrow\n");
        inbox.append("Date: ").append(currentDate).append("\n");
        inbox.append("Content: Hi there, just confirming our meeting tomorrow at 10 AM.\n\n");

        inbox.append("From: support@company.com\n");
        inbox.append("Subject: Your Recent Inquiry\n");
        inbox.append("Date: ").append(currentDate).append("\n");
        inbox.append("Content: Thank you for contacting us. We've processed your request.\n\n");

        inbox.append("From: newsletter@tech.com\n");
        inbox.append("Subject: Weekly Tech Updates\n");
        inbox.append("Date: ").append(currentDate).append("\n");
        inbox.append("Content: Check out the latest tech news and developments this week!");

        tvInbox.setText(inbox.toString());
        Toast.makeText(this, "Inbox updated (simulated)", Toast.LENGTH_SHORT).show();
    }

    private void openGmailApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            startActivity(intent);
        } catch (Exception e) {
            // If Gmail app is not installed, open Gmail in browser
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"));
                startActivity(browserIntent);
            } catch (Exception ex) {
                Toast.makeText(this, "No email application or browser found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
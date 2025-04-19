package com.example.sendingemail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SendEmailActivity extends AppCompatActivity {

    private EditText etTo, etSubject, etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        etTo = findViewById(R.id.etTo);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = etTo.getText().toString();
                String subject = etSubject.getText().toString();
                String message = etMessage.getText().toString();

                if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                    Toast.makeText(SendEmailActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Method 1: Using Intent to send via installed email apps
                sendEmailViaIntent(to, subject, message);
            }
        });
    }

    private void sendEmailViaIntent(String to, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
            Toast.makeText(this, "Email sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

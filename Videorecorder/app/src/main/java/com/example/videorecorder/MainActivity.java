package com.example.videorecorder;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    private PreviewView previewView;
    private ImageButton recordButton;
    private TextView timerText;

    private VideoCapture<Recorder> videoCapture;
    private Recording currentRecording;

    private CountDownTimer timer;
    private boolean isRecording = false;
    private long seconds = 0;

    private final String[] REQUIRED_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_VIDEO}
            : new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        recordButton = findViewById(R.id.recordButton);
        timerText = findViewById(R.id.timerText);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        recordButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                if (allPermissionsGranted()) {
                    startRecording();
                } else {
                    Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder().build();
                videoCapture = VideoCapture.withOutput(recorder);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);

                Toast.makeText(this, "Camera ready!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private void startRecording() {
        File file = new File(getExternalFilesDir(null),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date()) + ".mp4");

        FileOutputOptions options = new FileOutputOptions.Builder(file).build();

        PendingRecording pendingRecording = videoCapture.getOutput()
                .prepareRecording(this, options)
                .withAudioEnabled();

        currentRecording = pendingRecording.start(ContextCompat.getMainExecutor(this), event -> {
            if (event instanceof VideoRecordEvent.Start) {
                isRecording = true;
                startTimer();
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            } else if (event instanceof VideoRecordEvent.Finalize) {
                isRecording = false;
                stopTimer();
                Toast.makeText(this, "Recording saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopRecording() {
        if (currentRecording != null) {
            currentRecording.stop();
            currentRecording = null;
        }
    }
    private void startTimer() {
        seconds = 0;

        // 🔴 Blinking animation for the red dot
        TextView redDot = findViewById(R.id.redDot);
        redDot.setAlpha(1f); // make sure it's visible

        ObjectAnimator blink = ObjectAnimator.ofFloat(redDot, "alpha", 1f, 0f);
        blink.setDuration(500);
        blink.setRepeatMode(ValueAnimator.REVERSE);
        blink.setRepeatCount(ValueAnimator.INFINITE);
        blink.start();

        // store animator reference to stop it later
        redDot.setTag(blink);

        // ⏱ Start countdown timer
        timer = new CountDownTimer(1000 * 60 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds++;
                long hrs = seconds / 3600;
                long mins = (seconds % 3600) / 60;
                long secs = seconds % 60;
                timerText.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs));
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        }.start();
    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timerText.setText("00:00:00");
        TextView redDot = findViewById(R.id.redDot);
        Object tag = redDot.getTag();

        if (tag instanceof ObjectAnimator) {
            ((ObjectAnimator) tag).cancel();
        }

        redDot.setAlpha(1f); // fully visible (non-blinking)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(this, "Permissions granted ✅", Toast.LENGTH_SHORT).show();
                startCamera();
            } else {
                Toast.makeText(this, "Permissions are required ❌", Toast.LENGTH_LONG).show();
            }
        }
    }
}
package com.example.cameraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private boolean isFlashOn = false;

    private PreviewView previewView;
    private ImageButton shutterBtn, flipBtn, flashButton;
    private ImageView previewThumbnail;

    private ImageCapture imageCapture;
    private CameraSelector cameraSelector;
    private boolean isFrontCamera = false;

    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        shutterBtn = findViewById(R.id.Shutter);
        flipBtn = findViewById(R.id.CameraFlip);
        flashButton = findViewById(R.id.Flash);
        previewThumbnail = findViewById(R.id.previewThumbnail);

        cameraExecutor = Executors.newSingleThreadExecutor();

        requestCameraPermission.launch(Manifest.permission.CAMERA);

        shutterBtn.setOnClickListener(v -> takePhoto());
        flipBtn.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera;
            startCamera();
        });
        flashButton.setOnClickListener(v -> toggleFlash());
    }

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                cameraProvider.unbindAll();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(isFrontCamera ?
                                CameraSelector.LENS_FACING_FRONT :
                                CameraSelector.LENS_FACING_BACK)
                        .build();

                // Assign camera instance globally
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                );

                // Reset flash state on camera change
                isFlashOn = false;
                camera.getCameraControl().enableTorch(false);
                flashButton.setImageResource(R.drawable.striked_flash);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(getExternalFilesDir(null),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> {
                            Uri savedUri = Uri.fromFile(photoFile);
                            previewThumbnail.setImageURI(savedUri);
                            Toast.makeText(MainActivity.this, "Photo Saved!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraApp", "Photo capture failed: " + exception.getMessage(), exception);
                    }
                });
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
            flashButton.setImageResource(isFlashOn ? R.drawable.flash : R.drawable.striked_flash);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
package com.example.videoplayer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button btnPlayPause, btnReplay;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnReplay = findViewById(R.id.btnReplay);
        seekBar = findViewById(R.id.seekBar);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video);
        videoView.setVideoURI(videoUri);

        btnPlayPause.setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setText("Play");
            } else {
                videoView.start();
                btnPlayPause.setText("Pause");
                updateSeekBar();
            }
        });

        btnReplay.setOnClickListener(view -> {
            videoView.seekTo(0);
            videoView.start();
            btnPlayPause.setText("Pause");
            updateSeekBar();
        });

        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            updateSeekBar();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoView.isPlaying()) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
                videoView.seekTo(seekBar.getProgress());
            }
        });
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying() && !isTracking) {
                    seekBar.setProgress(videoView.getCurrentPosition());
                }
                handler.postDelayed(this, 500);
            }
        }, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

package com.example.music_player;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.SeekBar;
import android.os.Handler;
public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
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
        mediaPlayer= MediaPlayer.create(this,R.raw.song);
        ImageButton play = findViewById(R.id.imageButton3);
        TextView currentTime = findViewById(R.id.currentTime);
        TextView totalTime = findViewById(R.id.totalTime);
        int duration = mediaPlayer.getDuration();
        totalTime.setText(formatTime(duration));
        currentTime.setText(formatTime(0));
        play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                play.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                play.setImageResource(android.R.drawable.ic_media_pause);
            }
        });
        SeekBar sb;
        sb = findViewById(R.id.seekBar2);
        sb.setMax(mediaPlayer.getDuration());
        Handler h = new Handler();
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                sb.setProgress(mediaPlayer.getCurrentPosition());
                h.postDelayed(this, 500);
                int current = mediaPlayer.getCurrentPosition();
                currentTime.setText(formatTime(current));
            }
        };
        h.postDelayed(updateSeekBar, 0);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(fromUser && mediaPlayer!=null){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional
            }
        });
        mediaPlayer.setOnCompletionListener(mp->{
            play.setImageResource(android.R.drawable.ic_media_play);
            sb.setProgress(0);
            currentTime.setText(formatTime(0));
        });

    }
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
package com.example.testaudio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;




public class MainActivity extends AppCompatActivity {

 //   https://www.youtube.com/watch?v=Z4DQdeMAJRE

    ImageView imgPlay ,imgPause;
    TextView textCurrentTime, textTotalDuration;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;

    Handler  handler = new Handler();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPlay = findViewById(R.id.imgPlay);
        imgPause = findViewById(R.id.imgPause);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        seekbar = findViewById(R.id.playerSeekbar);


        mediaPlayer = new MediaPlayer();

        seekbar.setMax(100);



        imgPlay.setOnClickListener(v -> {
            prepareMediaPlayer();
            mediaPlayer.start();
            imgPlay.setVisibility(View.GONE);
            imgPause.setVisibility(View.VISIBLE);
            updateSeekbar();
        });

        imgPause.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()){
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
                imgPlay.setVisibility(View.VISIBLE);
                imgPause.setVisibility(View.GONE);
            }
        });




        seekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                SeekBar seekBar = (SeekBar) view;
                int playPosition = (mediaPlayer.getDuration() / 100)*seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });


        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                seekbar.setSecondaryProgress(i);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                 seekbar.setProgress(0);

                 imgPlay.setVisibility(View.VISIBLE);
                 imgPause.setVisibility(View.GONE);
//                 imagePlayPause.setImageResource(R.drawable.play);
                 textCurrentTime.setText("0:00");
                 textTotalDuration.setText("0:00");
                 mediaPlayer.reset();
//                 prepareMediaPlayer();
            }
        });

    }

    private void prepareMediaPlayer(){

        try {


            String path = "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_2MG.mp3";


            // to read from cache
            HttpProxyCacheServer proxyServer = new HttpProxyCacheServer.Builder(this).maxCacheSize(1024 * 1024 * 1024).build();
            String proxyUrl = proxyServer.getProxyUrl(path);

            mediaPlayer.setDataSource(proxyUrl);

            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));

        }catch (Exception exception){

            Toast.makeText(this, ""+exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }








    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondToTimer(currentDuration));
        }
    };


    private void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            seekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) *100 ));
            handler.postDelayed(updater , 1000);
        }
    }



    private String milliSecondToTimer (long milliSeconds){

        String timerString ="";
        String secondsString;
        int hour = (int) (milliSeconds/(1000*60*60));
        int minutes = (int) (milliSeconds % (1000*60*60)) /(1000*60);
        int seconds = (int) ((milliSeconds % (1000*60*60)) %(1000*60)/1000);

        if(hour > 0){
            timerString =  hour + ":";
        }

        if(seconds<10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }


}
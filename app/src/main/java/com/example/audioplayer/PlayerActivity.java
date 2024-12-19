package com.example.audioplayer;

import static com.example.audioplayer.MainActivity.repeatBoolean;
import static com.example.audioplayer.MainActivity.shuffleBoolean;
import static com.example.audioplayer.adapters.AllSongsAdapter.allSongsList;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.audioplayer.model.AllMusicFiles;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{



    ShapeableImageView player_album_art;
    float radius = 36f;



    TextView song_name_title , player_song_name , player_artist_name , tv_progress_start , tv_progress_end ;
    ImageView back , shuffle_button , prev_button ,play_pause_button, next_button , repeat_button ;

    SeekBar songSeekBar;

    int position = - 1;

    public static List<AllMusicFiles> musicList = new ArrayList<>();

    public static Uri uri;

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    Handler handler = new Handler();

    Thread playPauseThread , prevThread , nextThread ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initVeriable();

        SHAPED_IMAGE_VIEW();
        getIntentMethod();
        player_song_name.setSelected(true);

        player_song_name.setText(musicList.get(position).getSongName());
        player_artist_name.setText(musicList.get(position).getArtistName());
        mediaPlayer.setOnCompletionListener(this);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mediaPlayer != null && fromUser){

                    mediaPlayer.seekTo(progress*1000);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null){

                    int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                    songSeekBar.setProgress(currentPosition);
                    tv_progress_start.setText(formattedTime(currentPosition));
                }

                handler.postDelayed(this,1000);

            }
        });

        shuffle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shuffleBoolean){

                    shuffleBoolean = false;
                    shuffle_button.setImageResource(R.drawable.shuffle_off);
                }else {

                    shuffleBoolean = true;
                    shuffle_button.setImageResource(R.drawable.shuffle_on);
                }
            }
        });
        repeat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (repeatBoolean){

                    repeatBoolean = false;
                    repeat_button.setImageResource(R.drawable.repeat_off);
                }else {

                    repeatBoolean = true;
                    repeat_button.setImageResource(R.drawable.repeat_on);
                }


            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });



    }

    private String formattedTime(int currentPosition) {

        String total_out = "";
        String total_new = "";
        String seconds = String.valueOf(currentPosition%60);
        String minutes = String.valueOf(currentPosition/60);

        total_out = minutes + ":" + seconds;

        total_new = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1){

            return total_new;

        }else {

            return total_out;
        }

    }

    private void metaData(Uri uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration_total = Integer.parseInt(musicList.get(position).getDuration())/1000;
        tv_progress_end.setText(formattedTime(duration_total));

        byte [] img = retriever.getEmbeddedPicture();

        if (img!=null){

            Glide.with(this).asBitmap().load(img).into(player_album_art);

        }else {

            player_album_art.setImageResource(R.drawable.demo);

        }


    }

    public void SHAPED_IMAGE_VIEW(){

        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .build();
        player_album_art.setShapeAppearanceModel(shapeAppearanceModel);


    }

    private void getIntentMethod(){

        position = getIntent().getIntExtra("position",-1);

        String All = getIntent().getStringExtra("All" );

        if (All != null && All.equals("all_music")){

            musicList = allSongsList;

        }else {

           // Toast.makeText(PlayerActivity.this, "", Toast.LENGTH_SHORT).show();
        }

        if (musicList != null){

            play_pause_button.setImageResource(R.drawable.pause);
            uri = Uri.parse(musicList.get(position).getPath());

        }
        if (mediaPlayer != null){

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else {

            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();

        }
        songSeekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);




    }


    @Override
    protected void onResume() {
        playPauseThreadBtn();
        preThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    private void preThreadBtn() {

        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();

                prev_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });

            }

        };

        prevThread.start();



    }

    private void prevBtnClicked() {

        if (mediaPlayer.isPlaying()){

            mediaPlayer.stop();
            mediaPlayer.release();


            if (shuffleBoolean && !repeatBoolean){

                position = getRandom(musicList.size() -1);

            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position - 1 ) < 0 ? (musicList.size()-1) : (position -1 ));
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        }else {


            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){

                position = getRandom(musicList.size() -1);

            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position - 1 ) < 0 ? (musicList.size()-1 ) : (position -1 ));
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.play);

            //mediaPlayer.start();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(PlayerActivity.this, MainActivity.class));

                }
            });


        }


    }

    private void nextThreadBtn() {

        nextThread= new Thread(){
            @Override
            public void run() {
                super.run();

                next_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });

            }

        };

        nextThread.start();
    }

    private void nextBtnClicked() {

        if (mediaPlayer.isPlaying()){

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandom(musicList.size() -1);

            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position+1) % musicList.size());
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        }else {


            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandom(musicList.size() -1);

            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position+1) % musicList.size());
            }
            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.play);


        }


    }

    private int getRandom(int i) {

        Random random = new Random();

        return random.nextInt(i+1);

    }

    private void playPauseThreadBtn() {

        playPauseThread = new Thread(){
            @Override
            public void run() {
                super.run();

                play_pause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });

            }

        };

        playPauseThread.start();
    }


    private void playPauseBtnClicked() {


        if (mediaPlayer.isPlaying()){

            play_pause_button.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

        }else {

            play_pause_button.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            songSeekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){

                        int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                        songSeekBar.setProgress(currentPosition);

                    }

                    handler.postDelayed(this,1000);

                }
            });

        }

    }


    private void initVeriable() {

        player_album_art = findViewById(R.id.player_album_art);
        song_name_title = findViewById(R.id.song_name_title);
        player_song_name = findViewById(R.id.player_song_name);
        player_artist_name = findViewById(R.id.player_artist_name);
        tv_progress_start = findViewById(R.id.tvProgressStart);
        tv_progress_end = findViewById(R.id.tvProgressEnd);
        back = findViewById(R.id.back);
        shuffle_button = findViewById(R.id.shuffle_button);
        prev_button = findViewById(R.id.prev_button);
        next_button = findViewById(R.id.next_button);
        play_pause_button = findViewById(R.id.play_pause_button);
        repeat_button = findViewById(R.id.repeat_button);
        songSeekBar = findViewById(R.id.songSeekBar);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        nextBtnClicked();
        if (mediaPlayer!=null){

            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);

        }

    }


}
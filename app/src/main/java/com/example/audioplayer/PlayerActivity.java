package com.example.audioplayer;

import static com.example.audioplayer.MainActivity.repeatBoolean;
import static com.example.audioplayer.MainActivity.shuffleBoolean;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.audioplayer.model.AllMusicFiles;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.jummania.DataManager;
import com.jummania.DataManagerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {


    ShapeableImageView player_album_art;
    TextView song_name_title, player_song_name, player_artist_name, tv_progress_start, tv_progress_end;
    ImageView back, shuffle_button, prev_button, play_pause_button, next_button, repeat_button;
    SeekBar songSeekBar;
    int position = -1;
    private List<AllMusicFiles> musicList = new ArrayList<>();
    private Uri uri;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initVariable();

        SHAPED_IMAGE_VIEW();
        getIntentMethod();
        player_song_name.setSelected(true);

        player_song_name.setText(musicList.get(position).getSongName());
        player_artist_name.setText(musicList.get(position).getArtistName());
        mediaPlayer.setOnCompletionListener(this);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                setMediaProgress();
                handler.removeCallbacksAndMessages(this);
                handler.postDelayed(this, 1000);
            }
        });

        shuffle_button.setOnClickListener(v -> {

            if (shuffleBoolean) {
                shuffleBoolean = false;
                shuffle_button.setImageResource(R.drawable.shuffle_off);
            } else {
                shuffleBoolean = true;
                shuffle_button.setImageResource(R.drawable.shuffle_on);
            }
        });
        repeat_button.setOnClickListener(v -> {

            if (repeatBoolean) {
                repeatBoolean = false;
                repeat_button.setImageResource(R.drawable.repeat_off);
            } else {
                repeatBoolean = true;
                repeat_button.setImageResource(R.drawable.repeat_on);
            }


        });
        back.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        play_pause_button.setOnClickListener(v -> playPauseBtnClicked());

        prev_button.setOnClickListener(v -> prevBtnClicked());

        next_button.setOnClickListener(v -> nextBtnClicked());


    }

    private void setMediaProgress() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
            songSeekBar.setProgress(currentPosition);
            tv_progress_start.setText(formattedTime(currentPosition));
        }
    }

    private String formattedTime(int currentPosition) {

        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);

        String total_out = minutes + ":" + seconds;

        String total_new = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) return total_new;
        else return total_out;

    }

    private void metaData(Uri uri) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(uri.toString());
            int duration_total = Integer.parseInt(musicList.get(position).getDuration()) / 1000;
            tv_progress_end.setText(formattedTime(duration_total));

            byte[] img = retriever.getEmbeddedPicture();

            if (img != null) {
                Glide.with(this).asBitmap().load(img).into(player_album_art);
            } else {
                player_album_art.setImageResource(R.drawable.demo);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SHAPED_IMAGE_VIEW() {
        float radius = 36f;
        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder().setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).setBottomRightCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build();
        player_album_art.setShapeAppearanceModel(shapeAppearanceModel);
    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("position", -1);

        DataManager dataManager = DataManagerFactory.create(getFilesDir());
        musicList = dataManager.getList("allSongs", AllMusicFiles.class);

        if (musicList != null) {
            play_pause_button.setImageResource(R.drawable.pause);
            uri = Uri.parse(musicList.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);

    }


    private void prevBtnClicked() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(musicList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (musicList.size() - 1) : (position - 1));
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();

        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(musicList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (musicList.size() - 1) : (position - 1));
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.play);

            //mediaPlayer.start();

            back.setOnClickListener(v -> startActivity(new Intent(PlayerActivity.this, MainActivity.class)));


        }

        setMediaProgress();

    }


    private void nextBtnClicked() {

        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(musicList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % musicList.size());
            }

            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        } else {


            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(musicList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % musicList.size());
            }
            uri = Uri.parse(musicList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            player_song_name.setText(musicList.get(position).getSongName());
            player_artist_name.setText(musicList.get(position).getArtistName());
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);

            mediaPlayer.setOnCompletionListener(this);
            play_pause_button.setBackgroundResource(R.drawable.play);


        }

        setMediaProgress();

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            play_pause_button.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
        } else {
            play_pause_button.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
        }

    }


    private void initVariable() {

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
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }


}
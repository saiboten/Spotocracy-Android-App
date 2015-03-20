// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
package saiboten.no.spotifytestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;
import com.spotify.sdk.android.playback.PlayerStateCallback;
import com.spotify.sdk.android.playback.Config;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import saiboten.no.spotifytestapp.callbacks.UpdateMetaDataCallback;
import saiboten.no.spotifytestapp.tasks.DownloadImageTask;
import saiboten.no.spotifytestapp.tasks.GetSongByRestTask;
import saiboten.no.spotifytestapp.tasks.GetSongInfoByRestTask;

public class MainActivity extends Activity {

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;

    private static final String REDIRECT_URI = "spotocracy://callback";

    private Spotify spotify = null;

    private Handler handler = new Handler();

    private ProgressBar songProgress;

    private ImageButton playpausebutton;

    private TextView timePlayed;

    public TextView songLength;

    private EditText playlist;

    public int songDurationSeconds = 100;

    private int secondsPlayedTotal = 0;

    private int seconds = 0;

    private int minutes = 0;

    private SeekBar volumeSlider;

    private int maxVolume = 0;

    private AudioManager am;

    private TextView songinfo;

    private ImageView imageView;

    private SpotifyIntegrationComponent spotifyIntegrationComponent;

    private SpotifyPlayerHandler spotifyPlayerHandler;

    private static final String CLIENT_ID = "b60120e0052b4973b2a89fab00925019";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        songProgress = (ProgressBar) findViewById(R.id.progressBar);
        timePlayed = (TextView) findViewById(R.id.timePlayed);
        playlist = (EditText) findViewById(R.id.playlist);
        playpausebutton = (ImageButton) findViewById(R.id.button);
        songLength = (TextView) findViewById(R.id.song_length);
        volumeSlider = (SeekBar) findViewById(R.id.seekBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        songinfo = (TextView) findViewById(R.id.songinfo);

        volumeSlider.setEnabled(false);

        am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        setupClickListeners();
        initComponents();

        new android.os.Handler().postDelayed(new UpdateTime(), 1000);
    }

    public void initComponents()  {
        spotifyIntegrationComponent = new SpotifyIntegrationComponent(this);
        spotifyPlayerHandler = new SpotifyPlayerHandler(this, spotifyIntegrationComponent);
    }

    public void setupClickListeners() {
        ImageButton newTrack = (ImageButton) findViewById(R.id.button);
        newTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEventHandler();
            }
        });

        ImageButton nextTrack = (ImageButton) findViewById(R.id.next_button);
        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewSong();
            }
        });

        volumeSlider.setOnSeekBarChangeListener(new VolumeChangeListener(maxVolume, am));
    }

    private void resume() {
        Log.d("MainActivity", "Playing/Resuming");
        spotifyPlayerHandler.resume();
        playpausebutton.setImageResource(R.drawable.pause);
    }

    private void pause() {
        Log.d("MainActivity", "Pausing");
        spotifyPlayerHandler.pause();
        playpausebutton.setImageResource(R.drawable.play_button);
    }

    public void clickEventHandler() {
        if(spotifyPlayerHandler.getPlayer() != null && spotifyPlayerHandler.getPlayer().isInitialized() && spotifyPlayerHandler.getPlayer().isLoggedIn()) {
            spotifyPlayerHandler.getPlayer().getPlayerState(new PlayerStateCallback() {
                @Override
                public void onPlayerState(PlayerState playerState) {
                    if(playerState.playing) {
                        pause();
                    }
                    else {
                        resume();
                    }
                }
            });
        }
        else {
            Log.d("MainActivity", "Playlist selected: " + playlist.getText());

            if(playlist.getText().toString().equals("")) {
                Log.d("MainActivity", "No playlist selected");
                Toast.makeText(getApplicationContext(), "Du må velge en spilleliste", Toast.LENGTH_SHORT).show();
            }
            else {
                AuthenticationRequest.Builder builder =
                        new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                builder.setScopes(new String[]{"streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.d("MainActivity", "onActivityResult" + resultCode);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            Log.d("MainActivity", "Response type:" + response.getType());

            switch (response.getType()) {
                case TOKEN:
                    Log.d("MainActivity", "Token granted!");
                    playerEnabled(response);
                    break;

                case ERROR:
                    Log.d("MainActivity", "Some error has occured: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("MainActivity", "Default, user cancelled? " + response.getError());
                    // Handle other cases
            }
        }
    }

    private void playerEnabled(AuthenticationResponse response) {

        Log.d("MainActivity","Enabling player");

        // Handle successful response
        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
        spotifyPlayerHandler.playerEnabled(response);

        volumeSlider.setEnabled(true);
        getNewSong();

    }

    public void getNewSong() {
        songDurationSeconds = 0;
        seconds = 0;
        minutes = 0;
        String url = "http://spotocracy.net/get_song/";

        String playlistText = playlist.getText().toString();

        if (playlistText != null) {
            url += playlistText;
            Log.d("MainActivity", "Complete url: " + url);
            new GetSongByRestTask(this).execute(url);
        }
    }

    public void playNewSong(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String nextSong = jsonObject.getString("nextSong");
            Log.d("MainActivity", "Next song: " + nextSong);

            minutes = 0;
            seconds = 0;

            spotifyPlayerHandler.getPlayer().play(nextSong);
            playpausebutton.setImageResource(R.drawable.pause);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateSongMetadata() {
        spotifyPlayerHandler.getPlayer().getPlayerState(new UpdateMetaDataCallback(this));
    }

    public void updateSongInfo(String jsonFromWebApi) {
        Log.d("MainActivity", "Json: " + jsonFromWebApi);

        try {
            JSONObject jsonObject = new JSONObject(jsonFromWebApi);
            String artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
            String song = jsonObject.getString("name");
            songinfo.setText(artist + " - " + song);

            String urlString = jsonObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            Log.d("MainActivity", "Url of image: " + urlString);

            new DownloadImageTask(imageView).execute(urlString);
        }
        catch(JSONException jsonException) {
            Log.d("MainActivity", "This aint json, maybe? " + jsonException.getMessage());
        }
    }

    private class UpdateTime implements Runnable {

        @Override
        public void run() {
            if(spotifyPlayerHandler.getPlayer() != null) {
                spotifyPlayerHandler.getPlayer().getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {
                        if (playerState.playing) {

                            secondsPlayedTotal++;

                            int progress = (secondsPlayedTotal * 100 / songDurationSeconds);

                            Log.d("Time", "Updating progress status: " + progress);
                            songProgress.setProgress(progress);

                            seconds++;
                            if (seconds >= 60) {
                                seconds = 0;
                                minutes++;
                            }

                            timePlayed.setText(minutes + ":" + String.format("%02d", seconds));
                        }
                    }
                });
            }

            new android.os.Handler().postDelayed(new UpdateTime(), 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
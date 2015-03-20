package saiboten.no.spotifytestapp.callbacks;

import android.util.Log;

import com.spotify.sdk.android.playback.PlayerState;
import com.spotify.sdk.android.playback.PlayerStateCallback;

import saiboten.no.spotifytestapp.MainActivity;
import saiboten.no.spotifytestapp.tasks.GetSongInfoByRestTask;

/**
 * Created by Tobias on 15.03.2015.
 */
public class UpdateMetaDataCallback implements PlayerStateCallback {

    private MainActivity mainActivity;

    public UpdateMetaDataCallback(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onPlayerState(PlayerState playerState) {
        mainActivity.songDurationSeconds = playerState.durationInMs / 1000;
        int minutes = (playerState.durationInMs / 1000) / 60;
        int seconds = (playerState.durationInMs / 1000) % 60;

        mainActivity.songLength.setText(minutes + ":" + String.format("%02d", seconds));
        String trackId = playerState.trackUri.substring(14, playerState.trackUri.length());
        Log.d("MainActivity", "Track uri: " + trackId);

        new GetSongInfoByRestTask(mainActivity).execute("https://api.spotify.com/v1/tracks/" + trackId);
    }
}

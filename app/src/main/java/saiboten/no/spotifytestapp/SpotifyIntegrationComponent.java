package saiboten.no.spotifytestapp;

import android.util.Log;

import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

/**
 * Created by Tobias on 15.03.2015.
 */
public class SpotifyIntegrationComponent  implements
        PlayerNotificationCallback, ConnectionStateCallback {

    MainActivity mainActivity;

   public SpotifyIntegrationComponent(MainActivity mainActivity) {
       this.mainActivity = mainActivity;
   }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType);

        if(eventType.equals(EventType.TRACK_START)) {
            mainActivity.updateSongMetadata();
        }
        else if (eventType.equals(EventType.END_OF_CONTEXT)) {
            Log.d("MainActivity", "We are allowed to get a new song. Updating last time played time");
            mainActivity.getNewSong();
        }
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }
}
